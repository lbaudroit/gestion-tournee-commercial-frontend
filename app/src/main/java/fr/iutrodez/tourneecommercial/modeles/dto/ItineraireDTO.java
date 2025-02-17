package fr.iutrodez.tourneecommercial.modeles.dto;

import fr.iutrodez.tourneecommercial.modeles.Client;

import java.util.List;

public class ItineraireDTO {
    private long id;
    private String nom;
    private List<Client> clients;
    private int distance;

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
