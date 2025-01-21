package fr.iutrodez.tourneecommercial.modeles;

public class Client {

    private int idUtilisateur;

    private String nomEntreprise;

    private Adresse adresse;
    private String description;
    private double longitude;
    private double latitude;
    private Contact contact;

    public Client(int idUtilisateur , String nomEntreprise , Adresse adresse,
                  String description , double longitude , double latitude, Contact contact) {
        this.idUtilisateur = idUtilisateur;
        this.nomEntreprise = nomEntreprise;
    }
}
