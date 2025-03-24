package fr.iutrodez.tourneecommercial.model.dto;

import fr.iutrodez.tourneecommercial.model.Adresse;
import fr.iutrodez.tourneecommercial.model.Contact;

/**
 * Classe représentant un client.
 *
 * @author Benjamin NICOL, Enzo CLUZEL, Ahmed BRIBACH, Leïla BAUDROIT
 */
public class ClientDTO {
    final String nomEntreprise;
    final Adresse adresse;
    final String descriptif;
    final Contact contact;
    final boolean clientEffectif;

    public ClientDTO(String businessname, Adresse address, String description,
                     Contact contact, boolean isClient) {
        this.nomEntreprise = businessname;
        this.adresse = address;
        this.descriptif = description;
        this.contact = contact;
        this.clientEffectif = isClient;
    }
}
