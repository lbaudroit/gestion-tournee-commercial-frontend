package fr.iutrodez.tourneecommercial.utils.api;

import android.content.Context;
import com.android.volley.RequestQueue;
import com.google.gson.Gson;
import fr.iutrodez.tourneecommercial.model.Client;
import fr.iutrodez.tourneecommercial.model.Itineraire;
import fr.iutrodez.tourneecommercial.model.dto.ItineraireDTO;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Classe permettant de faire des requêtes à l'API pour les itinéraires
 *
 * @author Benjamin NICOL, Enzo CLUZEL, Ahmed BRIBACH, Leïla BAUDROIT
 */
public class ItineraireApiRequest extends ApiRessource {
    private static final String RESOURCE_NAME = "itineraire";

    /**
     * Constructeur de la classe ItineraireApiRequest.
     *
     * @param requestQueue La file de requêtes à utiliser pour les requêtes réseau.
     */
    public ItineraireApiRequest(RequestQueue requestQueue, String url) {
        super(requestQueue, url);
    }

    /**
     * Crée un JSONObject représentant un ItineraireDTO.
     *
     * @param nom      Le nom de l'itinéraire.
     * @param clients  La liste des clients associés.
     * @param distance La distance de l'itinéraire.
     * @return Le JSONObject représentant l'itinéraire.
     */
    private static JSONObject itinerartyDTOCreation(String nom, List<Client> clients, int distance) {
        // Création de l'objet avec les données
        JSONObject itineraireData = new JSONObject();
        List<String> clientIds = clients.stream()
                .map(Client::get_id)
                .collect(Collectors.toList());
        try {
            itineraireData.put("nom", nom);
            itineraireData.put("idClients", new JSONArray(clientIds));
            itineraireData.put("distance", distance);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        return itineraireData;
    }

    /**
     * Crée un nouvel itinéraire.
     *
     * @param context         Le contexte de l'application.
     * @param name            Le nom de l'itinéraire.
     * @param distance        La distance de l'itinéraire.
     * @param clients         La liste des clients associés à l'itinéraire.
     * @param successCallback Le callback à appeler en cas de succès.
     * @param errorCallback   Le callback à appeler en cas d'erreur.
     */
    public void create(Context context, String name, int distance, List<Client> clients, SuccessCallback<String> successCallback, ErrorCallback errorCallback) {
        String url = RESOURCE_NAME + "/";
        JSONObject body = itinerartyDTOCreation(name, clients, distance);
        super.postWithToken(context, url, body, response -> successCallback.onSuccess(extractMessage(response)), errorCallback::onError);
    }

    /**
     * Récupère le nombre de pages d'itinéraires.
     *
     * @param context         Le contexte de l'application.
     * @param successCallback Le callback à appeler en cas de succès.
     * @param errorCallback   Le callback à appeler en cas d'erreur.
     */
    public void getNumberOfPages(Context context, SuccessCallback<Integer> successCallback, ErrorCallback errorCallback) {
        String url = RESOURCE_NAME + "/count";
        super.getWithToken(context, url, response -> {
            try {
                successCallback.onSuccess(response.getInt("nombre"));
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }, errorCallback::onError);
    }

    /**
     * Récupère une page d'itinéraires.
     *
     * @param context         Le contexte de l'application.
     * @param page            Le numéro de la page à récupérer.
     * @param successCallback Le callback à appeler en cas de succès.
     * @param errorCallback   Le callback à appeler en cas d'erreur.
     */
    public void getPage(Context context, int page, SuccessCallback<List<Itineraire>> successCallback, ErrorCallback errorCallback) {
        String url = RESOURCE_NAME + "/lazy" + "?page=" + page;
        super.getWithTokenAsArray(context, url, response -> successCallback.onSuccess(extractItineraires(response)), errorCallback::onError);
    }

    /**
     * Génère un itinéraire à partir d'une liste de clients.
     *
     * @param context         Le contexte de l'application.
     * @param clients         La liste des clients pour générer l'itinéraire.
     * @param successCallback Le callback à appeler en cas de succès.
     * @param errorCallback   Le callback à appeler en cas d'erreur.
     */
    public void generate(Context context, List<Client> clients, SuccessCallback<ItineraireDTO> successCallback, ErrorCallback errorCallback) {
        String ids = clients.stream()
                .map(Client::get_id)
                .map(String::valueOf)
                .collect(Collectors.joining(","));
        String url = RESOURCE_NAME + "/generate?clients=" + ids;
        super.getWithToken(context, url, response -> successCallback.onSuccess(formatAndExtractClientsGeneration(clients, response)), errorCallback::onError);
    }

    /**
     * Récupère un itinéraire par son identifiant.
     *
     * @param context         Le contexte de l'application.
     * @param id              L'identifiant de l'itinéraire à récupérer.
     * @param successCallback Le callback à appeler en cas de succès.
     * @param errorCallback   Le callback à appeler en cas d'erreur.
     */
    public void getOne(Context context, long id, SuccessCallback<ItineraireDTO> successCallback, ErrorCallback errorCallback) {
        String url = RESOURCE_NAME + "/" + id;
        super.getWithToken(context, url, response -> successCallback.onSuccess(extractItinerairesDTOOne(response)), errorCallback::onError);
    }

    /**
     * Met à jour un itinéraire existant.
     *
     * @param context         Le contexte de l'application.
     * @param id              L'identifiant de l'itinéraire à mettre à jour.
     * @param name            Le nouveau nom de l'itinéraire.
     * @param distance        La nouvelle distance de l'itinéraire.
     * @param clients         La nouvelle liste des clients associés à l'itinéraire.
     * @param successCallback Le callback à appeler en cas de succès.
     * @param errorCallback   Le callback à appeler en cas d'erreur.
     */
    public void update(Context context, long id, String name, int distance, List<Client> clients, SuccessCallback<String> successCallback, ErrorCallback errorCallback) {
        String url = RESOURCE_NAME + "/" + id;
        JSONObject body = itinerartyDTOCreation(name, clients, distance);
        super.putWithToken(context, url, body, response -> successCallback.onSuccess(extractMessage(response)), errorCallback::onError);
    }

    /**
     * Supprime un itinéraire par son identifiant.
     *
     * @param context         Le contexte de l'application.
     * @param id              L'identifiant de l'itinéraire à supprimer.
     * @param successCallback Le callback à appeler en cas de succès.
     * @param errorCallback   Le callback à appeler en cas d'erreur.
     */
    public void delete(Context context, long id, SuccessCallback<String> successCallback, ErrorCallback errorCallback) {
        String url = RESOURCE_NAME + "/" + id;
        super.deleteWithToken(context, url, response -> successCallback.onSuccess(extractMessage(response)), errorCallback::onError);
    }

    /**
     * Extrait une liste d'itinéraires à partir d'un JSONArray.
     *
     * @param response Le JSONArray contenant les itinéraires.
     * @return La liste des itinéraires extraits.
     */
    private List<Itineraire> extractItineraires(JSONArray response) {
        List<Itineraire> itineraires = new java.util.ArrayList<>();
        for (int i = 0; i < response.length(); i++) {
            JSONObject itineraire;
            try {
                itineraire = response.getJSONObject(i);
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
            try {
                itineraires.add(new Itineraire(itineraire.getString("nom"), itineraire.getInt("distance"), itineraire.getLong("id")));
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }
        return itineraires;
    }

    /**
     * Extrait un message d'un JSONObject.
     *
     * @param json Le JSONObject contenant le message.
     * @return Le message extrait.
     */
    private String extractMessage(JSONObject json) {
        try {
            return json.getString("message");
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Formate et extrait un ItineraireDTO à partir d'un JSONObject.
     *
     * @param clients  La liste des clients associés.
     * @param response Le JSONObject contenant les données de l'itinéraire.
     * @return L'itineraireDTO extrait et formaté.
     */
    private ItineraireDTO formatAndExtractClientsGeneration(List<Client> clients, JSONObject response) {
        int kilometres;
        List<Client> clientsTmp = new java.util.ArrayList<>();
        try {
            JSONArray clients_received = response.getJSONArray("clients");
            for (int i = 0; i < clients_received.length(); i++) {
                JSONObject client = clients_received.getJSONObject(i);
                for (Client c : clients) {
                    if (c.get_id().equals(client.getString("_id"))) {
                        clientsTmp.add(c);
                    }
                }
            }
            kilometres = response.getInt("kilometres");
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        return new ItineraireDTO(clientsTmp, kilometres);
    }

    /**
     * Extrait un ItineraireDTO à partir d'un JSONObject.
     *
     * @param response Le JSONObject contenant les données de l'itinéraire.
     * @return L'itineraireDTO extrait.
     */
    private ItineraireDTO extractItinerairesDTOOne(JSONObject response) {
        Gson gson = new Gson();
        return gson.fromJson(response.toString(), ItineraireDTO.class);
    }
}
