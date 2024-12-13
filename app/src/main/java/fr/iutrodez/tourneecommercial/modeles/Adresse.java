package fr.iutrodez.tourneecommercial.modeles;

public class Adresse {

    private String libelle;
    private String code_postal;
    private String ville;

    public Adresse(String libelle,String code_postal , String ville){

        this.libelle = libelle;
        this.code_postal = code_postal;
        this.ville = ville;
    }
}
