package fr.iutrodez.tourneecommercial.modeles;

public class Client {


    private String _id;

    private String idUtilisateur;
    private String nomEntreprise;

    private String adresse;

    public Client(  String nomEntreprise,String adresse , String codePostal) {
        this.adresse = adresse + codePostal;
        this.nomEntreprise = nomEntreprise;
    }
    public Client(  String _id,String idUtilisateur,String nomEntreprise,Adresse adresse,Contact contact ) {
        this.adresse = adresse;
        this._id = _id;
        this.contact = contact;
        this.nomEntreprise = nomEntreprise;
    }


    public String getAdresse(){
        return this.adresse;
    }

    public String getNomEntreprise(){
        return this.nomEntreprise;
    }
}
