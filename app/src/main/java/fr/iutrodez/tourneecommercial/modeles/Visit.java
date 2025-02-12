package fr.iutrodez.tourneecommercial.modeles;

public class Visit {

    Client client;

    boolean visited;
    public Visit(Client client, boolean visited){
        this.client = client;
        this.visited = visited;
    }
}
