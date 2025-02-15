package fr.iutrodez.tourneecommercial.utils.api;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.google.gson.Gson;

import org.json.JSONObject;

import fr.iutrodez.tourneecommercial.R;
import fr.iutrodez.tourneecommercial.modeles.dto.JwtToken;

public class AuthApiRequest extends ApiRessource {

    private static final String RESOURCE_NAME = "auth";

    public AuthApiRequest(RequestQueue requestQueue) {
        super(requestQueue);
    }

    /**
     * Se connecte à l'API avec un email et un mot de passe et récupère un token d'authentification.
     *
     * @param user            l'email de l'utilisateur
     * @param password        le mot de passe de l'utilisateur
     * @param successCallback le callback en cas de succès
     * @param errorCallback   le callback en cas d'erreur
     */
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

    /**
     * Rafraîchit le token d'authentification.
     * Méthode blocante : ne pas appeler sur le thread principal.
     *
     * @param context  le contexte de l'application
     * @param email    l'email de l'utilisateur
     * @param password le mot de passe de l'utilisateur
     */
    public void refreshToken(Context context, String email, String password) {
        ApiRequest apiRequest = ApiRequest.getInstance();
        boolean[] done = {false};
        apiRequest.auth.login(email, password, jwtToken -> {

            long expirationTime = System.currentTimeMillis() + jwtToken.getExpiration();
            context.getSharedPreferences("user", MODE_PRIVATE)
                    .edit()
                    .putString("token", jwtToken.getToken())
                    .putLong("expiration", expirationTime)
                    .apply();

            done[0] = true;
        }, error -> {
            Toast.makeText(context, R.string.invalid_login_params_error, Toast.LENGTH_LONG).show();
            done[0] = true;
        });
        // On attend la fin de la requête
        try {
            while (!done[0]) {
                Thread.sleep(100);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private JwtToken extractJwtToken(JSONObject json) {
        Gson gson = new Gson();
        return gson.fromJson(json.toString(), JwtToken.class);
    }
}
