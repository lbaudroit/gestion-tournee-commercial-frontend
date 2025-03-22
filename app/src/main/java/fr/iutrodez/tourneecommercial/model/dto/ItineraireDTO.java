package fr.iutrodez.tourneecommercial.model.dto;

import fr.iutrodez.tourneecommercial.model.Client;

import java.util.List;

/**
 * DTO représentant un itinéraire.
 *
 * @author Benjamin NICOL, Enzo CLUZEL, Ahmed BRIBACH, Leïla BAUDROIT
 */
public class ItineraireDTO {
    private final List<Client> clients;
    private final int distance;
    private long id;
    private String nom;

    /**
     * @noinspection unused
     */
    public ItineraireDTO(long id, String nom, List<Client> clients, int distance) {
        this.id = id;
        this.nom = nom;
        this.clients = clients;
        this.distance = distance;
    }

    public ItineraireDTO(List<Client> clients, int distance) {
        this.clients = clients;
        this.distance = distance;
    }

    public long getId() {
        return id;
    }

    public String getNom() {
        return nom;
    }

    public List<Client> getClients() {
        return clients;
    }

    public int getDistance() {
        return distance;
    }
}
