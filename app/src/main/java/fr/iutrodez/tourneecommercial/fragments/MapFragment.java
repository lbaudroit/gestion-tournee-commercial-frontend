package fr.iutrodez.tourneecommercial.fragments;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationResult;
import fr.iutrodez.tourneecommercial.MainActivity;
import fr.iutrodez.tourneecommercial.R;
import fr.iutrodez.tourneecommercial.model.Client;
import fr.iutrodez.tourneecommercial.model.Coordonnees;
import fr.iutrodez.tourneecommercial.model.Parcours;
import fr.iutrodez.tourneecommercial.utils.api.ApiRequest;
import fr.iutrodez.tourneecommercial.utils.helper.*;
import nl.dionsegijn.konfetti.KonfettiView;
import nl.dionsegijn.konfetti.models.Shape;
import nl.dionsegijn.konfetti.models.Size;
import org.osmdroid.config.Configuration;
import org.osmdroid.events.MapAdapter;
import org.osmdroid.events.ScrollEvent;
import org.osmdroid.events.ZoomEvent;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Fragment affichant une carte et permettant de suivre un itinéraire de clients.
 *
 * @author Benjamin NICOL, Enzo CLUZEL, Ahmed BRIBACH, Leïla BAUDROIT
 */
public class MapFragment extends Fragment implements NotificationHelper.NotificationListener {
    private final ApiRequest API_REQUEST = ApiRequest.getInstance();
    final List<Client> prospectNotified = new ArrayList<>();
    private MainActivity parent;
    private KonfettiView konfettiView;
    private GeoPoint destinationPoint, startPoint;
    private Marker start, end;
    private boolean gotNotificationForClient = false;
    private NotificationHelper notificationHelper;
    private LocationHelper locationHelper;
    private MapHelper mapHelper;
    private SavedParcoursHelper savedParcoursHelper;
    private boolean userInteracted = false;
    private boolean isUserInteraction = false;
    private boolean isPaused = false;
    private Parcours parcours;
    private TextView companyName;
    private TextView companyAddress;
    private TextView companyType;
    private Button buttonVisit;
    private Button buttonPass;
    private boolean isParcoursFinished;
    private Button buttonPause;
    private Button buttonStop;
    /**
     * Callback appelé à chaque mise à jour de la localisation.
     */
    private final LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(@NonNull LocationResult locationResult) {
            if (!buttonVisit.isEnabled()) {
                unlockButtons();
            }
            Location location = locationResult.getLastLocation();
            if (location != null && (startPoint == null
                    || startPoint.getLongitude() != location.getLongitude()
                    || startPoint.getLatitude() != location.getLatitude())) {
                handlePathAndNotifications(location);
                mapHelper.drawMarker(start, startPoint, getString(R.string.start_point));
            }
        }
    };

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        parent = (MainActivity) context;
        locationHelper = new LocationHelper(context);
        notificationHelper = new NotificationHelper(context, this);
        savedParcoursHelper = new SavedParcoursHelper(context);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View frag = inflater.inflate(R.layout.map_fragment, container, false);
        Configuration.getInstance().setUserAgentValue(requireContext().getPackageName());

        companyName = frag.findViewById(R.id.client_company_name);
        companyAddress = frag.findViewById(R.id.client_company_address);
        companyType = frag.findViewById(R.id.client_company_type);
        konfettiView = frag.findViewById(R.id.viewKonfetti);
        buttonVisit = frag.findViewById(R.id.btn_continue);
        buttonPass = frag.findViewById(R.id.btn_pass);
        buttonPause = frag.findViewById(R.id.btn_pause);
        buttonStop = frag.findViewById(R.id.btn_stop);
        Button buttonCenter = frag.findViewById(R.id.btn_recenter);
        MapView mapView = frag.findViewById(R.id.mapView);

        initializeMapView(mapView);
        initializeStartEnd(mapView);
        initializeItineraryMap();

        buttonVisit.setOnClickListener(view -> markVisitedAndGoToNext());
        buttonPass.setOnClickListener(view -> markPassedAndGoToNext());
        buttonPause.setOnLongClickListener(view -> {
            pausePressed();
            animateButtonWhileHold(view);
            return true;
        });

        buttonStop.setOnLongClickListener(view -> {
            stop();
            animateButtonWhileHold(view);
            return true;
        });
        buttonCenter.setOnClickListener(view -> centerButtonPressed());
        return frag;
    }

    /**
     * Anime un bouton en réduisant sa taille puis en la restaurant.
     *
     * @param view La vue du bouton à animer.
     */
    private void animateButtonWhileHold(View view) {
        ObjectAnimator scaleDown = ObjectAnimator.ofPropertyValuesHolder(
                view,
                PropertyValuesHolder.ofFloat("scaleX", 0.9f),
                PropertyValuesHolder.ofFloat("scaleY", 0.9f));
        scaleDown.setDuration(300);
        scaleDown.setRepeatCount(1);
        scaleDown.setRepeatMode(ObjectAnimator.REVERSE);
        scaleDown.setInterpolator(new AccelerateDecelerateInterpolator());
        scaleDown.start();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (parcours != null && !isParcoursFinished) {
            savedParcoursHelper.serializeParcours(parcours);
        }
        Log.d("MapFragment", "onPause called");
        locationHelper.stopLocationUpdates(locationCallback);
        destinationPoint = null;
        startPoint = null;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (locationHelper.checkPermissions()) {
            ActivityCompat.requestPermissions(parent, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            return;
        }
        locationHelper.startContinuousLocationUpdates(locationCallback);
    }

    @Override
    public void onProspectNotification(List<Client> prospects) {
        StringBuilder message = new StringBuilder();
        for (Client prospect : prospects) {
            if (!(prospectNotified.contains(prospect) || parcours.clientInItinerary(prospect))) {
                prospectNotified.add(prospect);
                message.append(getString(R.string.prospect_nearby_sub_message, prospect.getNomEntreprise(),
                        prospect.getAdresse(), prospect.getContact().getNumeroTelephone()));
            }
        }
        if (message.length() > 0) {
            notificationHelper.triggerNotification(getString(R.string.prospect_nearby), getString(R.string.prospect_nearby_message, message.toString()));
        }
    }


    /**
     * Enregistre le parcours.
     */
    private void enregistrerParcours() {
        savedParcoursHelper.deleteSavedParcours();
        parcours.registerAndSaveItineraire(requireContext());
        hideButtonsAndTextView();
        mapHelper.dropMarker(end);
        isParcoursFinished = true;
        parent.clearCache(MainActivity.MAP_FRAGMENT);
    }

    /**
     * Appelé lorsque le client est à moins de 200 mètres.
     * La notification ressemble à ceci :
     * "Vous êtes à moins de 200 mètres du client :
     * Nom de l'entreprise
     * Numéro de téléphone : xx xx xx xx xx".
     */
    private void onClientNotification() {
        if (!gotNotificationForClient) {
            gotNotificationForClient = true;
            String message = getString(R.string.client_notification_message,
                    parcours.getCurrentClientName(), parcours.getCurrentClientPhoneNumber());
            notificationHelper.triggerNotification(getString(R.string.destination_reached), message);
        }
    }

    /**
     * /**
     * Initialise la vue de la carte.
     *
     * @param mapView Vue de la carte.
     */
    @SuppressLint("ClickableViewAccessibility")
    private void initializeMapView(MapView mapView) {
        mapView.setTileSource(TileSourceFactory.MAPNIK);
        mapView.setMultiTouchControls(true);
        mapHelper = new MapHelper(mapView);

        mapView.setOnTouchListener(
                (v, event) -> {
                    isUserInteraction = event.getAction() == android.view.MotionEvent.ACTION_DOWN || event.getAction() == android.view.MotionEvent.ACTION_MOVE;
                    if (event.getAction() == android.view.MotionEvent.ACTION_UP) {
                        v.performClick();
                    }
                    return false;
                });

        mapView.addMapListener(new MapAdapter() {
            @Override
            public boolean onScroll(ScrollEvent event) {
                if (isUserInteraction) {
                    userInteracted = true;
                }
                return super.onScroll(event);
            }

            @Override
            public boolean onZoom(ZoomEvent event) {
                if (isUserInteraction) {
                    userInteracted = true;
                }
                return super.onZoom(event);
            }
        });
    }

    /**
     * Initialise les marqueurs de début et de fin.
     *
     * @param mapView Vue de la carte.
     */
    private void initializeStartEnd(MapView mapView) {
        start = new Marker(mapView);
        start.setIcon(ResourcesCompat.getDrawable(getResources(), R.drawable.baseline_my_location_24, null));
        start.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER);
        end = new Marker(mapView);
        end.setIcon(ResourcesCompat.getDrawable(getResources(), R.drawable.baseline_flag_24, null));
    }

    /**
     * Si un fichier de données sérialisées existe, propose de le charger.
     * Si un fichier de données sérialisées n'existe pas, initialise une nouvelle carte.
     * Si un fichier de données sérialisées existe et l'utilisateur accepte de le charger, le charge.
     * Si un fichier de données sérialisées existe et l'utilisateur refuse de le charger,
     * enregistre l'ancien et initialise une nouvelle carte.
     */
    private void initializeItineraryMap() {
        File file = savedParcoursHelper.getFileForLocalSave();
        if (parcours == null) {
            if (file != null && file.exists()) {
                handleItineraryFoundInStorage();
            } else {
                initializeNewItineraryMap();
            }
        }
    }

    /**
     * Gère le cas où un itinéraire sérialisé est trouvé dans le stockage.
     * Affiche une boîte de dialogue pour demander à l'utilisateur s'il souhaite charger l'itinéraire précédent.
     * Si l'utilisateur accepte, l'itinéraire sérialisé est chargé.
     * Si l'utilisateur refuse, l'itinéraire précédent est enregistré et un nouvel itinéraire est initialisé.
     */
    private void handleItineraryFoundInStorage() {
        new AlertDialog.Builder(getContext())
                .setTitle("Charger l'itinéraire")
                .setMessage("Voulez-vous charger l'itinéraire précédent ? Si vous ne voulez pas, l'itinéraire précédent sera terminé et enregistré.")
                .setPositiveButton("Oui", (dialog, which) -> initializeSerializedItineraryMap())
                .setNegativeButton("Non", (dialog, which) -> {
                    Parcours tmp = savedParcoursHelper.deserializeSavedParcours();
                    assert tmp != null;
                    tmp.registerAndSaveItineraire(requireContext());
                    savedParcoursHelper.deleteSavedParcours();
                    initializeNewItineraryMap();
                })
                .show();
    }

    /**
     * Prépare la carte en récupérant les données de l'itinéraire.
     */
    private void initializeNewItineraryMap() {
        Bundle args = getArguments();
        if (args != null && args.containsKey("id")) {
            long itineraireId = args.getLong("id");
            API_REQUEST.itineraire.getOne(parent, itineraireId, response -> {
                List<Client> clients = response.getClients();
                parcours = new Parcours(args.getString("name"), clients);
                mapHelper.loadGeoPointsList(parcours.getPath());
            }, error -> Log.e("MapFragment", "Erreur de récupération de l'itinéraire", error));
        } else {
            handleNoItinerary();
        }
    }

    /**
     * Prépare la carte en récupérant les données sérailisées de l'itinéraire.
     */
    private void initializeSerializedItineraryMap() {
        parcours = savedParcoursHelper.deserializeSavedParcours();
        mapHelper.loadGeoPointsList(parcours.getPath());

        locationHelper.getCurrentLocation(locationCallback);
        clientMarker();
        if (parcours == null) {
            new AlertDialog.Builder(getContext())
                    .setTitle("Erreur")
                    .setMessage("Erreur lors de la récupération des données de l'itinéraire")
                    .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                    .show();
        }
    }

    /**
     * Déverrouille les boutons de visite, de passage, de pause et d'arrêt.
     */
    private void unlockButtons() {
        ViewHelper.enableView(buttonVisit);
        ViewHelper.enableView(buttonPass);
        ViewHelper.enableView(buttonPause);
        ViewHelper.enableView(buttonStop);
    }

    /**
     * Gère le cas où il n'y a pas d'itinéraire.
     */
    private void handleNoItinerary() {
        new AlertDialog.Builder(getContext())
                .setTitle(R.string.no_itinerary)
                .setMessage(R.string.stay_without_itinerary)
                .setPositiveButton(R.string.yes, (dialog, which) -> dialog.dismiss())
                .setNegativeButton(R.string.no, (dialog, which) -> parent.navigateToNavbarItem(MainActivity.ITINERARY_FRAGMENT, false))
                .show();
        hideButtonsAndTextView();
    }

    /**
     * Affiche ou cache les boutons.
     */
    private void hideButtonsAndTextView() {
        ViewHelper.setVisibilityFor(View.GONE, buttonVisit, buttonPass, buttonPause, buttonStop, companyName, companyAddress, companyType);
    }

    /**
     * Gère le chemin et les notifications en fonction de la localisation actuelle.
     *
     * @param location La localisation actuelle.
     */
    private void handlePathAndNotifications(Location location) {
        startPoint = new GeoPoint(location.getLatitude(), location.getLongitude());
        if (isItineraryRunning()) {
            Coordonnees coordinates = new Coordonnees(location.getLatitude(), location.getLongitude());
            if (mapHelper.isLastRecordedCoordinates20MetersAway(coordinates)) {
                mapHelper.addNewGeoPoint(new GeoPoint(coordinates.getLatitude(), coordinates.getLongitude()));
                parcours.addPointToPath(new GeoPoint(coordinates.getLatitude(), coordinates.getLongitude()));
            }
            if (mapHelper.isLastRecordedCoordinates200MetersAway(coordinates)) {
                notificationHelper.checkProspectNotification(coordinates);
            }
            if (mapHelper.isClientWithin200Meters(coordinates, parcours.getCurrentClientCoordonnees())) {
                onClientNotification();
            }
            if (destinationPoint == null) {
                clientMarker();
            }
        }
        centerView();
    }

    /**
     * Vérifie si l'itinéraire est en cours d'exécution.
     *
     * @return true si l'itinéraire est en cours d'exécution, false sinon.
     */
    private boolean isItineraryRunning() {
        return parcours != null && !isParcoursFinished && !isPaused;
    }

    /**
     * Gère l'appui sur le bouton de pause.
     * Inverse l'état de pause et met à jour le texte du bouton.
     * Arrête ou démarre les mises à jour de localisation en fonction de l'état de pause.
     */
    private void pausePressed() {
        isPaused = !isPaused;
        if (isPaused) {
            ViewHelper.disableView(buttonVisit);
            ViewHelper.disableView(buttonPass);
            buttonPause.setText(R.string.resumeItinerary);
            locationHelper.stopLocationUpdates(locationCallback);
        } else {
            ViewHelper.enableView(buttonVisit);
            ViewHelper.enableView(buttonPass);
            buttonPause.setText(R.string.pause);
            locationHelper.startContinuousLocationUpdates(locationCallback);
        }
    }

    /**
     * Place un marqueur pour le client actuel.
     */
    private void clientMarker() {
        destinationPoint = parcours.getCurrentClientGeoPoint();

        companyAddress.setText(parcours.getCurrentAddress());
        companyName.setText(parcours.getCurrentClientName());
        companyType.setText(getString(R.string.type, parcours.getCurrentType()));
        if (parcours.isCurrentCLientEffectif()) {
            end.setIcon(ResourcesCompat.getDrawable(getResources(), R.drawable.baseline_flag_24, null));
        } else {
            end.setIcon(ResourcesCompat.getDrawable(getResources(), R.drawable.baseline_flag_orange, null));
        }
        end.setPosition(destinationPoint);
        end.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        end.setTitle(parcours.getCurrentClientName());
        end.setSnippet(parcours.getCurrentAddress());
        end.showInfoWindow();
        mapHelper.drawMarker(end, destinationPoint, "Point d'arrivée");
        if (startPoint != null) {
            mapHelper.adjustZoomToMarkers(startPoint, destinationPoint);
        }
    }

    /**
     * Démarre l'animation de confettis.
     */
    private void startConfetti() {
        konfettiView.build()
                .addColors(Color.YELLOW, Color.GREEN, Color.MAGENTA)
                .setDirection(0.0, 359.0)
                .setSpeed(1f, 5f)
                .setFadeOutEnabled(true)
                .setTimeToLive(2000L)
                .addShapes(Shape.Square.INSTANCE, Shape.Circle.INSTANCE)
                .addSizes(new Size(12, 5f))
                .setPosition(-50f, konfettiView.getWidth() + 50f, -50f, -50f)
                .streamFor(300, 5000L);
    }


    /**
     * Marque le client actuel comme "visité" et passe au suivant.
     */
    private void markVisitedAndGoToNext() {
        if (parcours.markCurrentAsVisitedAndMoveToNext()) {
            gotNotificationForClient = false;
            mapHelper.dropMarker(end);
            clientMarker();
        } else {
            destinationPoint = null;
            centerView();
            finish();
        }
    }

    /**
     * Passe au client suivant sans le marquer comme visité.
     */
    private void markPassedAndGoToNext() {
        if (parcours.markCurrentAsNotVisitedAndMoveToNext()) {
            gotNotificationForClient = false;
            mapHelper.dropMarker(end);
            clientMarker();
        } else {
            destinationPoint = null;
            centerView();
            finish();
        }
    }

    /**
     * Termine le parcours.
     */
    private void finish() {
        enregistrerParcours();
        notificationHelper.playNotificationSound();
        new AlertDialog.Builder(requireContext())
                .setTitle(getString(R.string.destination_done))
                .setMessage(getString(R.string.message_destination))
                .setPositiveButton("OK", (dialog, which) -> {
                    dialog.dismiss();
                    startConfetti();
                })
                .show();
    }

    /**
     * Centre la vue sur la carte lorsque le bouton de recentrage est pressé.
     * Réinitialise l'état d'interaction de l'utilisateur.
     */
    private void centerButtonPressed() {
        userInteracted = false;
        centerView();
    }

    /**
     * Centre la vue sur les marqueurs.
     */
    private void centerView() {
        if (startPoint != null && !userInteracted) {
            if (destinationPoint != null) {
                mapHelper.adjustZoomToMarkers(startPoint, destinationPoint);
            } else {
                mapHelper.adjustZoomToMarker(startPoint);
            }
        } else {
            mapHelper.updateMap();
        }
    }

    /**
     * Affiche une boîte de dialogue pour confirmer l'arrêt du parcours.
     */
    private void stop() {
        new AlertDialog.Builder(getContext())
                .setTitle("Arrêter le parcours")
                .setMessage("êtes vous sur de vouloir arrêter le parcours")
                .setPositiveButton(R.string.yes, (dialog, which) -> enregistrerParcours())
                .setNegativeButton(R.string.no, (dialog, which) -> dialog.dismiss())
                .show();
    }
}