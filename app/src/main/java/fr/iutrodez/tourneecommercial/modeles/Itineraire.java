package fr.iutrodez.tourneecommercial.modeles;

public class Itineraire {

    String nom;

    int kilometres;

    public Itineraire(String nom, int kilometres) {
        this.nom = nom;
        this.kilometres = kilometres;
    }

    public String getNom() {
        return nom;
    }

    public int getKilometres() {
        return kilometres;
    }
}
