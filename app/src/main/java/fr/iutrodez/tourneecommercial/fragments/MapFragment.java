package fr.iutrodez.tourneecommercial.fragments;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.location.Location;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationResult;

import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

import java.util.List;

import fr.iutrodez.tourneecommercial.MainActivity;
import fr.iutrodez.tourneecommercial.R;
import fr.iutrodez.tourneecommercial.modeles.Client;
import fr.iutrodez.tourneecommercial.modeles.Parcours;
import fr.iutrodez.tourneecommercial.modeles.Visit;
import fr.iutrodez.tourneecommercial.utils.api.ApiRequest;
import fr.iutrodez.tourneecommercial.utils.helper.LocationHelper;
import fr.iutrodez.tourneecommercial.utils.helper.MapHelper;
import nl.dionsegijn.konfetti.KonfettiView;
import nl.dionsegijn.konfetti.models.Shape;
import nl.dionsegijn.konfetti.models.Size;

/**
 * Fragment affichant une carte et permettant de suivre un itinéraire de clients.
 */
public class MapFragment extends Fragment implements NotificationHelper.NotificationListener {
    private MainActivity parent;
    private KonfettiView konfettiView;
    private final ApiRequest API_REQUEST = ApiRequest.getInstance();
    private MapView mapView;
    private GeoPoint destinationPoint, startPoint;
    private long itineraireId;
    private List<Client> clients;
    private int clientsIndex = 0;

    private Marker start, end;
    private NotificationHelper notificationHelper;
    private boolean gotNotificationForClient = false;
    List<Client> prospectNotified;
    private LocationHelper locationHelper;
    private MapHelper mapHelper;

    private TextView companyName;
    private TextView companyAdress;

    private Parcours parcours;

    private Button buttonVisit;

    private Button buttonPass;

    private boolean isParcoursFinished;

    private Button buttonPause;
    private Button buttonStop;

