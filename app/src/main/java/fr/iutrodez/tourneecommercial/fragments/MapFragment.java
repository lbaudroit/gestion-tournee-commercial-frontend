package fr.iutrodez.tourneecommercial.fragments;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
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
import java.util.Objects;

import fr.iutrodez.tourneecommercial.MainActivity;
import fr.iutrodez.tourneecommercial.R;
import fr.iutrodez.tourneecommercial.modeles.Client;
import fr.iutrodez.tourneecommercial.utils.api.ApiRequest;
import fr.iutrodez.tourneecommercial.utils.helper.LocationHelper;
import fr.iutrodez.tourneecommercial.utils.helper.MapHelper;

/**
 * Fragment affichant une carte et permettant de suivre un itinéraire de clients.
 */
public class MapFragment extends Fragment {
    private MainActivity parent;
    private final ApiRequest API_REQUEST = ApiRequest.getInstance();
    private MapView mapView;
    private GeoPoint destinationPoint, pointDepart;
    private long itineraireId;
    private List<Client> clients;
    private int clientsIndex = 0;

    private Marker start, end;
    private LocationHelper locationHelper;
    private MapHelper mapHelper;

    private TextView companyName;
    private TextView companyAdresse;


    /**
     * Callback appelé à chaque mise à jour de la localisation.
     */
    private final LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            Location location = locationResult.getLastLocation();
            if (location != null) {
                boolean positionChanged = pointDepart == null
                        || pointDepart.getLongitude() != location.getLongitude()
                        || pointDepart.getLatitude() != location.getLatitude();
                //  Avant de placer un nouveau marker vérifie que notre position est bien différente
                //  que celle de l'ancien marker
                if( positionChanged){

                        pointDepart = new GeoPoint(location.getLatitude(), location.getLongitude());
                    mapHelper.drawMarker(start, pointDepart, "Ma Position");
                    centerView();

                }
                // ne place le marker que le marker client n'existe pas
                if (clients != null && destinationPoint == null) {
                    clientMarker();
                }
            }

        }

    };


    /**
     *
     */
    private void clientMarker(){
        destinationPoint = new GeoPoint(
                clients.get(clientsIndex).getCoordonnees().getLatitude(),
                clients.get(clientsIndex).getCoordonnees().getLongitude());

        companyAdresse.setText(clients.get(clientsIndex).getAdresse().toString());
        companyName.setText(clients.get(clientsIndex).getNomEntreprise());
        mapHelper.drawMarker(end, destinationPoint, "Point d'arrivée");
        mapHelper.adjustZoomToMarkers(pointDepart, destinationPoint);
    }

    /**
     * Attache le fragment au contexte de l'activité principale.
     * @param context Contexte de l'application.
     */
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        parent = (MainActivity) context;
        locationHelper = new LocationHelper(context);
    }

    /**
     * Crée et retourne la vue associée au fragment.
     * @param inflater LayoutInflater pour gonfler la vue.
     * @param container Vue parente.
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
        companyAdresse = frag.findViewById(R.id.client_company_adress);

        // Initialisation de la carte
        mapView = frag.findViewById(R.id.mapView);
        mapView.setTileSource(TileSourceFactory.MAPNIK);
        mapView.setMultiTouchControls(true);
        mapHelper = new MapHelper(mapView);

        start = new Marker(mapView);
        end = new Marker(mapView);

        // Gestion des permissions
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.ACCESS_FINE_LOCATION
            }, 1);
        }

        Button buttonVisit = frag.findViewById(R.id.btn_continue);
        buttonVisit.setOnClickListener(view -> markVisited());

        Button buttonCenter = frag.findViewById(R.id.btn_recenter);
        buttonCenter.setOnClickListener(view -> centerView());

        // Chargement de l'itinéraire
        prepareItineraireMap();

        return frag;
    }

    /**
     * Prépare la carte en récupérant les données de l'itinéraire.
     */
    private void prepareItineraireMap() {
        Bundle args = getArguments();
        if (args != null && args.containsKey("id")) {
            itineraireId = args.getLong("id");
            API_REQUEST.itineraire.getOne(parent, itineraireId, response -> {
                clients = response.getClients();
            }, error -> Log.e("MapFragment", "Erreur de récupération de l'itinéraire", error));
        } else {
            String message = "Aucun itinéraire sélectionné , voulez vous poursuivre sans itinéraire ?";
            new AlertDialog.Builder(getContext())
                    .setTitle(R.string.delete_route)
                    .setMessage(message)
                    .setPositiveButton(R.string.yes, (dialog, which) -> dialog.dismiss())
                    .setNegativeButton(R.string.no, (dialog, which) -> parent.navigateToNavbarItem(MainActivity.ITINERARY_FRAGMENT,false,args))
                    .show();

        }
    }

    /**
     * Marque le client actuel comme "visité" et passe au suivant.
     */
    private void markVisited() {
        destinationPoint = null;
        clientsIndex++;
        clientMarker();
        // Ajout d'une vérification que clientsIndex est dans les limites
    }

    /**
     * recentre la vue
     */
    private void centerView(){
        if(pointDepart != null){
            if(destinationPoint != null){
                mapHelper.adjustZoomToMarkers(pointDepart, destinationPoint);

            }else{
                mapHelper.adjustZoomToMarkers(pointDepart, pointDepart);

            }
        }

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
     * Arrête la mise à jour continue de la localisation pour économiser la batterie.
     */
    @Override
    public void onPause() {
        super.onPause();
        locationHelper.stopLocationUpdates(locationCallback);
        destinationPoint = null;
        pointDepart = null;
    }
}
