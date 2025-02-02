package fr.iutrodez.tourneecommercial.modeles;

import androidx.annotation.NonNull;

public class Adresse {

    private String libelle;
    private String codePostal;
    private String ville;


    public Adresse(String libelle, String codePostal, String ville) {

        this.libelle = libelle;
        this.codePostal = codePostal;
        this.ville = ville;
    }

    public String getLibelle() {
        return this.libelle;
    }

    public String getCodePostal() {
        return codePostal;
    }

    public String getVille() {
        return ville;
    }

    @NonNull
    @Override
    public String toString() {
        return libelle + ", " + codePostal + ", " + ville;
    }
}
