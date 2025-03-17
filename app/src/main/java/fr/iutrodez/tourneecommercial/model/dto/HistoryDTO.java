package fr.iutrodez.tourneecommercial.model.dto;

import org.osmdroid.util.GeoPoint;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import fr.iutrodez.tourneecommercial.model.Coordonnees;
import fr.iutrodez.tourneecommercial.model.Visit;
import fr.iutrodez.tourneecommercial.utils.helper.MapHelper;

public class HistoryDTO {

    private String nom;
    private List<Visit> visitList;

    private String dateDebut;
    private String dateFin;

    private String heureFin;

    private String heureDebut;

    private String distance;

    private String duree;

    private List<GeoPoint> chemin;

    public HistoryDTO(String nom, List<Visit> visitList, LocalDateTime dateDebut, LocalDateTime dateFin,List<GeoPoint> chemin) {
        this.nom = nom;
        this.visitList = visitList;
        this.dateDebut = dateDebut.getYear() +"/"+ String.format("%02d",dateDebut.getMonthValue()) +"/"+ String.format("%02d",dateDebut.getDayOfMonth());
        this.dateFin = dateFin.getYear() +"/"+ String.format("%02d",dateFin.getMonthValue()) +"/"+ String.format("%02d",dateFin.getDayOfMonth());
        this.heureDebut = String.format("%02d",dateDebut.getHour())+"h"+String.format("%02d",dateDebut.getMinute());
        this.heureFin = String.format("%02d",dateFin.getHour())+"h"+String.format("%02d",dateFin.getMinute());
        long seconds = Duration.between(dateDebut,dateFin).getSeconds();
        long minutes = seconds /60 %60;
        long heures = seconds/3600;
        this.duree = String.format("%02d",heures)+"h"+String.format("%02d",minutes);
        this.chemin = chemin;
        int distance = 0;
        for(int i =0; i<chemin.size()-1 ; i++) {
            distance += MapHelper.computeHaversineFormula(new Coordonnees(chemin.get(i).getLatitude(),chemin.get(i).getLongitude()),
                    new Coordonnees(chemin.get(i+1).getLatitude(),chemin.get(i+1).getLongitude()));
        }
        this.distance = String.valueOf(distance) + " m";


    }

    public String getNom() {
        return nom;
    }

    public List<Visit> getVisitList() {
        return visitList;
    }

    public String getDateDebut() {
        return dateDebut;
    }

    public String getDateFin() {
        return dateFin;
    }

    public String getDistance() {
        return distance;
    }

    public String getDuree() {
        return duree;
    }

    public String getHeureDebut() {
        return heureDebut;
    }

    public String getHeureFin() {
        return heureFin;
    }

    public List<GeoPoint> getChemin() {
        return chemin;
    }
}
