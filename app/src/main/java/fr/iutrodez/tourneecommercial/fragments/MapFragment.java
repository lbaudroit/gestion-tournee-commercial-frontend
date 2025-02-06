package fr.iutrodez.tourneecommercial.fragments;

import android.Manifest;
import android.content.Context;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;

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
import fr.iutrodez.tourneecommercial.utils.api.ApiRequest;
import fr.iutrodez.tourneecommercial.utils.helper.LocationHelper;
import fr.iutrodez.tourneecommercial.utils.helper.MapHelper;

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

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        parent = (MainActivity) context;
        locationHelper = new LocationHelper(context);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View frag = inflater.inflate(R.layout.map_fragment, container, false);
        Configuration.getInstance().setUserAgentValue(requireContext().getPackageName());

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

        // Initialisation des UI
        LinearLayout tableInfo = frag.findViewById(R.id.layoutOverlay);
        LinearLayout tvNoRoute = frag.findViewById(R.id.tv_no_route);
        ImageButton buttonVisit = frag.findViewById(R.id.btn_continue);
        buttonVisit.setOnClickListener(view -> markVisited());

        // Chargement de l'itinéraire
        prepareItineraireMap(tableInfo, tvNoRoute);
        getCurrentLocation();

        return frag;
    }

    private void prepareItineraireMap(LinearLayout tableInfo, LinearLayout tvNoRoute) {
        Bundle args = getArguments();
        if (args != null && args.containsKey("id") ) {
            itineraireId = args.getLong("id");
            API_REQUEST.itineraire.getOne(parent, itineraireId, response -> {
                clients = response.getClients();
            }, error -> Log.e("MapFragment", "Erreur de récupération de l'itinéraire", error));

        } else {
            tableInfo.setVisibility(View.GONE);
            tvNoRoute.setVisibility(View.VISIBLE);
        }
    }

    private void getCurrentLocation() {
        if (!locationHelper.checkPermissions()) {
            ActivityCompat.requestPermissions(parent, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            return;
        }

        locationHelper.getCurrentLocation(new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                Location location = locationResult.getLastLocation();
                if (location != null) {
                    pointDepart = new GeoPoint(location.getLatitude(), location.getLongitude());
                    mapHelper.drawMarker(start, pointDepart, "Ma Position");

                    if (clients != null) {
                        destinationPoint = new GeoPoint(clients.get(clientsIndex).getCoordonnees().getLatitude(),
                                clients.get(clientsIndex).getCoordonnees().getLongitude());


                        mapHelper.drawMarker(end, destinationPoint, "Point d'arrivée");
                        mapHelper.adjustZoomToMarkers(pointDepart, destinationPoint);
                    }
                }
            }
        });
    }

    private void markVisited() {
        destinationPoint = null;
        clientsIndex++;
        getCurrentLocation();
    }
}
