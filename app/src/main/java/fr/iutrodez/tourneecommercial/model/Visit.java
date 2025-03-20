package fr.iutrodez.tourneecommercial.model;

/**
 * Classe représentant une visite.
 *
 * @author Benjamin NICOL, Enzo CLUZEL, Ahmed BRIBACH, Leïla BAUDROIT
 */
public class Visit implements java.io.Serializable {

    private final String name;
    private final boolean visited;
    private final Coordonnees coordonnees;

    public Visit(Client client, boolean visited) {
        this.name = client.getNomEntreprise();
        this.visited = visited;
        this.coordonnees = client.getCoordonnees();
    }

    public Visit(String name, boolean visited, Coordonnees coordonnees) {
        this.name = name;
        this.visited = visited;
        this.coordonnees = coordonnees;
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
