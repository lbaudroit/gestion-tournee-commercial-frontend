package fr.iutrodez.tourneecommercial.utils.api;

import android.content.Context;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import fr.iutrodez.tourneecommercial.R;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;

/**
 * Classe ApiRessource pour gérer les requêtes API.
 */
public class ApiRessource {

    private static final String BASE_URL = "http://10.0.2.2:9090/";
    private static RequestQueue requestQueue;

    /**
     * Constructeur de la classe ApiRessource.
     *
     * @param requestQueue la file de requêtes à utiliser
     */
    public ApiRessource(RequestQueue requestQueue) {
        ApiRessource.requestQueue = requestQueue;
    }

    /**
     * Retourne la file de requêtes.
     *
     * @return la file de requêtes
     */
    public static RequestQueue getRequestQueue() {
        return requestQueue;
    }

    /**
     * Récupère le token d'authentification.
     *
     * @param context le contexte de l'application
     * @return le token d'authentification
     */
    public static String getToken(Context context) {
        long expirationTime = context.getSharedPreferences("user", MODE_PRIVATE).getLong("expiration", 0);
        if (expirationTime < System.currentTimeMillis()) {
            String email = context.getSharedPreferences("user", MODE_PRIVATE).getString("email", "");
            String password = context.getSharedPreferences("user", MODE_PRIVATE).getString("password", "");
            ApiRequest apiRequest = ApiRequest.getInstance();
            apiRequest.auth.login(email, password, jwtToken -> {
                context.getSharedPreferences("user", MODE_PRIVATE).edit().putString("token", jwtToken.getToken()).apply();
                context.getSharedPreferences("user", MODE_PRIVATE).edit().putLong("expiration", jwtToken.getExpiration()).apply();
            }, error -> Toast.makeText(context, R.string.invalid_login_params_error, Toast.LENGTH_LONG).show());
        }
        return context.getSharedPreferences("user", MODE_PRIVATE).getString("token", "");
    }

    /**
     * Envoie une requête POST.
     *
     * @param url       l'URL de la requête
     * @param body      le corps de la requête
     * @param onSuccess le listener pour le succès
     * @param onError   le listener pour l'erreur
     */
    public void post(String url, JSONObject body, Response.Listener<JSONObject> onSuccess, Response.ErrorListener onError) {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, BASE_URL + url, body, onSuccess, onError);
        requestQueue.add(jsonObjectRequest);
    }

    /**
     * Envoie une requête GET avec un token d'authentification.
     *
     * @param context   le contexte de l'application
     * @param url       l'URL de la requête
     * @param onSuccess le listener pour le succès
     * @param onError   le listener pour l'erreur
     */
    public void getWithToken(Context context, String url, Response.Listener<JSONObject> onSuccess, Response.ErrorListener onError) {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, BASE_URL + url, null, onSuccess, onError) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + getToken(context));
                return headers;
            }
        };
        requestQueue.add(jsonObjectRequest);
    }

    /**
     * Envoie une requête GET avec un token d'authentification.
     *
     * @param context   le contexte de l'application
     * @param url       l'URL de la requête
     * @param onSuccess le listener pour le succès
     * @param onError   le listener pour l'erreur
     */
    public void getWithTokenAsArray(Context context, String url, Response.Listener<JSONArray> onSuccess, Response.ErrorListener onError) {
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, BASE_URL + url, null, onSuccess, onError) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + getToken(context));
                return headers;
            }
        };
        requestQueue.add(jsonArrayRequest);
    }

    /**
     * Envoie une requête POST avec un token d'authentification.
     *
     * @param context   le contexte de l'application
     * @param url       l'URL de la requête
     * @param body      le corps de la requête
     * @param onSuccess le listener pour le succès
     * @param onError   le listener pour l'erreur
     */
    public void postWithToken(Context context, String url, JSONObject body, Response.Listener<JSONObject> onSuccess, Response.ErrorListener onError) {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, BASE_URL + url, body, onSuccess, onError) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + getToken(context));
                return headers;
            }
        };
        requestQueue.add(jsonObjectRequest);
    }

    /**
     * Envoie une requête PUT avec un token d'authentification.
     *
     * @param context   le contexte de l'application
     * @param url       l'URL de la requête
     * @param body      le corps de la requête
     * @param onSuccess le listener pour le succès
     * @param onError   le listener pour l'erreur
     */
    public void putWithToken(Context context, String url, JSONObject body, Response.Listener<JSONObject> onSuccess, Response.ErrorListener onError) {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.PUT, BASE_URL + url, body, onSuccess, onError) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + getToken(context));
                return headers;
            }
        };
        requestQueue.add(jsonObjectRequest);
    }

    /**
     * Envoie une requête DELETE avec un token d'authentification.
     *
     * @param context   le contexte de l'application
     * @param url       l'URL de la requête
     * @param onSuccess le listener pour le succès
     * @param onError   le listener pour l'erreur
     */
    public void deleteWithToken(Context context, String url, Response.Listener<JSONObject> onSuccess, Response.ErrorListener onError) {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.DELETE, BASE_URL + url, null, onSuccess, onError) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + getToken(context));
                return headers;
            }
        };
        requestQueue.add(jsonObjectRequest);
    }

}
