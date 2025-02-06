package fr.iutrodez.tourneecommercial.utils.helper;

import org.osmdroid.util.BoundingBox;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

public class MapHelper {
    private final MapView mapView;

    public MapHelper(MapView mapView) {
        this.mapView = mapView;
    }

    public void drawMarker(Marker marker, GeoPoint point, String title) {
        marker.setPosition(point);
        marker.setTitle(title);
        mapView.getOverlays().add(marker);
    }

    public void adjustZoomToMarkers(GeoPoint start, GeoPoint end) {
        if (start != null && end != null) {
            BoundingBox boundingBox = new BoundingBox(
                    Math.max(start.getLatitude(), end.getLatitude()) + 0.3,
                    Math.max(start.getLongitude(), end.getLongitude()) + 0.3,
                    Math.min(start.getLatitude(), end.getLatitude()) - 0.3,
                    Math.min(start.getLongitude(), end.getLongitude()) - 0.3
            );
            mapView.zoomToBoundingBox(boundingBox, true);
            mapView.invalidate();
        }
    }
}
