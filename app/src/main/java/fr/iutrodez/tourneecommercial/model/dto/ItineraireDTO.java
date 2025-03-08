package fr.iutrodez.tourneecommercial.model.dto;

import fr.iutrodez.tourneecommercial.model.Client;

import java.util.List;

public class ItineraireDTO {
    private long id;
    private String nom;
    private final List<Client> clients;
    private final int distance;

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
