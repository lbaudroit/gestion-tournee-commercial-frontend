package fr.iutrodez.tourneecommercial.utils.api;

import android.content.Context;
import com.android.volley.RequestQueue;
import com.google.gson.Gson;
import fr.iutrodez.tourneecommercial.modeles.Client;
import fr.iutrodez.tourneecommercial.modeles.Parcours;
import fr.iutrodez.tourneecommercial.modeles.Visit;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ParcoursApiRequest extends ApiRessource {

    private static final String RESSOURCE_NAME = "parcours";

    public ParcoursApiRequest(RequestQueue requestQueue) {
        super(requestQueue);
    }

    public void create(Context context, Parcours parcours, SuccessCallback<String> successCallback, ErrorCallback errorCallback) {
        String url = RESSOURCE_NAME + "/create";
        JSONObject body = parcoursDTOCreation(parcours);
        System.out.println(body);
        super.postWithToken(context, url, body, response -> {
            successCallback.onSuccess(extractMessage(response));
        }, errorCallback::onError);
    }

    public void getProspectsForNotifications(Context context, double latitude, double longitude, SuccessCallback<List<Client>> successCallback, ErrorCallback errorCallback) {
        String url = RESSOURCE_NAME + "/prospects/notifications?latitude=" + latitude + "&longitude=" + longitude;
        super.getWithTokenAsArray(context, url, response -> {
            successCallback.onSuccess(extractClients(response));
        }, errorCallback::onError);
    }

    private static JSONObject parcoursDTOCreation(Parcours parcours) {
        JSONObject parcoursData = new JSONObject();
        try {
            JSONArray etapesArray = new JSONArray();
            for (Object visite : parcours.getVisits()) {
                if (visite != null) {
                    JSONObject visiteObj = new JSONObject();
                    Visit v = (Visit) visite;
                    visiteObj.put("nom", v.getName());
                    visiteObj.put("visite", v.isVisited());

                    JSONObject coordinates = new JSONObject();
                    coordinates.put("latitude", v.getCoordonnees().getLatitude());
                    coordinates.put("longitude", v.getCoordonnees().getLongitude());

                    visiteObj.put("coordonnees", coordinates);
                    etapesArray.put(visiteObj);
                } else {
                    System.out.println("Visite est null !");
                }
            }
            parcoursData.put("nom", parcours.getName());
            parcoursData.put("etapes", etapesArray);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        return parcoursData;
    }

    private String extractMessage(JSONObject json) {
        try {
            return json.getString("message");
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

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
