package fr.iutrodez.tourneecommercial.utils;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

public class ApiRequest {
    private static RequestQueue requestQueue;
    private static final String API_URL = "http://10.0.2.2:9090/";

    private static final String API_URL = "http://10.0.2.2:9090/";

    public interface ApiResponseCallback {
        void onSuccess(JSONObject response);

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
        url = API_URL + url;
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

}