    /**
     * Attache le fragment au contexte de l'activité principale.
     *
     * @param context Contexte de l'application.
     */
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        parent = (MainActivity) context;
        locationHelper = new LocationHelper(context);
        notificationHelper = new NotificationHelper(context, this);
    }

    /**
     * Crée et retourne la vue associée au fragment.
     *
     * @param inflater           LayoutInflater pour gonfler la vue.
     * @param container          Vue parente.
     * @param savedInstanceState État précédent du fragment.
     * @return Vue créée.
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View frag = inflater.inflate(R.layout.map_fragment_2, container, false);
        Configuration.getInstance().setUserAgentValue(requireContext().getPackageName());

        companyName = frag.findViewById(R.id.client_company_name);
        companyAdress = frag.findViewById(R.id.client_company_adress);
        konfettiView = frag.findViewById(R.id.viewKonfetti);
        buttonVisit = frag.findViewById(R.id.btn_continue);
        buttonPass = frag.findViewById(R.id.btn_pass);
        buttonPause = frag.findViewById(R.id.btn_pause);
        buttonStop = frag.findViewById(R.id.btn_stop);
        Button buttonCenter = frag.findViewById(R.id.btn_recenter);
        MapView mapView = frag.findViewById(R.id.mapView);

        initializeMapView(mapView);
        initializeStartEnd(mapView);
        initializeParcours();
        initializeItineraryMap();

        buttonVisit.setOnClickListener(view -> markVisited());
        buttonPass.setOnClickListener(view -> pass());
        buttonPause.setOnClickListener(view -> pause());
        buttonStop.setOnClickListener(view -> stop());
        buttonCenter.setOnClickListener(view -> centerView());

        return frag;
    }

    /**
     * Arrête la mise à jour continue de la localisation pour économiser la batterie.
     */
    @Override
    public void onPause() {
        super.onPause();
        locationHelper.stopLocationUpdates(locationCallback);
        destinationPoint = null;
        startPoint = null;
    }

    /**
     * Démarre la mise à jour continue de la localisation lorsque le fragment est visible.
     */
    @Override
    public void onResume() {
        super.onResume();
        if (!locationHelper.checkPermissions()) {
            ActivityCompat.requestPermissions(parent, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            return;
        }
        locationHelper.startContinuousLocationUpdates(locationCallback);
    }

    /**
     * Initialise la vue de la carte.
     *
     * @param mapView Vue de la carte.
     */
    private void initializeMapView(MapView mapView) {
        mapView.setTileSource(TileSourceFactory.MAPNIK);
        mapView.setMultiTouchControls(true);
        mapHelper = new MapHelper(mapView);
    }

    /**
     * Initialise les marqueurs de début et de fin.
     *
     * @param mapView Vue de la carte.
     */
    private void initializeStartEnd(MapView mapView) {
        start = new Marker(mapView);
        start.setIcon(ResourcesCompat.getDrawable(getResources(), R.drawable.baseline_my_location_24, null));

        end = new Marker(mapView);
        end.setIcon(ResourcesCompat.getDrawable(getResources(), R.drawable.baseline_flag_24, null));
    }

    /**
     * Initialise le parcours.
     */
    private void initializeParcours() {
        if (parcours == null) {
            parcours = new Parcours();
        } else {
            if (isParcoursFinished) {
                removeButtons();
                startConfetti();
                mapHelper.dropMarker(end);

            }
        }
    }

    /**
     * Prépare la carte en récupérant les données de l'itinéraire.
     */
    private void initializeItineraryMap() {
        Bundle args = getArguments();
        if (args != null && args.containsKey("id")) {
            long itineraireId = args.getLong("id");
            parcours.setName(args.getString("name"));
            API_REQUEST.itineraire.getOne(parent, itineraireId, response -> clients = response.getClients(),
                    error -> Log.e("MapFragment", "Erreur de récupération de l'itinéraire", error));
        } else {
            handleNoItinerary();
        }
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
        removeButtons();
    }

    /**
     * Affiche ou cache les boutons.
     */
    private void removeButtons() {
        buttonVisit.setVisibility(View.GONE);
        buttonPass.setVisibility(View.GONE);
        buttonPause.setVisibility(View.GONE);
        buttonStop.setVisibility(View.GONE);
    }

    /**
     * Callback appelé à chaque mise à jour de la localisation.
     */
    private final LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            Location location = locationResult.getLastLocation();
            if (location != null) {
                boolean positionChanged = startPoint == null
                        || startPoint.getLongitude() != location.getLongitude()
                        || startPoint.getLatitude() != location.getLatitude();
                if (positionChanged) {
                    startPoint = new GeoPoint(location.getLatitude(), location.getLongitude());
                    mapHelper.drawMarker(start, startPoint, getString(R.string.start_point));
                    centerView();
                }
                // ne place le marker que si le marker client n'existe pas
                if (clients != null && destinationPoint == null && !isParcoursFinished) {
                    clientMarker();
                }
            }

        }

    };

    /**
     * Place un marqueur pour le client actuel.
     */
    private void clientMarker() {
        destinationPoint = new GeoPoint(
                clients.get(clientsIndex).getCoordonnees().getLatitude(),
                clients.get(clientsIndex).getCoordonnees().getLongitude());

        companyAdress.setText(clients.get(clientsIndex).getAdresse().toString());
        companyName.setText(clients.get(clientsIndex).getNomEntreprise());
        mapHelper.drawMarker(end, destinationPoint, "Point d'arrivée");
        mapHelper.adjustZoomToMarkers(startPoint, destinationPoint);
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
    private void markVisited() {
        goToNext(true);
        // Ajout d'une vérification que clientsIndex est dans les limites
    }

    /**
     * Passe au client suivant.
     *
     * @param visit Indique si le client a été visité.
     */
    private void goToNext(boolean visit) {
        parcours.addVisite(new Visit(clients.get(clientsIndex), visit));
        destinationPoint = null;
        if (clientsIndex + 1 == clients.size()) {
            finish();
        } else {
            clientsIndex++;
            clientMarker();
        }
    }

    /**
     * Termine le parcours.
     */
    private void finish() {
        enregistrerParcours();
        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Ringtone ringtone = RingtoneManager.getRingtone(requireContext(), notification);
        ringtone.play();
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            if (ringtone.isPlaying()) {
                ringtone.stop();
            }
        }, 2000);
        new AlertDialog.Builder(requireContext())
                .setTitle(getString(R.string.destination_done))
                .setMessage(getString(R.string.message_destination))
                .setPositiveButton("OK", (dialog, which) -> {
                    dialog.dismiss();
                    startConfetti();
                })
                .show();
        parent.clearCache(MainActivity.MAP_FRAGMENT);
    }

    private void pass() {
        goToNext(false);
    }

    /**
     * Centre la vue sur les marqueurs.
     */
    private void centerView() {
        if (startPoint != null) {
            if (destinationPoint != null) {
                mapHelper.adjustZoomToMarkers(startPoint, destinationPoint);
            } else {
                mapHelper.adjustZoomToMarkers(startPoint, startPoint);
            }
        }
    }

    private void pause() {
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

    /**
     * Enregistre le parcours.
     */
    public void enregistrerParcours() {
        API_REQUEST.parcours.create(getContext(), parcours, System.out::println
                , error -> System.out.println(error.getMessage()));

        removeButtons();
        companyAdress.setVisibility(View.GONE);
        companyName.setText(R.string.route_finished);
        mapHelper.dropMarker(end);
        isParcoursFinished = true;
    }

    /**
     * Appelé lorsque au moins un prospect est à moins de 1 Km.
     */
    @Override
    public void onProspectNotification(List<Client> prospects) {
        System.out.println(prospects.size());
        StringBuilder message = new StringBuilder();
        for (Client prospect : prospects) {
            if (!(prospectNotified.contains(prospect) || clients.contains(prospect))) {
                prospectNotified.add(prospect);
                message.append(getString(R.string.prospect_nearby_sub_message, prospect.getNomEntreprise(),
                        prospect.getAdresse(), prospect.getContact().getNumeroTelephone()));
            }
        }
        if (message.length() > 0) {
            triggerNotification(getString(R.string.prospect_nearby_message), message.toString());
        }
    }

    /**
     * Appelé lorsque le client est à moins de 200 mètres.
     * La notification ressemble à ceci :
     * "Vous êtes à moins de 200 mètres du client :
     * Nom de l'entreprise
     * Numéro de téléphone : xx xx xx xx xx".
     */
    @Override
    public void onClientNotification() {
        if (!gotNotificationForClient) {
            gotNotificationForClient = true;
            String message = getString(R.string.client_notification_message,
                    clients.get(clientsIndex).getNomEntreprise(),
                    clients.get(clientsIndex).getContact().getNumeroTelephone());
            triggerNotification(getString(R.string.destination_reached), message);
        }
    }

    /**
     * Affiche un pop up de notification.
     * Celui-ci joue un son et affiche un message pendant 10 secondes.
     * L'utilisateur est informé toutes les secondes du temps restant.
     * L'utilisateur peut également fermer la notification manuellement en appuyant sur le bouton "Fermer".
     *
     * @param contenue le message à afficher
     */
    private void triggerNotification(String title, String contenue) {
        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Ringtone ringtone = RingtoneManager.getRingtone(requireContext(), notification);
        ringtone.play();
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            if (ringtone.isPlaying()) {
                ringtone.stop();
            }
        }, 2000);
        AlertDialog dialog = new AlertDialog.Builder(requireContext())
                .setTitle(title)
                .setMessage(contenue)
                .setPositiveButton("Fermer", (dialogInterface, which) -> dialogInterface.dismiss())
                .create();
        dialog.show();
        final Handler handler = new Handler(Looper.getMainLooper());
        final int[] secondsLeft = {20};
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                if (secondsLeft[0] > 0) {
                    dialog.setMessage(contenue + "\n" + getString(R.string.time_remaining, secondsLeft[0]));
                    secondsLeft[0]--;
                    handler.postDelayed(this, 1000);
                } else {
                    dialog.dismiss();
                }
            }
        };
        handler.post(runnable);
    }
}
