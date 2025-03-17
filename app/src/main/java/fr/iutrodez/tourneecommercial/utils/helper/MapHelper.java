package fr.iutrodez.tourneecommercial.utils.helper;

import android.content.Context;
import android.graphics.Paint;
import android.util.Log;
import fr.iutrodez.tourneecommercial.model.Coordonnees;
import fr.iutrodez.tourneecommercial.model.Parcours;
import org.osmdroid.util.BoundingBox;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Polyline;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Classe utilitaire pour la gestion des cartes et des marqueurs avec OSMdroid.
 */
public class MapHelper {
    private Coordonnees lastChangedCoordinates20Meters;
    private Coordonnees lastChangedCoordinates200Meters;
    private final MapView mapView;
    private Polyline latestPolyline;

    /**
     * Constructeur de MapHelper.
     *
     * @param mapView La vue de la carte sur laquelle agir.
     */
    public MapHelper(MapView mapView) {
        this.mapView = mapView;
        this.latestPolyline = createAndAddNormalPolylineToMap(new ArrayList<>());
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
        double margin = 0.5 * Math.max(deltaLat, deltaLon);
        try {
            BoundingBox boundingBox = new BoundingBox(maxLat + margin,
                    maxLon + margin,
                    minLat - margin,
                    minLon - margin
            );
            mapView.zoomToBoundingBox(boundingBox, true);
            mapView.invalidate();
        } catch (IllegalArgumentException e) {
            adjustZoomToMarker(start);
        }
    }

    /**
     * Ajuste le zoom de la carte pour englober plusieurs points donnés.
     *
     * @param points Les points à englober.
     */
    public void adjustZoomToListMarkers(List<GeoPoint> points) {
        double maxLat = Double.MIN_VALUE;
        double maxLon = Double.MIN_VALUE;
        double minLat = Double.MAX_VALUE;
        double minLon = Double.MAX_VALUE;
        double deltaLat;
        double deltaLon;
        for(int i =0 ; i < points.size(); i++) {
            double lat = points.get(i).getLatitude();
            double lon = points.get(i).getLongitude();
            if(maxLat < lat) {
                maxLat = lat;
            }
            if(minLat > lat) {
                minLat = lat;
            }
            if(maxLon < lon) {
                maxLon = lon;
            }
            if(minLon > lon) {
                minLon = lon;
            }
        }
        deltaLon = maxLon - minLon;
        deltaLat = maxLat - minLat;
        double margin = 0.25 * Math.max(deltaLat, deltaLon);
        try {
            BoundingBox boundingBox = new BoundingBox(maxLat + margin,
                    maxLon + margin,
                    minLat - margin,
                    minLon - margin
            );
            mapView.zoomToBoundingBox(boundingBox, true);
            mapView.invalidate();
        } catch (IllegalArgumentException e) {
            adjustZoomToMarker(points.get(0));
        }
    }

    /**
     * Ajuste le zoom de la carte pour englober un seul point donné.
     *
     * @param point Le point géographique à englober.
     */
    public void adjustZoomToMarker(GeoPoint point) {
        BoundingBox boundingBox = new BoundingBox(
                point.getLatitude() + 0.1,
                point.getLongitude() + 0.1,
                point.getLatitude() - 0.1,
                point.getLongitude() - 0.1
        );
        mapView.zoomToBoundingBox(boundingBox, true);
        mapView.invalidate();
    }

    /**
     * Met à jour la carte en invalidant la vue de la carte.
     */
    public void updateMap() {
        mapView.invalidate();
    }

    /**
     * Sérialise les données de la carte et les enregistre dans un fichier.
     *
     * @param context Le contexte de l'application.
     * @param mapData Les données de la carte à sérialiser.
     */
    public void serializeMapDataCached(Context context, Parcours mapData) {
        try (FileOutputStream fos = context.openFileOutput("mapData.ser", Context.MODE_PRIVATE);
             ObjectOutputStream oos = new ObjectOutputStream(fos)) {
            oos.writeObject(mapData);
            oos.flush();
            Log.d("MapFragment", "Serialized map data");
        } catch (IOException e) {
            Log.e("MapFragment", "Error serializing map data", e);
        }
    }

    /**
     * Désérialise les données de la carte à partir d'un fichier.
     *
     * @param context Le contexte de l'application.
     * @return Les données de la carte désérialisées ou null si le fichier n'existe pas ou est vide.
     */
    public Parcours deserializeMapDataCached(Context context) {
        File file = new File(context.getFilesDir(), "mapData.ser");
        if (!file.exists()) {
            System.out.println("File does not exist, skipping deserialization");
            return null;
        }
        if (file.length() == 0) {
            System.out.println("File is empty, skipping deserialization");
            return null;
        }
        try (FileInputStream fileIn = context.openFileInput("mapData.ser");
             ObjectInputStream in = new ObjectInputStream(fileIn)) {
            System.out.println("Deserialized map data");
            return (Parcours) in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            Log.e("MapFragment", "Error deserializing map data", e);
        }
        return null;
    }

    /**
     * Supprime les données de la carte sérialisées du cache.
     *
     * @param context Le contexte de l'application.
     */
    public void clearMapDataCached(Context context) {
        File file = new File(context.getFilesDir(), "mapData.ser");
        if (file.exists()) {
            Log.e("MapFragment", file.delete() ? "Deleted map data" : "Failed to delete map data");
        }
    }

