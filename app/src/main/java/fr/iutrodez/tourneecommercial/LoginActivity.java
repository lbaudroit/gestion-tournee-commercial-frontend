package fr.iutrodez.tourneecommercial;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import fr.iutrodez.tourneecommercial.model.dto.JwtToken;
import fr.iutrodez.tourneecommercial.utils.api.ApiRequest;

import java.util.Objects;

import static fr.iutrodez.tourneecommercial.utils.api.ApiRequest.hasInternetCapability;

/**
 * Activité de connexion pour l'application Tournée Commerciale.
 * Permet à l'utilisateur de se connecter en vérifiant ses identifiants.
 *
 * @author Benjamin NICOL, Enzo CLUZEL, Ahmed BRIBACH, Leïla BAUDROIT
 */
public class LoginActivity extends AppCompatActivity {

    public static final String PASSWORD_PATTERN = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@#$%^&+=_]).+$";
    private final static String EMAIL_PATTERN = "^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,6}$";
    private static ApiRequest apiRequest;
    private EditText email;

    private EditText password;

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
    }

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
        if (hasInternetCapability(this)) {
            Toast.makeText(this, R.string.no_internet_error, Toast.LENGTH_LONG).show();
            inputsValid = false;
        }

        if (inputsValid) {
            apiRequest.auth.login(extracted_email, extracted_password, (jwtToken) -> {
                setSharedPreferences(jwtToken);
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

    /**
     * Enregistre le token d'authentification et les informations de connexion dans les SharedPreferences.
     *
     * @param jwtToken les informations du token d'authentification
     */
    private void setSharedPreferences(JwtToken jwtToken) {
        long expirationTime = System.currentTimeMillis() + jwtToken.getExpiration();
        getSharedPreferences("user", MODE_PRIVATE)
                .edit()
                .putString("token", jwtToken.getToken())
                .putLong("expiration", expirationTime)
                .putString("email", email.getText().toString())
                .putString("password", password.getText().toString())
                .apply();
    }
}