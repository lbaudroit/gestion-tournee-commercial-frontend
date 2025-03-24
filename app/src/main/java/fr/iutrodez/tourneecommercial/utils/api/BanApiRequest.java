package fr.iutrodez.tourneecommercial.utils.api;

import android.util.Log;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import fr.iutrodez.tourneecommercial.model.Adresse;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Classe permettant de faire des requêtes à l'API de la BAN (Base Adresse Nationale)
 *
 * @author Benjamin NICOL, Enzo CLUZEL, Ahmed BRIBACH, Leïla BAUDROIT
 */
public class BanApiRequest {
    private final RequestQueue requestQueue;

    public BanApiRequest(RequestQueue requestQueue) {
        this.requestQueue = requestQueue;
    }

    /**
     * Envoie une requête à l'API de la BAN pour obtenir des suggestions d'adresses.
     *
     * @param recherche       La chaîne de recherche pour les adresses.
     * @param successCallback Le callback à appeler en cas de succès avec un tableau d'objets Adresse.
     * @param errorCallback   Le callback à appeler en cas d'erreur.
     */
    public void getSuggestions(String recherche, SuccessCallback<Adresse[]> successCallback, ErrorCallback errorCallback) {
        String url = "https://api-adresse.data.gouv.fr/search/?q=" + recherche + "&limit=20" + "&type=housenumber";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                response -> {
                    Adresse[] adresses = extractAddresses(response);
                    successCallback.onSuccess(adresses);
                },
                errorCallback::onError
        );
        requestQueue.add(jsonObjectRequest);
    }

    /**
     * Extrait un tableau d'objets Adresse à partir de la réponse JSON de l'API.
     *
     * @param json L'objet JSON contenant les données de réponse de l'API.
     * @return Un tableau d'objets Adresse ou null en cas d'erreur.
     */
    private Adresse[] extractAddresses(JSONObject json) {
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
            Log.e("Error", e.getMessage());
        }
        return null;
    }
}
