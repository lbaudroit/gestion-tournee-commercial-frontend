package fr.iutrodez.tourneecommercial.fragments;

import static fr.iutrodez.tourneecommercial.utils.helper.ViewHelper.setVisibilityFor;

import android.annotation.SuppressLint;
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

import org.jetbrains.annotations.NotNull;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

import java.util.List;

import fr.iutrodez.tourneecommercial.MainActivity;
import fr.iutrodez.tourneecommercial.R;
import fr.iutrodez.tourneecommercial.model.Visit;
import fr.iutrodez.tourneecommercial.utils.FullscreenFetchStatusDisplay;
import fr.iutrodez.tourneecommercial.utils.adapter.ClientListCourseAdapter;
import fr.iutrodez.tourneecommercial.utils.api.ApiRequest;
import fr.iutrodez.tourneecommercial.utils.helper.MapHelper;

public class CourseFragment extends Fragment {

    public MainActivity parent;

    private Marker point;
    private FullscreenFetchStatusDisplay status;
    private ClientListCourseAdapter listClients;

    private ListView listView;

    private TextView courseName;

    private TextView courseDate;

    private View view;

    private TextView courseHBegin;

    private TextView courseHEnd;

    private TextView courseDistance;

    private TextView courseDuration;

    private MapHelper mapHelper;

    public static final ApiRequest API_REQUEST = ApiRequest.getInstance();
    @Override
    public void onAttach(@NotNull Context context) {
        super.onAttach(context);
        parent = (MainActivity) context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View frag = inflater.inflate(R.layout.course_view_fragment, container, false);
        view = frag;

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
        MapView mapView = frag.findViewById(R.id.mapView);

        mapHelper = new MapHelper(mapView);

        initializeMapView(mapView);
        initializePoints(mapView);

        // On récupère les arguments mis dans le fragment
        Bundle args = getArguments();

        // On vérifie si l'argument id existe
        if (args != null && args.containsKey("id")) {
            String idParcours = args.getString("id");
            // On récupère le client par rapport à l'id
            getParcours(idParcours);

        }
        return frag;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public void setContentVisibility(int visibility) {
        setVisibilityFor(visibility,listView);
    }
    private void getParcours(String id) {
        API_REQUEST.parcours.getWithId(requireContext(),id,response -> {
                    courseName.setText(response.getNom());
                    courseDuration.setText(response.getDuree());
                    courseDate.setText(response.getDateDebut());
                    courseHBegin.setText(response.getHeureDebut());
                    courseHEnd.setText(response.getHeureFin());
                    courseDistance.setText(response.getDistance());
                    listClients = new ClientListCourseAdapter(parent,R.layout.course_clients_items,response.getVisitList(),
                            new ClientListCourseAdapter.OnClickList() {
                                @Override
                                public void OnClick(Visit visit) {
                                    mapHelper.adjustZoomToMarker(new GeoPoint(visit.getCoordonnees().getLatitude(),visit.getCoordonnees().getLongitude()));
                                }
                            });
                    listView.setAdapter(listClients);

                    loadMarkers();
                    loadPoints(response.getChemin());
                },
                error -> status.error(R.string.fetch_course_error));
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initializeMapView(MapView mapView) {
        mapView.setTileSource(TileSourceFactory.MAPNIK);
        mapView.setMultiTouchControls(true);

    }

    private void initializePoints(MapView mapView) {
         point = new Marker(mapView);
        point.setIcon(ResourcesCompat.getDrawable(getResources(), R.drawable.baseline_flag_24, null));
    }

    private void loadMarkers() {
        for(int i =0 ; i < listClients.getCount();i++) {
            Visit currentVisit = listClients.getItem(i);
            Marker marker = new Marker(point.getInfoWindow().getMapView());
            if(currentVisit.isVisited()) {
                marker.setIcon(ResourcesCompat.getDrawable(getResources(), R.drawable.baseline_flag_24, null));
            } else {
                marker.setIcon(ResourcesCompat.getDrawable(getResources(), R.drawable.person, null));
            }

            mapHelper.drawMarker(marker,new GeoPoint(currentVisit.getCoordonnees().getLatitude(),currentVisit.getCoordonnees().getLongitude())
                    ,currentVisit.getName());
            mapHelper.adjustZoomToMarker(new GeoPoint(currentVisit.getCoordonnees().getLatitude(),currentVisit.getCoordonnees().getLongitude()));
        }
    }

    private void loadPoints(List<GeoPoint> pointList) {
        mapHelper.loadGeoPointsList(pointList);
    }
}
