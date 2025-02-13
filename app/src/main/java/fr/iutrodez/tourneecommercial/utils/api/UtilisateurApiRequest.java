package fr.iutrodez.tourneecommercial.utils.api;

import android.content.Context;
import android.util.Log;
import com.android.volley.RequestQueue;
import com.google.gson.Gson;
import fr.iutrodez.tourneecommercial.modeles.dto.Parameter;
import org.json.JSONException;
import org.json.JSONObject;

public class UtilisateurApiRequest extends ApiRessource {
    private static final String RESOURCE_NAME = "utilisateur";
    private static final String TAG = "UtilisateurApiRequest";

    public UtilisateurApiRequest(RequestQueue requestQueue) {
        super(requestQueue);
    }

    public void getSelf(Context context, SuccessCallback<Parameter> successCallback, ErrorCallback errorCallback) {
        String url = RESOURCE_NAME + "/";
        super.getWithToken(context, url, response -> {
            Parameter parametre = extractParameter(response);
            successCallback.onSuccess(parametre);
        }, errorCallback::onError);
    }

    public void create(String name, String firstName, String email,
                       String address, String postalCode, String city,
                       String password,
                       SuccessCallback<String> successCallback, ErrorCallback errorCallback) {
        String url = RESOURCE_NAME + "/";
        JSONObject body = new JSONObject();
        try {
            body.put("nom", name);
            body.put("prenom", firstName);
            body.put("email", email);
            body.put("libelleAdresse", address);
            body.put("codePostal", postalCode);
            body.put("ville", city);
            body.put("motDePasse", password);
        } catch (JSONException e) {
            Log.e(TAG, "Failed to create JSON body", e);
        }

        super.post(url, body, response -> {
            String message = extractMessage(response);
            successCallback.onSuccess(message);
        }, errorCallback::onError);
    }

    public void updateSelf(Context context, String name, String firstname, String email,
                           SuccessCallback<String> successCallback, ErrorCallback errorCallback) {
        String url = RESOURCE_NAME + "/";
        JSONObject body = new JSONObject();
        try {
            body.put("nom", name);
            body.put("prenom", firstname);
            body.put("email", email);
        } catch (JSONException e) {
            Log.e(TAG, "Failed to create JSON body", e);
        }
        super.putWithToken(context, url, body, response -> {
            String message = extractMessage(response);
            successCallback.onSuccess(message);
        }, errorCallback::onError);
    }

    public void updatePassword(Context context, String password,
                               SuccessCallback<String> successCallback, ErrorCallback errorCallback) {
        String url = RESOURCE_NAME + "/password";
        JSONObject body = new JSONObject();
        try {
            body.put("password", password);
        } catch (JSONException e) {
            Log.e(TAG, "Failed to create JSON body", e);
        }
        super.putWithToken(context, url, body, response -> {
            String message = extractMessage(response);
            successCallback.onSuccess(message);
        }, errorCallback::onError);
    }

    private String extractMessage(JSONObject json) {
        try {
            return json.getString("message");
        } catch (JSONException e) {
            Log.e(TAG, "Failed to extract message from JSON", e);
        }
        return null;
    }

    private Parameter extractParameter(JSONObject json) {
        Gson gson = new Gson();
        return gson.fromJson(json.toString(), Parameter.class);
    }
}
