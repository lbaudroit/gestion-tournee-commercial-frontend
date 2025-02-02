package fr.iutrodez.tourneecommercial.utils.api;

import android.content.Context;
import com.android.volley.RequestQueue;
import com.google.gson.Gson;
import fr.iutrodez.tourneecommercial.modeles.Client;
import fr.iutrodez.tourneecommercial.modeles.Itineraire;
import fr.iutrodez.tourneecommercial.modeles.dto.ItineraireDTO;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.stream.Collectors;

public class ItineraireApiRequest extends ApiRessource {
    private static final String RESOURCE_NAME = "itineraire";
    private static final String TAG = "ItineraireApiRequest";

    public ItineraireApiRequest(RequestQueue requestQueue) {
        super(requestQueue);
    }

    public void create(Context context, String name, int distance, List<Client> clients, SuccessCallback<String> successCallback, ErrorCallback errorCallback) {
        String url = RESOURCE_NAME + "/";
        JSONObject body = itinerartyDTOCreation(name, clients, distance);
        super.postWithToken(context, url, body, response -> {
            successCallback.onSuccess(extractMessage(response));
        }, errorCallback::onError);
    }

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

    public void getPage(Context context, int page, SuccessCallback<List<Itineraire>> successCallback, ErrorCallback errorCallback) {
        String url = RESOURCE_NAME + "/lazy" + "?page=" + page;
        super.getWithTokenAsArray(context, url, response -> {
            successCallback.onSuccess(extractItineraires(response));
        }, errorCallback::onError);
    }

    public void generate(Context context, List<Client> clients, SuccessCallback<ItineraireDTO> successCallback, ErrorCallback errorCallback) {
        String ids = clients.stream()
                .map(Client::get_id)
                .map(String::valueOf)
                .collect(Collectors.joining(","));
        String url = RESOURCE_NAME + "/generate?clients=" + ids;
        super.getWithToken(context, url, response -> {
            successCallback.onSuccess(formatAndExtractClientsGeneration(clients, response));
        }, errorCallback::onError);
    }

    public void getOne(Context context, long id, SuccessCallback<ItineraireDTO> successCallback, ErrorCallback errorCallback) {
        String url = RESOURCE_NAME + "/" + id;
        super.getWithToken(context, url, response -> {
            successCallback.onSuccess(extractItinerairesDTOOne(response));
        }, errorCallback::onError);

    }

    public void update(Context context, long id, String name, int distance, List<Client> clients, SuccessCallback<String> successCallback, ErrorCallback errorCallback) {
        String url = RESOURCE_NAME + "/" + id;
        JSONObject body = itinerartyDTOCreation(name, clients, distance);
        super.putWithToken(context, url, body, response -> {
            successCallback.onSuccess(extractMessage(response));
        }, errorCallback::onError);
    }

    public void delete(Context context, long id, SuccessCallback<String> successCallback, ErrorCallback errorCallback) {
        String url = RESOURCE_NAME + "/" + id;
        super.deleteWithToken(context, url, response -> {
            successCallback.onSuccess(extractMessage(response));
        }, errorCallback::onError);
    }

    private List<Itineraire> extractItineraires(JSONArray response) {
        List<Itineraire> itineraires = new java.util.ArrayList<>();
        for (int i = 0; i < response.length(); i++) {
            JSONObject itineraire = null;
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

    private String extractMessage(JSONObject json) {
        try {
            return json.getString("message");
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    private ItineraireDTO formatAndExtractClientsGeneration(List<Client> clients, JSONObject response) {
        int kilometres = 0;
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

    private ItineraireDTO extractItinerairesDTOOne(JSONObject response) {
        Gson gson = new Gson();
        return gson.fromJson(response.toString(), ItineraireDTO.class);
    }
}
