package fr.iutrodez.tourneecommercial.model.dto;

/**
 * Classe représentant les paramètres de l'utilisateur.
 *
 * @author Benjamin NICOL, Enzo CLUZEL, Ahmed BRIBACH, Leïla BAUDROIT
 */
public class Parameter {
    final String nom;
    final String prenom;
    final String email;

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
