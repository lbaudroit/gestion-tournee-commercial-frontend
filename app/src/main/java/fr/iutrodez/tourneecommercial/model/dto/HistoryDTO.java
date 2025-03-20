package fr.iutrodez.tourneecommercial.model.dto;

import fr.iutrodez.tourneecommercial.model.Coordonnees;
import fr.iutrodez.tourneecommercial.model.Visit;
import fr.iutrodez.tourneecommercial.utils.helper.MapHelper;
import org.osmdroid.util.GeoPoint;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;

/**
 * DTO pour l'historique des itinéraires.
 *
 * @author Benjamin NICOL, Enzo CLUZEL, Ahmed BRIBACH, Leïla BAUDROIT
 */
public class HistoryDTO {

    private final String nom;
    private final List<Visit> visitList;

    private final String dateDebut;

    private final String heureFin;

    private final String heureDebut;

    private final String distance;

    private final String duree;

    private final List<GeoPoint> chemin;

    public HistoryDTO(String nom, List<Visit> visitList, LocalDateTime dateDebut, LocalDateTime dateFin, List<GeoPoint> chemin) {
        this.nom = nom;
        this.visitList = visitList;
        this.dateDebut = String.format(Locale.FRANCE, "%d/%02d/%02d", dateDebut.getYear(), dateDebut.getMonthValue(), dateDebut.getDayOfMonth());
        this.heureDebut = String.format(Locale.FRANCE, "%02dh%02d", dateDebut.getHour(), dateDebut.getMinute());
        this.heureFin = String.format(Locale.FRANCE, "%02dh%02d", dateFin.getHour(), dateFin.getMinute());
        long seconds = Duration.between(dateDebut, dateFin).getSeconds();
        long minutes = seconds / 60 % 60;
        long heures = seconds / 3600;
        this.duree = String.format(Locale.FRANCE, "%02dh%02d", heures, minutes);
        this.chemin = chemin;
        int distance = 0;
        for (int i = 0; i < chemin.size() - 1; i++) {
            distance += MapHelper.computeHaversineFormula(new Coordonnees(chemin.get(i).getLatitude(), chemin.get(i).getLongitude()),
                    new Coordonnees(chemin.get(i + 1).getLatitude(), chemin.get(i + 1).getLongitude()));
        }
        this.distance = distance + " m";


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
