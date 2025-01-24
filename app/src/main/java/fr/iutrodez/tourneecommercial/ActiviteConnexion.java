package fr.iutrodez.tourneecommercial;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.VolleyError;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;

import fr.iutrodez.tourneecommercial.utils.ApiRequest;

/**
 * Activité de connexion pour l'application Tournée Commerciale.
 * Permet à l'utilisateur de se connecter en vérifiant ses identifiants.
 *
 * @author Benjamin NICOL,
 * Leïla BAUDROIT,
 * Enzo CLUZEL,
 * Ahmed BRIBACH
 */
public class ActiviteConnexion extends AppCompatActivity {

    private EditText email;

    private EditText password;

    /**
     * Méthode appelée lors de la création de l'activité.
     * Initialise les composants de l'interface utilisateur.
     *
     * @param savedInstanceState État sauvegardé de l'activité.
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activite_connexion);
        //eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJlbkBjbC5mciIsImlhdCI6MTczNzY0NjUyNywiZXhwIjoxNzM3NjQ4MzI3LCJqdGkiOiJkRUVrMTdrbVdHR1NUN2ZyZEtnZ05INlZyeEQ3UWFnSmx1ckpDUHdLR1BleFBGL21KVTgrWkNXZGpZK1BCQm5pYWNmV2pJL3NqeWRIMEtjbmZkUCtLUT09In0.JQDJ1iSwfxX4ro57yXKRANvE6lFb0dlohZKYVNtW-QE

        //String token = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJlbkBjbC5mciIsImlhdCI6MTczNzY0NjUyNywiZXhwIjoxNzM3NjQ4MzI3LCJqdGkiOiJkRUVrMTdrbVdHR1NUN2ZyZEtnZ05INlZyeEQ3UWFnSmx1ckpDUHdLR1BleFBGL21KVTgrWkNXZGpZK1BCQm5pYWNmV2pJL3NqeWRIMEtjbmZkUCtLUT09In0.JQDJ1iSwfxX4ro57yXKRANvE6lFb0dlohZKYVNtW-Q";
        // Enregistrement du token dans les SharedPreferences
        //getSharedPreferences("user", MODE_PRIVATE).edit().putString("token", token).apply();

        //ApiRequest.creationClient(this,"/client/creer/",);
        Objects.requireNonNull(getSupportActionBar()).setTitle(R.string.connexion);

        email = findViewById(R.id.field_email);
        password = findViewById(R.id.field_password);

        findViewById(R.id.btn_connexion).setOnClickListener(this::onClickEnvoyer);
        findViewById(R.id.btn_inscription).setOnClickListener(this::onClickGoToInscription);
    }

    /**
     * Méthode appelée lors du clic sur le bouton "Envoyer".
     * Vérifie la validité des champs et envoie les données à l'API pour authentification.
     *
     * @param view Vue qui a déclenché l'événement.
     */
    private void onClickEnvoyer(View view) {

        // Prévérification de la validité des champs
        boolean auth = true;

        // On vérifie que les champs ne sont pas vides
        if (email.getText().toString().isEmpty()) {
            email.setError(getString(R.string.empty_fields_error));
            auth = false;
        } else if (password.getText().toString().isEmpty()) {
            password.setError(getString(R.string.empty_fields_error));
            auth = false;
        }

        // On vérifie que l'email est valide
        String emailPattern = "^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,6}$";
        if (!email.getText().toString().matches(emailPattern)) {
            email.setError(getString(R.string.invalid_email_error));
            auth = false;
        }

        // On vérifie que le mot de passe fait au moins 8 caractères
        if (password.getText().toString().length() < 8) {
            password.setError(getString(R.string.password_length_error));
            auth = false;
        }

        // On vérifie que le mot de passe contient au moins une majuscule, une minuscule et un chiffre
        String passwordPattern = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).+$";
        if (!password.getText().toString().matches(passwordPattern)) {
            password.setError(getString(R.string.password_pattern_error));
            auth = false;
        }

        // Si l'authentification est réussie, on redirige vers l'activité principale
        if (auth) {
            // Utilisation de Volley pour envoyer les données à l'API et récupérer le token
            // Création de l'objet JSON
            JSONObject postData = new JSONObject();
            try {
                postData.put("email", email.getText().toString());
                postData.put("motDePasse", password.getText().toString());
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
            System.out.println(postData);
            // Création de la requête
            ApiRequest.connexion(this, "auth/authentifier", postData, new ApiRequest.ApiResponseCallback() {
                @Override
                public void onSuccess(JSONObject response) {
                    // Récupération du token
                    try {
                        System.out.println(response);
                        String token = response.getString("token");
                        // Enregistrement du token dans les SharedPreferences
                        getSharedPreferences("user", MODE_PRIVATE).edit().putString("token", token).apply();
                        // Si l'authentification est réussie, on enregistre le token dans les SharedPreferences et on redirige vers l'activité principale
                        startActivity(new Intent(ActiviteConnexion.this, ActivitePrincipale.class));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onError(VolleyError error) {
                    Toast.makeText(ActiviteConnexion.this, R.string.invalid_params_connexion_error, Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    /**
     * Méthode appelée lors du clic sur le bouton "Inscription".
     * Redirige l'utilisateur vers l'activité d'inscription.
     *
     * @param view Vue qui a déclenché l'événement.
     */
    private void onClickGoToInscription(View view) {
        startActivity(new Intent(this, ActiviteInscription.class));
        Toast.makeText(this, R.string.todo, Toast.LENGTH_SHORT).show();
    }
}