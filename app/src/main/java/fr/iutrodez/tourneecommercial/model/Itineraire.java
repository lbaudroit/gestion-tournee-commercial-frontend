package fr.iutrodez.tourneecommercial.model;

public class Itineraire {

    private final long id;

    String nom;

    int kilometres;

    public Itineraire(String nom, int kilometres, long id) {
        this.nom = nom;
        this.kilometres = kilometres;
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public int getKilometres() {
        return kilometres;
    }

    public long getId() {
        return id;
    }
}
