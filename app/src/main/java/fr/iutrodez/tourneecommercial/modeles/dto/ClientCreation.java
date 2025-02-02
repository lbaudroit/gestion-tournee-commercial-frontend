package fr.iutrodez.tourneecommercial.modeles.dto;

import fr.iutrodez.tourneecommercial.modeles.Adresse;
import fr.iutrodez.tourneecommercial.modeles.Contact;

public class ClientCreation {
    String nomEntreprise;
    Adresse adresse;
    String descriptif;
    Contact contact;
    boolean clientEffectif;

    public ClientCreation(String businessname, Adresse adress, String description,
                          Contact contact, boolean isClient) {
        this.nomEntreprise = businessname;
        this.adresse = adress;
        this.descriptif = description;
        this.contact = contact;
        this.clientEffectif = isClient;
    }
}
