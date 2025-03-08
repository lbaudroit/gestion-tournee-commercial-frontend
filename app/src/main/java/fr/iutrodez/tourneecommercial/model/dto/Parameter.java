package fr.iutrodez.tourneecommercial.model.dto;

public class Parameter {
    String nom;
    String prenom;
    String email;

    public Parameter(String nom, String prenom, String email) {
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
    }

    public String getNom() {
        return nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public String getEmail() {
        return email;
    }
}
