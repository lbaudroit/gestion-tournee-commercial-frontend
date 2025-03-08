package fr.iutrodez.tourneecommercial.utils.api;

import android.content.Context;
import com.android.volley.RequestQueue;
import com.google.gson.Gson;
import fr.iutrodez.tourneecommercial.model.Client;
import fr.iutrodez.tourneecommercial.model.Parcours;
import fr.iutrodez.tourneecommercial.model.Visit;
import fr.iutrodez.tourneecommercial.model.dto.ParcoursReducedDTO;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.osmdroid.util.GeoPoint;

import java.util.ArrayList;
import java.util.List;

/**
 * Classe pour gérer les requêtes API pour les parcours.
 */
public class ParcoursApiRequest extends ApiRessource {

    private static final String RESSOURCE_NAME = "parcours";

    /**
     * Constructeur pour initialiser la requête API avec une file de requêtes.
     *
     * @param requestQueue la file de requêtes à utiliser
     */
    public ParcoursApiRequest(RequestQueue requestQueue) {
        super(requestQueue);
    }

    /**
     * Crée un nouveau parcours via une requête API.
     *
     * @param context         le contexte de l'application
     * @param mapData         l'objet Parcours à créer
     * @param successCallback le callback en cas de succès
     * @param errorCallback   le callback en cas d'erreur
     */
    public void create(Context context, Parcours mapData, SuccessCallback<String> successCallback, ErrorCallback errorCallback) {
        String url = RESSOURCE_NAME + "/";
        JSONObject body = parcoursDTOCreation(mapData);
        super.postWithToken(context, url, body, response -> {
            successCallback.onSuccess(extractMessage(response));
        }, errorCallback::onError);
    }

    /**
     * Récupère le nombre de pages de parcours disponibles via une requête API.
     *
     * @param context         le contexte de l'application
     * @param successCallback le callback en cas de succès, retourne le nombre de pages
     * @param errorCallback   le callback en cas d'erreur
     */
    public void getNumberOfPages(Context context, SuccessCallback<Integer> successCallback, ErrorCallback errorCallback) {
        String url = RESSOURCE_NAME + "/count";
        System.out.println(url);
        super.getWithToken(context, url, response -> {
            try {
                System.out.println(response);
                successCallback.onSuccess(response.getInt("nombre"));
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }, errorCallback::onError);
    }

    /**
     * Récupère une page de parcours via une requête API.
     *
     * @param context         le contexte de l'application
     * @param page            le numéro de la page à récupérer
     * @param successCallback le callback en cas de succès, retourne une liste de ParcoursReducedDTO
     * @param errorCallback   le callback en cas d'erreur
     */
    public void getPage(Context context, int page, SuccessCallback<List<ParcoursReducedDTO>> successCallback, ErrorCallback errorCallback) {
        String url = RESSOURCE_NAME + "/lazy?page=" + page;
        super.getWithTokenAsArray(context, url, response -> {
            successCallback.onSuccess(extractParcours(response));
        }, errorCallback::onError);
    }

    /**
     * Extrait une liste de ParcoursReducedDTO à partir d'un JSONArray.
     *
     * @param response le JSONArray contenant les données des parcours
     * @return une liste de ParcoursReducedDTO
     */
    private List<ParcoursReducedDTO> extractParcours(JSONArray response) {
        Gson gson = new Gson();
        ArrayList<ParcoursReducedDTO> parcoursList = new ArrayList<>();
        for (int i = 0; i < response.length(); i++) {
            try {
                parcoursList.add(gson.fromJson(response.getJSONObject(i).toString(), ParcoursReducedDTO.class));
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }
        return parcoursList;
    }

    /**
     * Récupère les prospects pour les notifications via une requête API.
     *
     * @param context         le contexte de l'application
     * @param latitude        la latitude de la position actuelle
     * @param longitude       la longitude de la position actuelle
     * @param successCallback le callback en cas de succès, retourne une liste de clients
     * @param errorCallback   le callback en cas d'erreur
     */
    public void getProspectsForNotifications(Context context, double latitude, double longitude, SuccessCallback<List<Client>> successCallback, ErrorCallback errorCallback) {
        String url = RESSOURCE_NAME + "/prospects/notifications?latitude=" + latitude + "&longitude=" + longitude;
        super.getWithTokenAsArray(context, url, response -> {
            successCallback.onSuccess(extractClients(response));
        }, errorCallback::onError);
    }

    /**
     * Crée un objet JSON à partir d'un objet Parcours.
     *
     * @param mapData l'objet Parcours à convertir
     * @return l'objet JSON représentant le parcours
     */
    private static JSONObject parcoursDTOCreation(Parcours mapData) {
        JSONObject parcoursData = new JSONObject();
        try {

            JSONArray etapesArray = new JSONArray();
            for (Visit visite : mapData.getClientsVisited()) {
                if (visite != null) {
                    JSONObject visiteObj = new JSONObject();
                    visiteObj.put("nom", visite.getName());
                    visiteObj.put("visite", visite.isVisited());

                    JSONObject coordinates = new JSONObject();
                    coordinates.put("latitude", visite.getCoordonnees().getLatitude());
                    coordinates.put("longitude", visite.getCoordonnees().getLongitude());

                    visiteObj.put("coordonnees", coordinates);
                    etapesArray.put(visiteObj);
                } else {
                    System.out.println("Visite est null !");
                }
            }
            JSONArray coordinatesArray = new JSONArray();
            for (GeoPoint point : mapData.getPath()) {
                JSONArray pointObj = new JSONArray();
                pointObj.put(point.getLatitude());
                pointObj.put(point.getLongitude());
                coordinatesArray.put(pointObj);
            }
            JSONObject linestring = new JSONObject();
            linestring.put("type", "LineString");
            linestring.put("coordinates", coordinatesArray);
            parcoursData.put("debut", mapData.getStart());
            parcoursData.put("fin", mapData.getEnd());
            parcoursData.put("chemin", linestring);
            parcoursData.put("nom", mapData.getItineraryName());
            parcoursData.put("etapes", etapesArray);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        return parcoursData;
    }

    /**
     * Extrait le message d'un objet JSON.
     *
     * @param json l'objet JSON contenant le message
     * @return le message extrait
     * @throws RuntimeException si une erreur JSON se produit
     */
    private String extractMessage(JSONObject json) {
        try {
            return json.getString("message");
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Extrait une liste de clients à partir d'un JSONArray.
     *
     * @param clients le JSONArray contenant les données des clients
     * @return une liste de clients
     * @throws RuntimeException si une erreur JSON se produit
     */
    private List<Client> extractClients(JSONArray clients) {
        Gson gson = new Gson();
        ArrayList<Client> clientList = new ArrayList<>();
        for (int i = 0; i < clients.length(); i++) {
            try {
                clientList.add(gson.fromJson(clients.getJSONObject(i).toString(), Client.class));
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }
        return clientList;
    }
}
