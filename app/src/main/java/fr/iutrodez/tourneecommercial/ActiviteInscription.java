package fr.iutrodez.tourneecommercial;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import fr.iutrodez.tourneecommercial.utils.AdaptateurAdresse;
import fr.iutrodez.tourneecommercial.utils.ApiRequest;

public class ActiviteInscription extends AppCompatActivity {

    private Handler handler = new Handler();
    private Runnable fetchSuggestionsRunnable;
    private EditText nom;
    private EditText prenom;
    private EditText email;
    private AutoCompleteTextView
            libelleAdresse;
    private EditText codePostal;
    private EditText ville;
    private EditText password;
    private EditText passwordConfirmation;
    private Button btnInscription;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activite_inscription);
        getSupportActionBar().setTitle(R.string.inscription);

        nom = findViewById(R.id.field_nom);
        prenom = findViewById(R.id.field_prenom);
        email = findViewById(R.id.field_email);
        libelleAdresse = findViewById(R.id.field_adresse);
        codePostal = findViewById(R.id.field_code_postal);
        ville = findViewById(R.id.field_ville);
        password = findViewById(R.id.field_password);
        passwordConfirmation = findViewById(R.id.field_password_again);
        btnInscription = findViewById(R.id.btn_inscription);

        btnInscription.setOnClickListener(this::onClickEnvoyer);
        findViewById(R.id.btn_connexion).setOnClickListener(this::onClickGoToConnexion);

        // Add TextWatcher for address autocomplete
        libelleAdresse.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 2) {
                    if (fetchSuggestionsRunnable != null) {
                        handler.removeCallbacks(fetchSuggestionsRunnable);
                    }
                    fetchSuggestionsRunnable = () -> ApiRequest.fetchAddressSuggestions(ActiviteInscription.this, s.toString(),
                            new ApiRequest.ApiResponseCallback() {
                                @Override
                                public void onSuccess(JSONObject response) {
                                    try {
                                        List<JSONObject> suggestions = new ArrayList<>();
                                        JSONArray features = response.getJSONArray("features");
                                        for (int i = 0; i < features.length(); i++) {
                                            JSONObject properties = features.getJSONObject(i).getJSONObject("properties");
                                            suggestions.add(properties);
                                        }
                                        AdaptateurAdresse adapter = new AdaptateurAdresse(ActiviteInscription.this,
                                                android.R.layout.simple_dropdown_item_1line,
                                                suggestions,
                                                ActiviteInscription.this::onClickSuggestions);
                                        libelleAdresse.setAdapter(adapter);
                                        adapter.notifyDataSetChanged();
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }

                                @Override
                                public void onError(VolleyError error) {
                                    error.printStackTrace();
                                }
                            });
                    handler.postDelayed(fetchSuggestionsRunnable, 300);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void onClickSuggestions(JSONObject adresse) {
        try {
            libelleAdresse.setText(adresse.getString("name"));
            codePostal.setText(adresse.getString("postcode"));
            ville.setText(adresse.getString("city"));
            libelleAdresse.dismissDropDown();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void onClickEnvoyer(View view) {
        if (checkFields()) {
            startActivity(new Intent(this, ActivitePrincipale.class));
        }
        return;
    }

    private void onClickGoToConnexion(View view) {
        startActivity(new Intent(this, ActiviteConnexion.class));
        Toast.makeText(this, R.string.todo, Toast.LENGTH_SHORT).show();
    }

    private boolean checkFields() {
        String nom = this.nom.getText().toString();
        String prenom = this.prenom.getText().toString();
        String email = this.email.getText().toString();
        String libelleAdresse = this.libelleAdresse.getText().toString();
        String codePostal = this.codePostal.getText().toString();
        String ville = this.ville.getText().toString();
        String password = this.password.getText().toString();
        String passwordConfirmation = this.passwordConfirmation.getText().toString();
        // utilisation de l'opérateur & pour évaluer toutes les conditions
        return checkNom(nom) &
                checkPrenom(prenom) &
                checkEmail(email) &
                checkAdresse(libelleAdresse,
                        codePostal, ville) &
                checkPassword(password) &
                checkPasswordConfirmation(password, passwordConfirmation);
    }

    private boolean checkNom(String nom) {
        boolean retour = true;
        if (nom.trim().isEmpty()) {
            this.nom.setError(getString(R.string.empty_field_error));
            retour = false;
        }
        return retour;
    }

    private boolean checkPrenom(String prenom) {
        boolean retour = true;
        if (prenom.trim().isEmpty()) {
            this.prenom.setError(getString(R.string.empty_field_error));
            retour = false;
        }
        return retour;
    }

    private boolean checkEmail(String email) {
        boolean retour = true;
        if (email.trim().isEmpty()) {
            this.email.setError(getString(R.string.empty_field_error));
            retour = false;
        }
        String emailPattern = "^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,6}$";
        if (!email.matches(emailPattern)) {
            this.email.setError(getString(R.string.invalid_email_error));
            retour = false;
        }
        return retour;
    }

    private boolean checkAdresse(String libelleAdresse, String codePostal, String ville) {
        final boolean[] retour = {true}; // utilisation d'un tableau pour pouvoir modifier la valeur dans une lambda
        if (libelleAdresse.trim().isEmpty()) {
            this.libelleAdresse.setError(getString(R.string.empty_field_error));
            retour[0] = false;
        }
        if (codePostal.trim().isEmpty()) {
            this.codePostal.setError(getString(R.string.empty_field_error));
            retour[0] = false;
        }
        if (ville.trim().isEmpty()) {
            this.ville.setError(getString(R.string.empty_field_error));
            retour[0] = false;
        }
        ApiRequest.validationAdresse(this, libelleAdresse, codePostal, ville, new ApiRequest.ApiResponseCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                try {
                    if (!((JSONObject) response.getJSONArray("features").get(0)).getJSONObject("properties").getString("name").equals(libelleAdresse)) {
                        ActiviteInscription.this.libelleAdresse.setError("Adresse non valide");
                        ActiviteInscription.this.codePostal.setError("Adresse non valide");
                        ActiviteInscription.this.ville.setError("Adresse non valide");
                        retour[0] = false;
                    }

                } catch (Exception e) {
                    ActiviteInscription.this.libelleAdresse.setError("Adresse non valide");
                    ActiviteInscription.this.codePostal.setError("Adresse non valide");
                    ActiviteInscription.this.ville.setError("Adresse non valide");
                    retour[0] = false;
                }
            }

            @Override
            public void onError(VolleyError error) {

            }
        });
        return retour[0];
    }

    private boolean checkPassword(String password) {
        boolean retour = true;
        if (password.trim().isEmpty()) {
            this.password.setError(getString(R.string.empty_field_error));
            retour = false;
        }
        if (password.length() < 8) {
            this.password.setError(getString(R.string.password_length_error));
            retour = false;
        }
        String passwordPattern = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).+$";
        if (!password.matches(passwordPattern)) {
            this.password.setError(getString(R.string.password_pattern_error));
            retour = false;
        }
        return true;
    }

    private boolean checkPasswordConfirmation(String password, String passwordConfirmation) {
        boolean retour = true;
        if (passwordConfirmation.trim().isEmpty()) {
            this.passwordConfirmation.setError(getString(R.string.empty_field_error));
            retour = false;
        }
        if (!password.equals(passwordConfirmation)) {
            this.passwordConfirmation.setError(getString(R.string.password_confirmation_error));
            retour = false;
        }
        return retour;
    }
}
