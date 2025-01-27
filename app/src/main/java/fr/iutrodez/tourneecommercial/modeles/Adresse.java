package fr.iutrodez.tourneecommercial.modeles;

import android.view.View;

public class Adresse {

    private String libelle;
    private String code_postal;
    private String ville;

    public Adresse(String libelle,String code_postal , String ville){

        this.libelle = libelle;
        this.code_postal = code_postal;
        this.ville = ville;
    }
    public String getLibelle( ) {
        return this.libelle;

    }

    public String getVille() {
        return ville;
    }

    public String getCode_postal() {
        return code_postal;
    }
}
