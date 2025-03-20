package fr.iutrodez.tourneecommercial.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import fr.iutrodez.tourneecommercial.MainActivity;
import fr.iutrodez.tourneecommercial.R;
import fr.iutrodez.tourneecommercial.model.Coordonnees;
import fr.iutrodez.tourneecommercial.model.Visit;
import fr.iutrodez.tourneecommercial.utils.FullscreenFetchStatusDisplay;
import fr.iutrodez.tourneecommercial.utils.adapter.ClientListCourseAdapter;
import fr.iutrodez.tourneecommercial.utils.api.ApiRequest;
import fr.iutrodez.tourneecommercial.utils.helper.MapHelper;
import org.jetbrains.annotations.NotNull;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static fr.iutrodez.tourneecommercial.utils.helper.ViewHelper.setVisibilityFor;

/**
 * Fragment affichant les détails d'un parcours.
 *
 * @author Benjamin NICOL, Enzo CLUZEL, Ahmed BRIBACH, Leïla BAUDROIT
 */
public class CourseFragment extends Fragment {

    public static final ApiRequest API_REQUEST = ApiRequest.getInstance();
    public MainActivity parent;
    private MapView map;
    private FullscreenFetchStatusDisplay status;
    private ClientListCourseAdapter listClients;
    private ListView listView;
    private TextView courseName;
    private TextView courseDate;
    private TextView courseHBegin;
    private TextView courseHEnd;
    private TextView courseDistance;
    private TextView courseDuration;
    private MapHelper mapHelper;

    @Override
    public void onAttach(@NotNull Context context) {
        super.onAttach(context);
        parent = (MainActivity) context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View frag = inflater.inflate(R.layout.course_view_fragment, container, false);

        listView = frag.findViewById(R.id.course_list_item_clients);

        courseName = frag.findViewById(R.id.course_name);
        courseDate = frag.findViewById(R.id.course_date);
        courseHBegin = frag.findViewById(R.id.course_hBegin);
        courseHEnd = frag.findViewById(R.id.course_hFin);
        courseDistance = frag.findViewById(R.id.course_distance);
        courseDuration = frag.findViewById(R.id.course_duration);
        status = frag.findViewById(R.id.fetchStatus_status);
        status.setShowContentFunction(() -> setContentVisibility(View.VISIBLE));
        status.setHideContentFunction(() -> setContentVisibility(View.GONE));
        map = frag.findViewById(R.id.mapView);

        mapHelper = new MapHelper(map);

        initializeMapView(map);

        // On récupère les arguments mis dans le fragment
        Bundle args = getArguments();

        // On vérifie si l'argument id existe
        if (args != null && args.containsKey("id")) {
            String idParcours = args.getString("id");
            // On récupère le client par rapport à l'id
            setParcours(idParcours);

        }
        return frag;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private void setContentVisibility(int visibility) {
        setVisibilityFor(visibility, listView);
    }

    /**
     * Méthode pour mettre en place le parcours sur le fragment.
     *
     * @param id L'id du parcours à mettre en place.
     */
    private void setParcours(String id) {
        API_REQUEST.parcours.getWithId(requireContext(), id, response -> {
                    courseName.setText(response.getNom());
                    courseDuration.setText(response.getDuree());
                    courseDate.setText(response.getDateDebut());
                    courseHBegin.setText(response.getHeureDebut());
                    courseHEnd.setText(response.getHeureFin());
                    courseDistance.setText(response.getDistance());


                    listClients = new ClientListCourseAdapter(parent, R.layout.course_clients_items, response.getVisitList(),
                            // Lors d'un clic sur un client de la liste on ajuste le zoom
                            this::zoomToClient);

                    listView.setAdapter(listClients);

                    loadMarkers();
                    loadPoints(response.getChemin());
                    zoomMap();
                },
                error -> status.error(R.string.fetch_course_error));
    }

    /**
     * Initialise la vue de la carte.
     *
     * @param mapView Vue de la carte.
     */
    private void initializeMapView(MapView mapView) {
        mapView.setTileSource(TileSourceFactory.MAPNIK);
        mapView.setMultiTouchControls(true);

    }

    /**
     * Ajoute sur la map les marqueurs des clients visités ou non en les différenciant avec deux icones différentes.
     */
    private void loadMarkers() {
        for (int i = 0; i < listClients.getCount(); i++) {
            Visit currentVisit = listClients.getItem(i);

            // création d'un nouveau marqueur pour chaque client
            Marker marker = new Marker(map);

            assert currentVisit != null;
            if (currentVisit.isVisited()) {
                marker.setIcon(ResourcesCompat.getDrawable(getResources(), R.drawable.baseline_flag_24, null));
            } else {
                marker.setIcon(ResourcesCompat.getDrawable(getResources(), R.drawable.baseline_flag_red, null));
            }

            mapHelper.drawMarker(marker, new GeoPoint(currentVisit.getCoordonnees().getLatitude(), currentVisit.getCoordonnees().getLongitude())
                    , currentVisit.getName());
        }
    }

    /**
     * Zoome sur la map pour afficher tous les points.
     */
    private void zoomMap() {
        ArrayList<GeoPoint> geoPoints = new ArrayList<>();
        for (int i = 0; i < listClients.getCount(); i++) {
            Coordonnees coordonnees = Objects.requireNonNull(listClients.getItem(i)).getCoordonnees();
            geoPoints.add(new GeoPoint(coordonnees.getLatitude(), coordonnees.getLongitude()));
        }
        mapHelper.adjustZoomToListMarkers(geoPoints);
    }

    /**
     * Ajuste le zoom de la map par rapport à un client sur la map.
     *
     * @param visit Le client sur la map.
     */
    private void zoomToClient(Visit visit) {
        mapHelper.adjustZoomToMarker(new GeoPoint(visit.getCoordonnees().getLatitude(), visit.getCoordonnees().getLongitude()));
    }

    /**
     * Charge les points d'une liste pour afficher le chemin effectué sur le parcours.
     *
     * @param pointList Le chemin effectué sur le parcours.
     */
    private void loadPoints(List<GeoPoint> pointList) {
        mapHelper.loadGeoPointsList(pointList);
    }
}
