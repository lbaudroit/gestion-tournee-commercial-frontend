package fr.iutrodez.tourneecommercial.modeles;

public class Adresse {

    private String libelle;
    private String codePostal;
    private String ville;

    public Adresse(String libelle,String code_postal , String ville){
        this.libelle = libelle;
        this.codePostal = code_postal;
        this.ville = ville;
    }

    public String getLibelle() {
        return libelle;
    }

    public String getCodePostal() {
        return codePostal;
    }

    public String getVille() {
        return ville;
    }
}
