package fr.iutrodez.tourneecommercial.utils;

import android.content.Context;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import fr.iutrodez.tourneecommercial.modeles.Client;

public class ApiRequest {
    private static RequestQueue requestQueue;

    private static final String API_URL = "http://10.0.2.2:9090/";

    public interface ApiResponseCallback {
        void onSuccess(JSONObject response);

        void onError(VolleyError error);
    }

    public interface ApiArrayResponseCallback {
        void onSuccess(JSONArray response);

        void onError(VolleyError error);
    }

    public static void connexion(Context context, String url, JSONObject postData, ApiResponseCallback callback) {
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(context);
        }
        System.out.println(API_URL + url);
        System.out.println(postData.toString());
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST,
                API_URL + url,
                postData,
                callback::onSuccess,
                callback::onError
        );
        requestQueue.add(jsonObjectRequest);
    }

    public static void inscription(Context context, String url, JSONObject postData, ApiResponseCallback callback) {
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(context);
        }
        System.out.println(API_URL + url);
        System.out.println(postData.toString());
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST,
                API_URL + url,
                postData,
                callback::onSuccess,
                callback::onError
        );
        requestQueue.add(jsonObjectRequest);
    }

    public static void creationClient(Context context, String url, JSONObject postData, ApiResponseCallback callback) {
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(context);
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST,
                API_URL + url,
                postData,
                callback::onSuccess,
                callback::onError
        );
        requestQueue.add(jsonObjectRequest);
    }

    public static void validationAdresse(Context context, String libelleAdresse, String codePostal, String ville, ApiResponseCallback callback) {
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(context);
        }
        String url = "https://api-adresse.data.gouv.fr/search/?q=" + libelleAdresse + "&postcode=" + codePostal + "&city=" + ville + "&limit=1" + "&type=housenumber" + "&autocomplete=0";

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                callback::onSuccess,
                callback::onError
        );
        requestQueue.add(jsonObjectRequest);
    }

    public static void fetchAddressSuggestions(Context context, String query, ApiResponseCallback callback) {
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(context);
        }
        String url = "https://api-adresse.data.gouv.fr/search/?q=" + query + "&limit=20" + "&type=housenumber";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                callback::onSuccess,
                callback::onError
        );
        requestQueue.add(jsonObjectRequest);
    }

    public static void recupererClients(Context context, ApiArrayResponseCallback callback) {
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(context);
        }
        String url = API_URL + "client/";
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET,
                url,
                null,
                callback::onSuccess,
                callback::onError
        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                String token = context.getSharedPreferences("user", Context.MODE_PRIVATE).getString("token", "");
                headers.put("Authorization", "Bearer " + token);
                return headers;
            }
        };
        requestQueue.add(jsonArrayRequest);
    }

    public static void recupererItineraire(Context context, Long id, ApiArrayResponseCallback callback) {
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(context);
        }
        String url = API_URL + "itineraire/?id=" + id;
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET,
                url,
                null,
                callback::onSuccess,
                callback::onError
        ) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                String token = context.getSharedPreferences("user", Context.MODE_PRIVATE).getString("token", "");
                headers.put("Authorization", "Bearer " + token);
                return headers;
            }
        };
        requestQueue.add(jsonArrayRequest);
    }

    public static void genererItineraire(Context context, List<Client> clients, ApiArrayResponseCallback callback) {
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(context);
        }
        String ids = clients.stream()
                .map(Client::get_id)
                .map(String::valueOf)
                .collect(Collectors.joining(","));
        String url = API_URL + "itineraire/generer/?clients=" + ids;
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET,
                url,
                null,
                callback::onSuccess,
                callback::onError
        ) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                String token = context.getSharedPreferences("user", Context.MODE_PRIVATE).getString("token", "");
                headers.put("Authorization", "Bearer " + token);
                return headers;
            }
        };
        requestQueue.add(jsonArrayRequest);
    }

    public static JSONObject creationDTOCreationItineraire(String nom, List<Client> clients) throws JSONException {
            // Création de l'objet avec les données
            JSONObject itineraireData = new JSONObject();
            itineraireData.put("nom", nom);
            itineraireData.put("clients", clients.stream()
                    .map(Client::get_id)
                    .collect(Collectors.toList()));

            return itineraireData;
    }

    public static void creationItineraire(Context context, String nom, List<Client> clients, ApiResponseCallback callback) throws JSONException {
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(context);
        }

        JSONObject donneesAEnvoyer = creationDTOCreationItineraire(nom, clients);

        String url = API_URL + "itineraire/creer/";
        JsonObjectRequest jsonArrayRequest = new JsonObjectRequest(
                Request.Method.GET,
                url,
                donneesAEnvoyer,
                callback::onSuccess,
                callback::onError
        ) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                String token = context.getSharedPreferences("user", Context.MODE_PRIVATE).getString("token", "");
                headers.put("Authorization", "Bearer " + token);
                return headers;
            }
        };
        requestQueue.add(jsonArrayRequest);
    }

    public static Client jsonToClient(JSONObject json) {
        Gson gson = new Gson();

        // Définir le type pour une liste de clients
        Type clientType = TypeToken.get(Client.class).getType();

        // Convertir le JSON en liste d'objets Client
        return gson.fromJson(json.toString(), clientType);
    }
}
