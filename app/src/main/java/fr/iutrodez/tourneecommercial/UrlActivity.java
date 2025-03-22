package fr.iutrodez.tourneecommercial;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Objects;

/**
 * Activité de connexion pour l'application Tournée Commerciale.
 * Permet à l'utilisateur de se connecter en vérifiant ses identifiants.
 *
 * @author Benjamin NICOL, Enzo CLUZEL, Ahmed BRIBACH, Leïla BAUDROIT
 */
public class UrlActivity extends AppCompatActivity {
    private EditText url;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.url_activity);
        Objects.requireNonNull(getSupportActionBar()).setTitle(R.string.login);
        url = findViewById(R.id.editText_url);
        url.setText(getSharedPreferences("user", MODE_PRIVATE).getString("url", "https://direct.bennybean.fr:9090/"));

        findViewById(R.id.button_validate).setOnClickListener(this::onClickValidate);
        findViewById(R.id.button_return).setOnClickListener(this::onClickReturn);
    }

    private void onClickReturn(View view) {
        startActivity(new Intent(this, LoginActivity.class));
    }

    private void onClickValidate(View view) {
        // COntrole que l'url soit une url valide
        String urlGiven = this.url.getText().toString();
        if (urlGiven.isEmpty()) {
            url.setError(getString(R.string.empty_field_error));
        } else if (!Patterns.WEB_URL.matcher(urlGiven).matches()) {
            url.setError(getString(R.string.invalid_url_error));
        } else if (!urlGiven.startsWith("http://") && !urlGiven.startsWith("https://")) {
            url.setError(getString(R.string.invalid_url_error_header));
        } else if (!urlGiven.endsWith("/")) {
            url.setError(getString(R.string.invalid_url_error_trailer));
        } else {
            setSharedPreferences(url.getText().toString());
            startActivity(new Intent(this, LoginActivity.class));
        }
    }

    /**
     * Enregistre le token d'authentification et les informations de connexion dans les SharedPreferences.
     *
     * @param url les informations du token d'authentification
     */
    private void setSharedPreferences(String url) {
        getSharedPreferences("user", MODE_PRIVATE)
                .edit()
                .putString("url", url)
                .apply();
    }
}