package fr.iutrodez.tourneecommercial.utils.api;

import com.android.volley.RequestQueue;
import org.json.JSONException;
import org.json.JSONObject;

public class UtilisateurApiRequest extends ApiRessource {
    private static final String RESOURCE_NAME = "utilisateur";

    public UtilisateurApiRequest(RequestQueue requestQueue) {
        super(requestQueue);
    }

    public void getSelf() {
        System.out.println("NOT IMPLEMETED");
    }

    public void create(String name, String firstName, String email,
                       String adress, String postalCode, String city,
                       String password,
                       SuccessCallback<String> successCallback, ErrorCallback errorCallback) {
        String url = RESOURCE_NAME + "/";
        JSONObject body = new JSONObject();
        try {
            body.put("nom", name);
            body.put("prenom", firstName);
            body.put("email", email);
            body.put("libelleAdresse", adress);
            body.put("codePostal", postalCode);
            body.put("ville", city);
            body.put("motDePasse", password);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        super.post(url, body, response -> {
            String message = extractMessage(response);
            successCallback.onSuccess(message);
        }, errorCallback::onError);
    }

    public void updateSelf() {
        System.out.println("NOT IMPLEMETED");
    }

    private String extractMessage(JSONObject json) {
        try {
            return json.getString("message");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}
