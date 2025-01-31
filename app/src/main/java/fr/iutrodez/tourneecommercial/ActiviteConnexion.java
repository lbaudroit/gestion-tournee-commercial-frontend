package fr.iutrodez.tourneecommercial;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import fr.iutrodez.tourneecommercial.modeles.dto.JwtToken;
import fr.iutrodez.tourneecommercial.utils.api.ApiRequest;

import java.util.Objects;

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
    private static ApiRequest apiRequest;
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
        Objects.requireNonNull(getSupportActionBar()).setTitle(R.string.connexion);

        email = findViewById(R.id.field_email);
        password = findViewById(R.id.field_password);

        findViewById(R.id.btn_connexion).setOnClickListener(this::onClickEnvoyer);
        findViewById(R.id.btn_inscription).setOnClickListener(this::onClickGoToInscription);
        apiRequest = ApiRequest.buildInstance(this);
        //TODO : Supprimer les lignes suivantes
        email.setText("en@cl.fr");
        password.setText("Enzo_123");
        findViewById(R.id.btn_connexion).performClick();
    }

    /**
     * Méthode appelée lors du clic sur le bouton "Envoyer".
     * Vérifie la validité des champs et envoie les données à l'API pour authentification.
     *
     * @param view Vue qui a déclenché l'événement.
     */
    private void onClickEnvoyer(View view) {
        boolean auth = true;
        String extracted_email = this.email.getText().toString();
        String extracted_password = this.password.getText().toString();

        if (extracted_email.isEmpty()) {
            email.setError(getString(R.string.empty_fields_error));
            auth = false;
        } else if (extracted_password.isEmpty()) {
            password.setError(getString(R.string.empty_fields_error));
            auth = false;
        }

        String emailPattern = "^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,6}$";
        if (!extracted_email.matches(emailPattern)) {
            email.setError(getString(R.string.invalid_email_error));
            auth = false;
        }

        if (extracted_password.length() < 8) {
            password.setError(getString(R.string.password_length_error));
            auth = false;
        }

        String passwordPattern = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@#$%^&+=_]).+$";
        if (!extracted_password.matches(passwordPattern)) {
            password.setError(getString(R.string.password_pattern_error));
            auth = false;
        }
        if (auth) {
            apiRequest.auth.login(extracted_email, extracted_password, (jwtToken) -> {
                long expirationTime = System.currentTimeMillis() + jwtToken.getExpiration();
                setSharedPreferences(jwtToken, expirationTime);
                Intent intent = new Intent(ActiviteConnexion.this, ActivitePrincipale.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }, error -> {
                Toast.makeText(ActiviteConnexion.this, R.string.invalid_params_connexion_error, Toast.LENGTH_LONG).show();
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
    }

    private void setSharedPreferences(JwtToken jwtToken, long expirationTime) {
        getSharedPreferences("user", MODE_PRIVATE).edit().putString("token", jwtToken.getToken()).apply();
        getSharedPreferences("user", MODE_PRIVATE).edit().putLong("expiration", expirationTime).apply();
        getSharedPreferences("user", MODE_PRIVATE).edit().putString("email", email.getText().toString()).apply();
        getSharedPreferences("user", MODE_PRIVATE).edit().putString("password", password.getText().toString()).apply();
    }
}