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
public class LoginActivity extends AppCompatActivity {

    private final static String EMAIL_PATTERN = "^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,6}$";
    public static final String PASSWORD_PATTERN = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@#$%^&+=_]).+$";
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
        setContentView(R.layout.login_activity);
        Objects.requireNonNull(getSupportActionBar()).setTitle(R.string.login);

        email = findViewById(R.id.editText_email);
        password = findViewById(R.id.editText_password);

        findViewById(R.id.button_login).setOnClickListener(this::onClickEnvoyer);
        findViewById(R.id.button_signup).setOnClickListener(this::onClickGoToInscription);
        apiRequest = ApiRequest.buildInstance(this);
        //TODO : Supprimer les lignes suivantes
        email.setText("en@cl.fr");
        password.setText("Enzo_123");
        findViewById(R.id.button_login).performClick();
    }

    /**
     * Méthode appelée lors du clic sur le bouton "Envoyer".
     * Vérifie la validité des champs et envoie les données à l'API pour authentification.
     *
     * @param view Vue qui a déclenché l'événement.
     */
    private void onClickEnvoyer(View view) {
        boolean inputsValid = true;
        String extracted_email = this.email.getText().toString();
        String extracted_password = this.password.getText().toString();

        if (extracted_email.isEmpty()) {
            email.setError(getString(R.string.empty_fields_error));
            inputsValid = false;
        } else if (extracted_password.isEmpty()) {
            password.setError(getString(R.string.empty_fields_error));
            inputsValid = false;
        }

        if (!extracted_email.matches(EMAIL_PATTERN)) {
            email.setError(getString(R.string.invalid_email_error));
            inputsValid = false;
        }

        if (extracted_password.length() < 8) {
            password.setError(getString(R.string.password_length_error));
            inputsValid = false;
        }

        if (!extracted_password.matches(PASSWORD_PATTERN)) {
            password.setError(getString(R.string.password_pattern_error));
            inputsValid = false;
        }
        if (inputsValid) {
            apiRequest.auth.login(extracted_email, extracted_password, (jwtToken) -> {
                long expirationTime = System.currentTimeMillis() + jwtToken.getExpiration();
                setSharedPreferences(jwtToken, expirationTime);
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }, error -> Toast.makeText(LoginActivity.this, R.string.invalid_login_params_error, Toast.LENGTH_LONG).show());
        }
    }

    /**
     * Méthode appelée lors du clic sur le bouton "Inscription".
     * Redirige l'utilisateur vers l'activité d'inscription.
     *
     * @param view Vue qui a déclenché l'événement.
     */
    private void onClickGoToInscription(View view) {
        startActivity(new Intent(this, SignupActivity.class));
    }

    private void setSharedPreferences(JwtToken jwtToken, long expirationTime) {
        getSharedPreferences("user", MODE_PRIVATE).edit().putString("token", jwtToken.getToken()).apply();
        getSharedPreferences("user", MODE_PRIVATE).edit().putLong("expiration", expirationTime).apply();
        getSharedPreferences("user", MODE_PRIVATE).edit().putString("email", email.getText().toString()).apply();
        getSharedPreferences("user", MODE_PRIVATE).edit().putString("password", password.getText().toString()).apply();
    }
}