    /**
     * Vérifie si les dernières coordonnées enregistrées sont à plus de 200 mètres.
     *
     * @param coordinates Les coordonnées à vérifier.
     * @return true si les coordonnées sont à plus de 200 mètres, sinon false.
     */
    public boolean isLastRecordedCoordinates200MetersAway(Coordonnees coordinates) {
        if (lastChangedCoordinates200Meters == null) {
            lastChangedCoordinates200Meters = coordinates;
            return true;
        }
        if (computeHaversineFormula(coordinates, lastChangedCoordinates200Meters) > 200) {
            lastChangedCoordinates200Meters = coordinates;
            return true;
        }
        return false;
    }

    /**
     * Vérifie si les dernières coordonnées enregistrées sont à plus de 20 mètres.
     *
     * @param coordinates Les coordonnées à vérifier.
     * @return true si les coordonnées sont à plus de 20 mètres, sinon false.
     */
    public boolean isLastRecordedCoordinates20MetersAway(Coordonnees coordinates) {
        if (lastChangedCoordinates20Meters == null) {
            lastChangedCoordinates20Meters = coordinates;
            return true;
        }
        if (computeHaversineFormula(coordinates, lastChangedCoordinates20Meters) > 20) {
            lastChangedCoordinates20Meters = coordinates;
            return true;
        }
        return false;
    }

    /**
     * Vérifie si un client est à moins de 200 mètres des coordonnées données.
     *
     * @param coordinates       Les coordonnées actuelles.
     * @param clientCoordinates Les coordonnées du client.
     * @return true si le client est à moins de 200 mètres, sinon false.
     */
    public boolean isClientWithin200Meters(Coordonnees coordinates, Coordonnees clientCoordinates) {
        return computeHaversineFormula(coordinates, clientCoordinates) < 200;
    }

    /**
     * Calcule la distance entre deux points géographiques en utilisant la formule de Haversine.
     *
     * @param coordinates      les coordonnées du premier point
     * @param otherCoordinates les coordonnées du second point
     * @return la distance entre les deux points en mètres
     */
    public static int computeHaversineFormula(Coordonnees coordinates, Coordonnees otherCoordinates) {
        double earthRadiusInKm = 6371.0;
        double deltaLat = Math.toRadians(otherCoordinates.getLatitude() - coordinates.getLatitude());
        double deltaLon = Math.toRadians(otherCoordinates.getLongitude() - coordinates.getLongitude());
        double startLat = Math.toRadians(coordinates.getLatitude());
        double endLat = Math.toRadians(otherCoordinates.getLatitude());

        double a = Math.pow(Math.sin(deltaLat / 2), 2)
                + Math.cos(startLat) * Math.cos(endLat)
                * Math.pow(Math.sin(deltaLon / 2), 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return (int) Math.round(earthRadiusInKm * c * 1000);
    }

    /**
     * Crée et ajoute une polyline normale à la carte.
     *
     * @param points Les points géographiques de la polyline.
     * @return La polyline créée.
     */
    public Polyline createAndAddNormalPolylineToMap(List<GeoPoint> points) {
        Polyline polyline = new Polyline();
        polyline.setPoints(points);
        polyline.getOutlinePaint().setStrokeWidth(5);
        polyline.getOutlinePaint().setColor(0xFF0000FF);
        polyline.getOutlinePaint().setStrokeCap(Paint.Cap.ROUND);
        mapView.getOverlayManager().add(polyline);
        return polyline;
    }

    /**
     * Crée et ajoute une polyline en pointillés à la carte.
     *
     * @param points Les points géographiques de la polyline.
     */
    public void createAndAddDottedPolylineToMap(List<GeoPoint> points) {
        Polyline polyline = new Polyline();
        polyline.setPoints(points);
        polyline.getOutlinePaint().setStrokeWidth(5);
        polyline.getOutlinePaint().setColor(0xFF0000FF);
        polyline.getOutlinePaint().setStrokeCap(Paint.Cap.ROUND);
        polyline.getOutlinePaint().setPathEffect(new android.graphics.DashPathEffect(new float[]{10, 20}, 0));
        mapView.getOverlayManager().add(polyline);
    }

    /**
     * Charge une liste de points géographiques et les ajoute à la carte.
     *
     * @param points La liste des points géographiques.
     */
    public void loadGeoPointsList(List<GeoPoint> points) {
        for (int i = 1; i < points.size(); i++) {
            addNewLine(points.get(i - 1), points.get(i));
        }
    }

    /**
     * Ajoute un nouveau point géographique à la polyline la plus récente.
     *
     * @param end Le point géographique à ajouter.
     */
    public void addNewGeoPoint(GeoPoint end) {
        if (latestPolyline.getActualPoints().isEmpty()) {
            latestPolyline.addPoint(end);
        } else {
            GeoPoint start = latestPolyline.getActualPoints().get(latestPolyline.getActualPoints().size() - 1);
            addNewLine(start, end);
        }
    }

    /**
     * Ajoute une nouvelle ligne entre deux points géographiques.
     *
     * @param start Le point de départ.
     * @param end   Le point d'arrivée.
     */
    private void addNewLine(GeoPoint start, GeoPoint end) {
        Coordonnees startCoordinates = new Coordonnees(start.getLatitude(), start.getLongitude());
        Coordonnees endCoordinates = new Coordonnees(end.getLatitude(), end.getLongitude());
        if (computeHaversineFormula(startCoordinates, endCoordinates) > 500) {
            createAndAddDottedPolylineToMap(List.of(start, end));
            latestPolyline = createAndAddNormalPolylineToMap(List.of(end));
        } else {
            latestPolyline.addPoint(end);
        }
    }
}
