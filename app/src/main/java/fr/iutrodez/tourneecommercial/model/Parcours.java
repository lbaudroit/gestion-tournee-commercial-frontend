package fr.iutrodez.tourneecommercial.model;

import android.content.Context;
import android.util.Log;
import fr.iutrodez.tourneecommercial.utils.helper.SavedParcoursHelper;
import org.osmdroid.util.GeoPoint;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import static fr.iutrodez.tourneecommercial.fragments.ItineraryFragment.API_REQUEST;

/**
 * Classe représentant un itinéraire.
 *
 * @author Benjamin NICOL, Enzo CLUZEL, Ahmed BRIBACH, Leïla BAUDROIT
 */
public class Parcours implements java.io.Serializable {
    private final String name;
    private final List<Client> clients;
    private final List<Visit> visited;
    private final List<GeoPoint> path;
    private final String startTime;
    private int currentClientIndex;
    private String endTime;

    public Parcours(String name, List<Client> clients) {
        this.name = name;
        this.clients = clients;
        this.visited = new java.util.ArrayList<>();
        this.path = new java.util.ArrayList<>();
        this.startTime = getCurrentTime();
        this.endTime = null;
    }

    public Coordonnees getCurrentClientCoordonnees() {
        return clients.get(currentClientIndex).getCoordonnees();
    }

    public String getCurrentClientName() {
        return clients.get(currentClientIndex).getNomEntreprise();
    }

    public String getCurrentAddress() {
        return clients.get(currentClientIndex).getAdresse().toString();
    }


    public List<Visit> getClientsVisited() {
        return visited;
    }

    public String getItineraryName() {
        return name;
    }

    public boolean clientInItinerary(Client client) {
        return clients.contains(client);
    }

    public void addPointToPath(GeoPoint point) {
        path.add(point);
    }

    public List<GeoPoint> getPath() {
        return path;
    }

    public String getStart() {
        return startTime;
    }

    public String getEnd() {
        return endTime;
    }

    /**
     * Récupère le client actuel.
     *
     * @return Le client actuel.
     */
    public GeoPoint getCurrentClientGeoPoint() {
        return new GeoPoint(clients.get(currentClientIndex).getCoordonnees().getLatitude(),
                clients.get(currentClientIndex).getCoordonnees().getLongitude());
    }

    /**
     * Récupère le type du client actuel.
     *
     * @return String du type du client actuel.
     */
    public String getCurrentType() {
        return clients.get(currentClientIndex).isClientEffectif() ? "Client" : "Prospect";
    }

    /**
     * Récupère le numéro de téléphone du client actuel.
     *
     * @return Le numéro de téléphone du client actuel.
     */
    public String getCurrentClientPhoneNumber() {
        return clients.get(currentClientIndex).getContact().getNumeroTelephone();
    }

    /**
     * Marque le client actuel comme visité et passe au suivant.
     *
     * @return true si le client suivant existe, false sinon.
     */
    public boolean markCurrentAsVisitedAndMoveToNext() {
        return markCurrentAsAndMoveToNext(true);
    }

    /**
     * Marque le client actuel comme non visité et passe au suivant.
     *
     * @return true si le client suivant existe, false sinon.
     */
    public boolean markCurrentAsNotVisitedAndMoveToNext() {
        return markCurrentAsAndMoveToNext(false);
    }

    /**
     * Enregistre et sauvegarde l'itinéraire.
     *
     * @param context Le contexte de l'application.
     */
    public void registerAndSaveItineraire(Context context) {
        SavedParcoursHelper SavedParcoursHelper = new SavedParcoursHelper(context);
        this.endTime = getCurrentTime();
        boolean done = clients.size() > currentClientIndex;
        while (done) {
            done = this.markCurrentAsNotVisitedAndMoveToNext();
        }
        API_REQUEST.parcours.create(context, this,
                System.out::println,
                error -> {
                    Log.e("ERROR", error.getMessage());
                    SavedParcoursHelper.serializeToSendLater(this);
                });
    }

    /**
     * Marque le client actuel comme visité et passe au suivant.
     *
     * @param isVisited Indique si le client actuel a été visité.
     * @return true si le client suivant existe, false sinon.
     */
    private boolean markCurrentAsAndMoveToNext(boolean isVisited) {
        visited.add(new Visit(clients.get(currentClientIndex), isVisited));
        currentClientIndex++;
        return currentClientIndex < clients.size();
    }

    /**
     * Récupère l'heure actuelle.
     *
     * @return L'heure actuelle.
     */
    private String getCurrentTime() {
        return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm", Locale.FRANCE)
                .format(new java.util.Date());
    }
}
