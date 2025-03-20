package fr.iutrodez.tourneecommercial.utils.api;

import android.content.Context;
import android.content.SharedPreferences;
import androidx.annotation.NonNull;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;
import fr.iutrodez.tourneecommercial.model.Parcours;
import fr.iutrodez.tourneecommercial.utils.helper.SavedParcoursHelper;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;

/**
 * Classe ApiRessource pour gérer les requêtes API.
 *
 * @author Benjamin NICOL, Enzo CLUZEL, Ahmed BRIBACH, Leïla BAUDROIT
 */
public class ApiRessource {

    private static final String BASE_URL = "http://direct.bennybean.fr:9090/";
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
     * Récupère le token d'authentification.
     * Le rafraîchit de manière blocante s'il est expiré.
     *
     * @param context le contexte de l'application
     * @return le token d'authentification
     */
    private static String getToken(Context context) {
        SharedPreferences pref = context.getSharedPreferences("user", MODE_PRIVATE);
        long expirationTime = pref.getLong("expiration", 0);

        if (expirationTime < System.currentTimeMillis()) {
            String email = pref.getString("email", "");
            String password = pref.getString("password", "");
            ApiRequest.getInstance().auth.refreshToken(context, email, password);
        }
        return context.getSharedPreferences("user", MODE_PRIVATE).getString("token", "");
    }

    /**
     * Envoie une requête en attendant un objet JSON en retour, en envoyant le token au backend
     *
     * @param context   le contexte de l'application
     * @param method    le verbe HTTP utilisé
     * @param url       le chemin du endpoint et les paramètres utilisés
     * @param body      le corps de la requête
     * @param onSuccess le listener en cas de réussite de la requête, prend en argument un JSONObject
     * @param onError   le listener en cas d'erreur
     */
    private void jsonObjectRequestWithToken(@NonNull Context context,
                                            int method,
                                            String url,
                                            JSONObject body,
                                            Response.Listener<JSONObject> onSuccess,
                                            Response.ErrorListener onError) {
        JsonRequest<JSONObject> request = new JsonObjectRequest(method,
                BASE_URL + url,
                body,
                obj -> {
                    sendAnyUnsentParcours(context);
                    onSuccess.onResponse(obj);
                },
                onError) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + getToken(context));
                return headers;
            }
        };
        requestQueue.add(request);
    }

    /**
     * Envoie une requête en attendant un tableau JSON en retour, en envoyant le token au backend
     *
     * @param context   le contexte de l'application
     * @param url       le chemin du endpoint et les paramètres utilisés
     * @param onSuccess le listener en cas de réussite de la requête, prend en argument un JSONArray
     * @param onError   le listener en cas d'erreur
     */
    private void jsonArrayRequestWithToken(@NonNull Context context,
                                           String url,
                                           Response.Listener<JSONArray> onSuccess,
                                           Response.ErrorListener onError) {
        JsonRequest<JSONArray> request = new JsonArrayRequest(Request.Method.GET, BASE_URL + url, null, onSuccess, onError) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + getToken(context));
                return headers;
            }
        };
        requestQueue.add(request);
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
        jsonObjectRequestWithToken(context, Request.Method.GET, url, null, onSuccess, onError);
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
        jsonArrayRequestWithToken(context, url, onSuccess, onError);
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
        jsonObjectRequestWithToken(context, Request.Method.POST, url, body, onSuccess, onError);
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
        jsonObjectRequestWithToken(context, Request.Method.PUT, url, body, onSuccess, onError);
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
        jsonObjectRequestWithToken(context, Request.Method.DELETE, url, null, onSuccess, onError);
    }

    /**
     * Envoie à l'API les parcours qui n'ont pas pu être envoyés précédemment.
     *
     * @param context le contexte de l'application
     */
    private void sendAnyUnsentParcours(Context context) {
        SavedParcoursHelper savedParcoursHelper = new SavedParcoursHelper(context);
        if (savedParcoursHelper.isLockedForSending()) {
            return;
        }
        savedParcoursHelper.lockForSending();
        File f = savedParcoursHelper.getFileToSend();
        Parcours parcours = savedParcoursHelper.deserializeParcoursFromFile(f);
        if (parcours != null) {
            ApiRequest.getInstance().parcours.create(context, parcours,
                    response -> {
                        boolean succeeded = f.delete();
                        System.out.println(succeeded ?
                                "Parcours sent and deleted" :
                                "Couldn't delete file");
                        savedParcoursHelper.unlockForSending();
                    }, error -> {
                        System.out.println(error.getMessage());
                        System.out.println("Parcours saved to be sent later");

                        savedParcoursHelper.serializeToSendLater(parcours);
                        savedParcoursHelper.unlockForSending();
                    });
        } else {
            savedParcoursHelper.unlockForSending();
        }
    }
}
