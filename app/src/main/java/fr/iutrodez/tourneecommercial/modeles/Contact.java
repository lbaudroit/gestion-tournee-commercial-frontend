package fr.iutrodez.tourneecommercial.modeles;

public class Contact {
    private String nom;
    private String prenom;
    private String numeroTelephone;

    public Contact(String nom, String prenom, String tel) {

        this.nom = nom;
        this.prenom = prenom;
        this.numeroTelephone = tel;
    }

    public Contact() {
        nom = "";
        prenom = "";
        numeroTelephone = "";
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
