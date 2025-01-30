package fr.iutrodez.tourneecommercial;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import fr.iutrodez.tourneecommercial.modeles.Adresse;
import fr.iutrodez.tourneecommercial.utils.AdaptateurAdresse;
import fr.iutrodez.tourneecommercial.utils.ApiRequest;

public class ActiviteInscription extends AppCompatActivity {

    private List<Adresse> adressesProposes;

    private Dialog dialog;
    private EditText nom;
    private EditText prenom;
    private EditText email;
    private TextView adresse_view;
    private EditText password;
    private EditText passwordConfirmation;
    private Button btnInscription;

    private AdaptateurAdresse adaptateurAdresse;

    private Adresse selectedAdresse;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activite_inscription);
        getSupportActionBar().setTitle(R.string.inscription);

        nom = findViewById(R.id.field_nom);
        prenom = findViewById(R.id.field_prenom);
        email = findViewById(R.id.field_email);
        adresse_view = findViewById(R.id.adresse_view);
        password = findViewById(R.id.field_password);
        passwordConfirmation = findViewById(R.id.field_password_again);
        btnInscription = findViewById(R.id.btn_inscription);

        btnInscription.setOnClickListener(this::onClickEnvoyer);
        findViewById(R.id.btn_connexion).setOnClickListener(this::onClickGoToConnexion);
        adressesProposes = new ArrayList<>();
        setupAdresse();
    }

    private void setupAdresse() {
        adresse_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Préparer le dialog
                dialog = new Dialog(ActiviteInscription.this);
                dialog.setContentView(R.layout.dialog_searchable_spinner);
                dialog.getWindow().setLayout(650, 800);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();

                // Récupérer les éléments du dialog
                EditText editText = dialog.findViewById(R.id.edit_text);
                ListView listView = dialog.findViewById(R.id.list_view);
                TextView titre = dialog.findViewById(R.id.titre);
                titre.setText(R.string.recherche_adresse);

                // Initialiser l'adapter
                adaptateurAdresse = new AdaptateurAdresse(
                        ActiviteInscription.this,
                        android.R.layout.simple_list_item_1,
                        adressesProposes);
                listView.setAdapter(adaptateurAdresse);

                // Ajout de l'écouteur sur le champ de recherche
                editText.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        ApiRequest.fetchAddressSuggestions(ActiviteInscription.this, s.toString(),
                                new ApiRequest.ApiResponseCallback<JSONObject>() {
                                    @Override
                                    public void onSuccess(JSONObject response) {
                                        try {
                                            List<Adresse> suggestions = new ArrayList<>();
                                            JSONArray features = response.getJSONArray("features");
                                            for (int i = 0; i < features.length(); i++) {
                                                JSONObject properties = features.getJSONObject(i).getJSONObject("properties");
                                                suggestions.add(new Adresse(
                                                        properties.getString("name"),
                                                        properties.getString("postcode"),
                                                        properties.getString("city")
                                                ));
                                            }
                                            adaptateurAdresse.clear();
                                            adaptateurAdresse.addAll(suggestions);
                                            adaptateurAdresse.notifyDataSetChanged();
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }

                                    @Override
                                    public void onError(VolleyError error) {
                                        error.printStackTrace();
                                    }
                                }
                        );
                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                    }
                });
                if (selectedAdresse != null) {
                    editText.setText(selectedAdresse.toString());
                }

                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        selectedAdresse = adaptateurAdresse.getItem(i);
                        adresse_view.setText(selectedAdresse.toString());
                        dialog.dismiss();
                    }
                });
            }
        });
    }

    private void onClickEnvoyer(View view) {
        if (checkFields()) {
            JSONObject postData = new JSONObject();
            try {
                postData.put("nom", nom.getText().toString());
                postData.put("prenom", prenom.getText().toString());
                postData.put("email", email.getText().toString());
                postData.put("libelleAdresse", selectedAdresse.getLibelle());
                postData.put("codePostal", selectedAdresse.getCodePostal());
                postData.put("ville", selectedAdresse.getVille());
                postData.put("motDePasse", password.getText().toString());
                System.out.println(postData.toString());
            } catch (JSONException e) {
                e.printStackTrace();
                return;
            }

            ApiRequest.inscription(this, "auth/creer", postData, new ApiRequest.ApiResponseCallback<JSONObject>() {
                @Override
                public void onSuccess(JSONObject response) {
                    Toast.makeText(ActiviteInscription.this, "Inscription réussie", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(ActiviteInscription.this, ActiviteConnexion.class));
                }

                @Override
                public void onError(VolleyError error) {
                    Toast.makeText(ActiviteInscription.this, "Erreur: " + error.toString(), Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    private void onClickGoToConnexion(View view) {
        startActivity(new Intent(this, ActiviteConnexion.class));
        Toast.makeText(this, R.string.todo, Toast.LENGTH_SHORT).show();
    }

    private boolean checkFields() {
        String nom = this.nom.getText().toString();
        String prenom = this.prenom.getText().toString();
        String email = this.email.getText().toString();
        String password = this.password.getText().toString();
        String passwordConfirmation = this.passwordConfirmation.getText().toString();
        // utilisation de l'opérateur & pour évaluer toutes les conditions
        return checkNom(nom) &
                checkPrenom(prenom) &
                checkEmail(email) &
                checkAdresse() &
                checkPassword(password) &
                checkPasswordConfirmation(password, passwordConfirmation);
    }

    private boolean checkAdresse() {
        if (selectedAdresse == null) {
            adresse_view.setError(getString(R.string.empty_field_error));
            return false;
        } else {
            adresse_view.setError(null);
            return true;
        }
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
        String passwordPattern = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@#$%^&+=_]).+$";
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
