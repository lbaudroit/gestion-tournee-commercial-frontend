package fr.iutrodez.tourneecommercial.utils.api;

import android.content.Context;

import com.android.volley.RequestQueue;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import fr.iutrodez.tourneecommercial.modeles.Parcours;
import fr.iutrodez.tourneecommercial.modeles.Visit;

public class ParcoursApiRequest extends ApiRessource {

    private static final String RESSOURCE_NAME = "parcours";

    public ParcoursApiRequest(RequestQueue requestQueue) {
        super(requestQueue);
    }

    private static JSONObject parcoursDTOCreation(Parcours parcours) {
        JSONObject parcoursData = new JSONObject();
        try {

            JSONArray etapesArray = new JSONArray();
            for (Object visite : parcours.getVisits()) {
                if (visite != null) {  // Vérifier que l'objet n'est pas null
                    JSONObject visiteObj = new JSONObject();
                    Visit v = (Visit) visite; // Cast en Visite
                    visiteObj.put("nom", v.getName());  // Ajoutez les vrais attributs de Visite
                    visiteObj.put("visite", v.isVisited());

                    JSONObject coordinates = new JSONObject();
                    coordinates.put("latitude",v.getCoordonnees().getLatitude());
                    coordinates.put("longitude",v.getCoordonnees().getLongitude());

                    visiteObj.put("coordonnees",coordinates );
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


    public void create(Context context,  Parcours parcours, SuccessCallback<String> successCallback, ErrorCallback errorCallback) {
        String url = RESSOURCE_NAME + "/create";
        JSONObject body = parcoursDTOCreation(parcours);
        System.out.println(body);
        super.postWithToken(context, url, body, response -> {
            successCallback.onSuccess(extractMessage(response));
        }, errorCallback::onError);
    }

    private String extractMessage(JSONObject json) {
        try {
            return json.getString("message");
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

}
