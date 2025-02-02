package fr.iutrodez.tourneecommercial.utils.api;

import com.android.volley.RequestQueue;
import com.google.gson.Gson;
import fr.iutrodez.tourneecommercial.modeles.dto.JwtToken;
import org.json.JSONObject;

public class AuthApiRequest extends ApiRessource {

    private static final String RESOURCE_NAME = "auth";

    public AuthApiRequest(RequestQueue requestQueue) {
        super(requestQueue);
    }

    public void login(String user, String password, SuccessCallback<JwtToken> successCallback, ErrorCallback errorCallback) {
        String url = RESOURCE_NAME + "/";

        JSONObject body = new JSONObject();
        try {
            body.put("email", user);
            body.put("motDePasse", password);
        } catch (Exception e) {
            e.printStackTrace();
        }

        super.post(url, body, response -> {
            JwtToken jwtToken = extractJwtToken(response);
            successCallback.onSuccess(jwtToken);
        }, errorCallback::onError);
    }

    private JwtToken extractJwtToken(JSONObject json) {
        Gson gson = new Gson();
        return gson.fromJson(json.toString(), JwtToken.class);
    }
}
