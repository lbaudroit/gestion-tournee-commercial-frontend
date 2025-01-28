package fr.iutrodez.tourneecommercial.modeles;

public class Contact {
    private String nom;
    private String prenom;
    private String tel;

    public Contact(String nom,String prenom , String tel){

        this.nom = nom;
        this.prenom = prenom;
        this.tel = tel;
    }
    public Contact(){
        nom = "";
        prenom = "";
        tel = "";
    }

    public String getNom() {
        return nom;
    }

    public  String getPrenom() {
        return prenom;
    }
    public String getTel() {
        return tel;
    }
}
