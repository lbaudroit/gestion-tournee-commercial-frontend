package fr.iutrodez.tourneecommercial.modeles;

public class Client {


    private String nomEntreprise;

    private String adresse;

    public Client(  String nomEntreprise,String adresse , String codePostal) {
        this.adresse = adresse + codePostal;
        this.nomEntreprise = nomEntreprise;
    }

    public String getAdresse(){
        return this.adresse;
    }

    public String getNomEntreprise(){
        return this.nomEntreprise;
    }
}
