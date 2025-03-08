package fr.iutrodez.tourneecommercial.utils.api;

import android.content.Context;
import android.util.Log;
import com.android.volley.RequestQueue;
import com.google.gson.Gson;
import fr.iutrodez.tourneecommercial.model.dto.Parameter;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Gère les requêtes API relatives à l'utilisateur.
 */
public class UtilisateurApiRequest extends ApiRessource {
    private static final String RESOURCE_NAME = "utilisateur";
    private static final String TAG = "UtilisateurApiRequest";

    public UtilisateurApiRequest(RequestQueue requestQueue) {
        super(requestQueue);
    }

    /**
     * Récupère les informations de l'utilisateur.
     *
     * @param context         le contexte de l'application
     * @param successCallback le callback en cas de succès
     * @param errorCallback   le callback en cas d'erreur
     */
    public void getSelf(Context context, SuccessCallback<Parameter> successCallback, ErrorCallback errorCallback) {
        String url = RESOURCE_NAME + "/";
        super.getWithToken(context, url, response -> {
            Parameter parametre = extractParameter(response);
            successCallback.onSuccess(parametre);
        }, errorCallback::onError);
    }

    /**
     * Crée un nouvel utilisateur.
     *
     * @param name            le nom de l'utilisateur
     * @param firstName       le prénom de l'utilisateur
     * @param email           l'email de l'utilisateur
     * @param address         l'adresse de l'utilisateur
     * @param postalCode      le code postal de l'utilisateur
     * @param city            la ville de l'utilisateur
     * @param password        le mot de passe de l'utilisateur
     * @param successCallback le callback en cas de succès
     * @param errorCallback   le callback en cas d'erreur
     */
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

    /**
     * Met à jour les informations de l'utilisateur.
     *
     * @param context         le contexte de l'application
     * @param name            le nom de l'utilisateur
     * @param firstname       le prénom de l'utilisateur
     * @param email           l'email de l'utilisateur
     * @param successCallback le callback en cas de succès
     * @param errorCallback   le callback en cas d'erreur
     */
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

    /**
     * Met à jour le mot de passe de l'utilisateur.
     *
     * @param context         le contexte de l'application
     * @param password        le nouveau mot de passe
     * @param successCallback le callback en cas de succès
     * @param errorCallback   le callback en cas d'erreur
     */
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

    /**
     * Extrait le message d'un objet JSON.
     *
     * @param json l'objet JSON contenant le message
     * @return le message extrait ou null en cas d'erreur
     */
    private String extractMessage(JSONObject json) {
        try {
            return json.getString("message");
        } catch (JSONException e) {
            Log.e(TAG, "Failed to extract message from JSON", e);
        }
        return null;
    }

    /**
     * Convertit un objet JSON en un objet Parameter.
     *
     * @param json l'objet JSON à convertir
     * @return l'objet Parameter converti
     */
    private Parameter extractParameter(JSONObject json) {
        Gson gson = new Gson();
        return gson.fromJson(json.toString(), Parameter.class);
    }
}
