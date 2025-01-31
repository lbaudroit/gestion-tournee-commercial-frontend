package fr.iutrodez.tourneecommercial.utils.api;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import fr.iutrodez.tourneecommercial.modeles.Adresse;
import org.json.JSONArray;
import org.json.JSONObject;

public class BanApiRequest {
    private RequestQueue requestQueue;

    public BanApiRequest(RequestQueue requestQueue) {
        this.requestQueue = requestQueue;
    }

    public void getSuggestions(String recherche, SuccessCallback<Adresse[]> successCallback, ErrorCallback errorCallback) {
        String url = "https://api-adresse.data.gouv.fr/search/?q=" + recherche + "&limit=20" + "&type=housenumber";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                response -> {
                    Adresse[] adresses = extractAdresses(response);
                    successCallback.onSuccess(adresses);
                },
                errorCallback::onError
        );
        requestQueue.add(jsonObjectRequest);

    }

    private Adresse[] extractAdresses(JSONObject json) {
        try {
            JSONArray features = json.getJSONArray("features");
            Adresse[] adresses = new Adresse[features.length()];
            for (int i = 0; i < features.length(); i++) {
                JSONObject featureProperties = features.getJSONObject(i).getJSONObject("properties");
                adresses[i] = new Adresse(featureProperties.getString("name"),
                        featureProperties.getString("postcode"),
                        featureProperties.getString("city"));
            }
            return adresses;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
