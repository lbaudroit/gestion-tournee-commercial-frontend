package fr.iutrodez.tourneecommercial.utils.api;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import org.json.JSONObject;

public class ApiRessource {

    private final static String BASE_URL = "http://10.0.2.2:9090/";

    public interface SuccessCallback<T> {
        void onSuccess(T response);
    }

    public interface ErrorCallback {
        void onError(VolleyError error);
    }

    private static RequestQueue requestQueue;

    public ApiRessource(RequestQueue requestQueue) {
        ApiRessource.requestQueue = requestQueue;
    }

    public void get(String url, Response.Listener<JSONObject> onSuccess, Response.ErrorListener onError) {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,
                BASE_URL + url,
                null,
                onSuccess,
                onError
        );
        requestQueue.add(jsonObjectRequest);
    }

    public void post(String url, JSONObject body, Response.Listener<JSONObject> onSuccess, Response.ErrorListener onError) {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST,
                BASE_URL + url,
                body,
                onSuccess,
                onError
        );
        requestQueue.add(jsonObjectRequest);
    }

}
