package fr.iutrodez.tourneecommercial.utils;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import fr.iutrodez.tourneecommercial.R;

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
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST,
                API_URL + url,
                postData,
                callback::onSuccess,
                callback::onError
        );
        requestQueue.add(jsonObjectRequest);
    }

    public static void modifierClient(Context context,String id,JSONObject postData,ApiResponseCallback callback) {
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(context);
        }
        String token = context.getSharedPreferences("user", Context.MODE_PRIVATE).getString("token", "");
        String url = API_URL + "client/modifier/?id="+id;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST,
                url,
                postData,
                callback::onSuccess,
                callback::onError
        ) {
            @Override
            public Map<String, String> getHeaders()  {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + token);

                return headers;
            }
        };

        requestQueue.add(jsonObjectRequest);
    }

    public static void creationClient(Context context, String url, JSONObject postData, ApiResponseCallback callback) {
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(context);
        }
        String token = context.getSharedPreferences("user", Context.MODE_PRIVATE).getString("token", "");
        url = API_URL + url;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.PUT,
                 url,
                postData,
                callback::onSuccess,
                callback::onError
        ) {
            @Override
            public Map<String, String> getHeaders()  {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + token);
                return headers;
            }
        };
        requestQueue.add(jsonObjectRequest);
    }

    public static void recupererClient(Context context,String id,ApiResponseCallback callback) {
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(context);
        }
        String token = context.getSharedPreferences("user", Context.MODE_PRIVATE).getString("token", "");

        String url = API_URL + "client/recuperer/?id="+id;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                callback::onSuccess,
                callback::onError
        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + token);
                return headers;
            }
        };
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

    public static void deleteItineraire(Context context, long itineraireId, ApiResponseCallback<JSONObject> callback) {
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(context);
        }
        String token = getAPI_KEY(context);
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
        return token[0];
    }

    public static void getClients(Context context, ApiResponseCallback<JSONArray> callback) {
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(context);
        }
        String token = getAPI_KEY(context);
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET,
                API_URL + "client/",
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
    public static void getNombreClient(Context context,  ApiResponseCallback<JSONObject> callback) {
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(context);
        }
        String token = getAPI_KEY(context);
        JsonObjectRequest jsonArrayRequest = new JsonObjectRequest(
                Request.Method.GET,
                API_URL + "client/number",
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

    public static void getClientsBy30(Context context, int page, ApiResponseCallback<JSONArray> callback){
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(context);
        }
        String token = getAPI_KEY(context);
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET,
                API_URL + "client/lazy/?page="+page,
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
}
