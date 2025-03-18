package fr.iutrodez.tourneecommercial.model;

import androidx.annotation.NonNull;

public class Adresse implements java.io.Serializable {

    private final String libelle;
    private final String codePostal;
    private final String ville;

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
