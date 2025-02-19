package fr.iutrodez.tourneecommercial.utils.helper;

import org.osmdroid.util.BoundingBox;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

/**
 * Classe utilitaire pour la gestion des cartes et des marqueurs avec OSMdroid.
 */
public class MapHelper {
    private final MapView mapView;

    /**
     * Constructeur de MapHelper.
     *
     * @param mapView La vue de la carte sur laquelle agir.
     */
    public MapHelper(MapView mapView) {
        this.mapView = mapView;
    }

    /**
     * Ajoute ou met à jour un marqueur sur la carte.
     *
     * @param marker Le marqueur à afficher.
     * @param point  La position géographique du marqueur.
     * @param title  Le titre du marqueur.
     */
    public void drawMarker(Marker marker, GeoPoint point, String title) {
        mapView.getOverlays().remove(marker);
        marker.setPosition(point);
        marker.setTitle(title);
        mapView.getOverlays().add(marker);
    }

    /**
     * Supprime le marqueur sur la carte
     *
     * @param marker le marqueur à supprimer
     */
    public void dropMarker(Marker marker) {
        mapView.getOverlays().remove(marker);
    }

    /**
     * Ajuste le zoom de la carte pour englober deux points donnés.
     *
     * @param start Point de départ.
     * @param end   Point d'arrivée.
     */
    public void adjustZoomToMarkers(GeoPoint start, GeoPoint end) {
        double maxLat = Math.max(start.getLatitude(), end.getLatitude());
        double maxLon = Math.max(start.getLongitude(), end.getLongitude());
        double minLat = Math.min(start.getLatitude(), end.getLatitude());
        double minLon = Math.min(start.getLongitude(), end.getLongitude());
        double deltaLat = maxLat - minLat;
        double deltaLon = maxLon - minLon;
        double margin = 1 * Math.max(deltaLat, deltaLon);
        if (start != null && end != null) {
            BoundingBox boundingBox = new BoundingBox(maxLat + margin,
                    maxLon + margin,
                    minLat - margin,
                    minLon - margin
            );
            mapView.zoomToBoundingBox(boundingBox, true);
            mapView.invalidate();
        }
    }
}
