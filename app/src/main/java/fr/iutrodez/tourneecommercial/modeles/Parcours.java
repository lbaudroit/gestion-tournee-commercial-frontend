package fr.iutrodez.tourneecommercial.modeles;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Ahmed Bribach
 */
public class Parcours {

    private List<Visit> visits;
    private List<Coordonnees> etapes;

    private String name;
    /**
     *
     */
    public Parcours() {
        visits = new ArrayList<>();
    }

    /**
     *
     * @param visits
     */
    public Parcours(List<Visit> visits){
        this.visits = visits;
    }

    /**
     *
     * @param name
     */
    public Parcours(String name){
        this.name = name;
    }

    /**
     *
     * @param visit
     */
    public void addVisite(Visit visit){
        visits.add(visit);
    }

    public void addListVisits(List<Visit> visits){
        this.visits.addAll(visits);
    }

    public void addEtapes(Coordonnees coords){
        etapes.add(coords);
    }

    public List<Coordonnees> getEtapes(){
        return etapes;
    }
    public void setName(String name){
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public List<Visit> getVisits() {
        return visits;
    }
}
