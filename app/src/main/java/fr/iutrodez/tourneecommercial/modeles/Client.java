package fr.iutrodez.tourneecommercial.modeles;

public class Client {


    private String nomEntreprise;

    private Adresse adresse;

    private Contact contact;
    public Client(  String nomEntreprise,Adresse adresse ) {
        this.adresse = adresse;
        this.nomEntreprise = nomEntreprise;
    }

    public Adresse getAdresse(){
        return this.adresse;
    }


    public String getNomEntreprise(){
        return this.nomEntreprise;
    }

    public Contact getContact() {
        return contact;
    }
}
