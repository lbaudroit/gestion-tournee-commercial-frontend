package fr.iutrodez.tourneecommercial.model;

/**
 * Classe représentant un contact.
 *
 * @author Benjamin NICOL, Enzo CLUZEL, Ahmed BRIBACH, Leïla BAUDROIT
 */
public class Contact implements java.io.Serializable {
    private final String nom;
    private final String prenom;
    private final String numeroTelephone;

    public Contact(String nom, String prenom, String tel) {

        this.nom = nom;
        this.prenom = prenom;
        this.numeroTelephone = tel;
    }

    public String getNom() {
        return nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public String getNumeroTelephone() {
        return numeroTelephone;
    }
}
