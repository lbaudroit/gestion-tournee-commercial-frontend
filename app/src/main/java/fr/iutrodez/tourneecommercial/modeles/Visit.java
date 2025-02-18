package fr.iutrodez.tourneecommercial.modeles;

public class Visit {


    private String name;
    private boolean visited;
    private Coordonnees coordonnees;
    public Visit(Client client, boolean visited){
        this.name = client.getNomEntreprise();
        this.visited = visited;
        this.coordonnees = client.getCoordonnees();
    }

    public String getName() {
        return name;
    }

    public boolean isVisited() {
        return visited;
    }

    public Coordonnees getCoordonnees() {
        return coordonnees;
    }
}
