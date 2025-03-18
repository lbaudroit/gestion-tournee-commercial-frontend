package fr.iutrodez.tourneecommercial.model.dto;

public class ParcoursReducedDTO {
    String id;
    String nom;
    String date;

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
