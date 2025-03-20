package fr.iutrodez.tourneecommercial.model;

/**
 * Classe représentant l'entité Itineraire.
 *
 * @author Benjamin NICOL, Enzo CLUZEL, Ahmed BRIBACH, Leïla BAUDROIT
 */
public class Itineraire {

    private final long id;

    final String nom;

    final int kilometres;

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
