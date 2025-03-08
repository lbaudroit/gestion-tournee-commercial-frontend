package fr.iutrodez.tourneecommercial.model.dto;

import fr.iutrodez.tourneecommercial.model.Adresse;
import fr.iutrodez.tourneecommercial.model.Contact;

public class ClientDTO {
    String nomEntreprise;
    Adresse adresse;
    String descriptif;
    Contact contact;
    boolean clientEffectif;

    public ClientDTO(String businessname, Adresse address, String description,
                     Contact contact, boolean isClient) {
        this.nomEntreprise = businessname;
        this.adresse = address;
        this.descriptif = description;
        this.contact = contact;
        this.clientEffectif = isClient;
    }
}
