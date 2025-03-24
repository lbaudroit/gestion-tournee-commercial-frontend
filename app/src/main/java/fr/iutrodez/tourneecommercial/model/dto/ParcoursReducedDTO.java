package fr.iutrodez.tourneecommercial.model.dto;

/**
 * DTO pour les parcours réduits.
 *
 * @author Benjamin NICOL, Enzo CLUZEL, Ahmed BRIBACH, Leïla BAUDROIT
 */
public class ParcoursReducedDTO {
    final String id;
    final String nom;
    final String date;

    public ParcoursReducedDTO(String id, String nom, String date) {
        this.id = id;
        this.nom = nom;
        this.date = date;
    }

    public String getId() {
        return id;
    }

    public String getNom() {
        return nom;
    }

    public String getDate() {
        return date;
    }
}
