package fr.iutrodez.tourneecommercial.model;

/**
 * Classe représentant les coordonnées géographiques.
 *
 * @author Benjamin NICOL, Enzo CLUZEL, Ahmed BRIBACH, Leïla BAUDROIT
 */
public class Coordonnees implements java.io.Serializable {

    final double latitude;
    final double longitude;

    public Coordonnees(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }
}
