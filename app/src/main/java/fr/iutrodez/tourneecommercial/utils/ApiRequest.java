package fr.iutrodez.tourneecommercial.utils;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.widget.Toast;
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

import java.util.HashMap;
import java.util.Map;

import fr.iutrodez.tourneecommercial.R;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import fr.iutrodez.tourneecommercial.modeles.Client;

public class ApiRequest {
    private static RequestQueue requestQueue;

    private static final String API_URL = "http://10.0.2.2:9090/";

    public interface ApiResponseCallback<T> {
        void onSuccess(T response);

        void onError(VolleyError error);
    }

    public static void connexion(Context context, String url, JSONObject postData, ApiResponseCallback<JSONObject> callback) {
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

    public static void inscription(Context context, String url, JSONObject postData, ApiResponseCallback<JSONObject> callback) {
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

    public static void creationClient(Context context, String url, JSONObject postData, ApiResponseCallback<JSONObject> callback) {
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

    public static void validationAdresse(Context context, String libelleAdresse, String codePostal, String ville, ApiResponseCallback<JSONObject> callback) {

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

    public static void fetchAddressSuggestions(Context context, String query, ApiResponseCallback<JSONObject> callback) {
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

    public static void fetchNombresItineraires(Context context, String url, ApiResponseCallback<JSONObject> callback) {
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(context);
        }
        String token = getAPI_KEY(context);
        JsonObjectRequest jsonArrayRequest = new JsonObjectRequest(
                Request.Method.GET,
                API_URL + url,
                null,
                callback::onSuccess,
                callback::onError
        ) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + token);
                return headers;
            }
        };
        requestQueue.add(jsonArrayRequest);
    }

    public static void fetchItineraires(Context context, String url, ApiResponseCallback<JSONArray> callback) {
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(context);
        }
        String token = getAPI_KEY(context);
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET,
                API_URL + url,
                null,
                callback::onSuccess,
                callback::onError
        ) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + token);
                return headers;
            }
        };
        requestQueue.add(jsonArrayRequest);
    }

    public static void recupererClients(Context context, ApiResponseCallback<JSONArray> callback) {
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(context);
        }
        String token = getAPI_KEY(context);
        String url = API_URL + "client/";
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
                headers.put("Authorization", "Bearer " + token);
                return headers;
            }
        };
        requestQueue.add(jsonArrayRequest);
    }

    public static void deleteItineraire(Context context, long itineraireId, ApiResponseCallback<JSONObject> callback) {
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(context);
        }
        String token = getAPI_KEY(context);
        System.out.println(API_URL + "itineraire/supprimer/?id=" + itineraireId);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.DELETE,
                API_URL + "itineraire/supprimer/?id=" + itineraireId,
                null,
                callback::onSuccess,
                callback::onError
        ) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + token);
                return headers;
            }
        };
        requestQueue.add(jsonObjectRequest);
    }

    public static void getParametres(Context context, ApiResponseCallback<JSONObject> callback) {
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(context);
        }
        String token = getAPI_KEY(context);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,
                API_URL + "utilisateur/",
                null,
                callback::onSuccess,
                callback::onError
        ) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + token);
                return headers;
            }
        };
        requestQueue.add(jsonObjectRequest);
    }

    public static void recupererItineraire(Context context, Long id, ApiResponseCallback<JSONArray> callback) {
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(context);
        }
        String token = getAPI_KEY(context);
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
                headers.put("Authorization", "Bearer " + token);
                return headers;
            }
        };
        requestQueue.add(jsonArrayRequest);
    }

    public static void modifierParametres(Context context, JSONObject postData, ApiResponseCallback<JSONObject> callback) {
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(context);
        }
        String token = getAPI_KEY(context);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST,
                API_URL + "utilisateur/modifier/",
                postData,
                callback::onSuccess,
                callback::onError
        ) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + token);
                return headers;
            }
        };
        requestQueue.add(jsonObjectRequest);
    }

    public static void genererItineraire(Context context, List<Client> clients, ApiResponseCallback<JSONObject> callback) {
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(context);
        }
        String token = getAPI_KEY(context);
        String ids = clients.stream()
                .map(Client::get_id)
                .map(String::valueOf)
                .collect(Collectors.joining(","));
        String url = API_URL + "itineraire/generer/?clients=" + ids;
        JsonObjectRequest jsonArrayRequest = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                callback::onSuccess,
                callback::onError
        ) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + token);
                return headers;
            }
        };
        requestQueue.add(jsonArrayRequest);
    }

    public static String getAPI_KEY(Context context) {
        final String[] token = {context.getSharedPreferences("user", MODE_PRIVATE).getString("token", "")};
        long expirationTime = context.getSharedPreferences("user", MODE_PRIVATE).getLong("expiration", 0);
        if (expirationTime < System.currentTimeMillis()) {
            JSONObject postData = new JSONObject();
            try {
                postData.put("email", context.getSharedPreferences("user", MODE_PRIVATE).getString("email", ""));
                postData.put("motDePasse", context.getSharedPreferences("user", MODE_PRIVATE).getString("password", ""));
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
            ApiRequest.connexion(context, "auth/authentifier", postData, new ApiRequest.ApiResponseCallback<JSONObject>() {
                @Override
                public void onSuccess(JSONObject response) {
                    // Récupération du token
                    try {
                        token[0] = response.getString("token");
                        // Current time in milliseconde
                        long expirationTime = System.currentTimeMillis() + response.getLong("expiration");
                        // Enregistrement du token dans les SharedPreferences
                        context.getSharedPreferences("user", MODE_PRIVATE).edit().putLong("expiration", expirationTime).apply();
                        context.getSharedPreferences("user", MODE_PRIVATE).edit().putString("token", token[0]).apply();

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onError(VolleyError error) {
                    Toast.makeText(context, R.string.invalid_params_connexion_error, Toast.LENGTH_LONG).show();
                }
            });
        }
        System.out.println("after : " + context.getSharedPreferences("user", MODE_PRIVATE).getLong("expiration", 0));
        return token[0];
    }

    private static JSONObject creationDTOCreationItineraire(String nom, List<Client> clients, int distance) throws JSONException {
            // Création de l'objet avec les données
            JSONObject itineraireData = new JSONObject();
            List<String> clientIds = clients.stream()
                    .map(Client::get_id)
                    .collect(Collectors.toList());
            itineraireData.put("nom", nom);
            itineraireData.put("idClients", new JSONArray(clientIds));
            itineraireData.put("distance", distance);

            return itineraireData;
    }

    public static void creationItineraire(Context context, String nom, List<Client> clients, int distance, ApiResponseCallback<JSONObject> callback) throws JSONException {
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(context);
        }

        JSONObject donneesAEnvoyer = creationDTOCreationItineraire(nom, clients, distance);
        System.out.println(donneesAEnvoyer.toString());

        String url = API_URL + "itineraire/creer/";
        JsonObjectRequest jsonArrayRequest = new JsonObjectRequest(
                Request.Method.POST,
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
