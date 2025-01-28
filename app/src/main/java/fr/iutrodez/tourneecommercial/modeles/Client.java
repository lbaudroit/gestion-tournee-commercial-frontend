package fr.iutrodez.tourneecommercial.modeles;

public class Client {


    private String _id;

    private String idUtilisateur;
    private String nomEntreprise;

    private Adresse adresse;

    private Contact contact;
    public Client(  String nomEntreprise,Adresse adresse ) {
        this.adresse = adresse;
        this.nomEntreprise = nomEntreprise;
    }
    public Client(  String _id,String idUtilisateur,String nomEntreprise,Adresse adresse,Contact contact ) {
        this.adresse = adresse;
        this._id = _id;
        this.contact = contact;
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
