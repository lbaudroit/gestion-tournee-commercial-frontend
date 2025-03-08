package fr.iutrodez.tourneecommercial.model;

public class Coordonnees implements java.io.Serializable {

    double latitude;
    double longitude;

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
