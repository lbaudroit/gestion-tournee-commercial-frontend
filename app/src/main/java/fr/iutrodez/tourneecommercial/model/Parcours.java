package fr.iutrodez.tourneecommercial.model;

import static fr.iutrodez.tourneecommercial.fragments.ItineraryFragment.API_REQUEST;

import android.content.Context;

import org.osmdroid.util.GeoPoint;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import fr.iutrodez.tourneecommercial.utils.helper.SavedParcoursHelper;

public class Parcours implements java.io.Serializable {
    private final String name;
    private final List<Client> clients;
    private int currentClientIndex;
    private final List<Visit> visited;
    private final List<GeoPoint> path;
    private final String startTime;
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

    public GeoPoint getCurrentClientGeoPoint() {
        return new GeoPoint(clients.get(currentClientIndex).getCoordonnees().getLatitude(),
                clients.get(currentClientIndex).getCoordonnees().getLongitude());
    }

    public String getCurrentClientName() {
        return clients.get(currentClientIndex).getNomEntreprise();
    }

    public String getCurrentAddress() {
        return clients.get(currentClientIndex).getAdresse().toString();
    }

    public String getCurrentType() {
        return clients.get(currentClientIndex).isClientEffectif() ? "Client" : "Prospect";
    }

    public boolean markCurrentAsVisitedAndMoveToNext() {
        return markCurrentAsAndMoveToNext(true);
    }

    public boolean markCurrentAsNotVisitedAndMoveToNext() {
        return markCurrentAsAndMoveToNext(false);
    }

    public List<Visit> getClientsVisited() {
        return visited;
    }

    /**
     * Enregistre et sauvegarde l'itinéraire.
     *
     * @param context Le contexte de l'application.
     */
    public void registerAndSaveItineraire(Context context) {
        SavedParcoursHelper SavedParcoursHelper = new SavedParcoursHelper(context);
        this.endTime = getCurrentTime();

        API_REQUEST.parcours.create(context, this,
                System.out::println,
                error -> {
                    System.out.println(error.getMessage());
                    SavedParcoursHelper.serializeToSendLater(this);
                    System.out.println("Parcours saved to be sent later");
                });
    }

    public String getItineraryName() {
        return name;
    }

    public boolean clientInItinerary(Client client) {
        return clients.contains(client);
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
     * Récupère le numéro de téléphone du client actuel.
     *
     * @return Le numéro de téléphone du client actuel.
     */
    public String getCurrentClientPhoneNumber() {
        return clients.get(currentClientIndex).getContact().getNumeroTelephone();
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
     * Récupère l'heure actuelle.
     *
     * @return L'heure actuelle.
     */
    private String getCurrentTime() {
        return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm", Locale.FRANCE)
                .format(new java.util.Date());
    }
}
