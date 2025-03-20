package fr.iutrodez.tourneecommercial;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.*;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.android.volley.VolleyError;
import fr.iutrodez.tourneecommercial.model.Adresse;
import fr.iutrodez.tourneecommercial.utils.adapter.AddressAdapter;
import fr.iutrodez.tourneecommercial.utils.api.ApiRequest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static fr.iutrodez.tourneecommercial.utils.api.ApiRequest.hasInternetCapability;

/**
 * Activité de création de compte
 *
 * @author Benjamin NICOL, Enzo CLUZEL, Ahmed BRIBACH, Leïla BAUDROIT
 */
public class SignupActivity extends AppCompatActivity {

    private final static ApiRequest API_REQUEST = ApiRequest.getInstance();
    private final static String PASSWORD_PATTERN = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@#$%^&+=_]).+$";
    private final static String EMAIL_PATTERN = "^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,6}$";

    private List<Adresse> suggestedAddress;

    private Dialog dialog;
    private EditText name;
    private EditText firstname;
    private EditText email;
    private TextView address;
    private EditText password;
    private EditText passwordConfirmation;

    private AddressAdapter addressAdapter;

    private Adresse selectedAddress;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup_activity);
        Objects.requireNonNull(getSupportActionBar()).setTitle(R.string.signup);

        name = findViewById(R.id.editText_name);
        firstname = findViewById(R.id.editText_firstname);
        email = findViewById(R.id.editText_email);
        address = findViewById(R.id.textView_address);
        password = findViewById(R.id.editText_password);
        passwordConfirmation = findViewById(R.id.editText_passwordConfirmation);
        Button signup = findViewById(R.id.button_signup);

        signup.setOnClickListener(this::onClickSignup);
        findViewById(R.id.button_login).setOnClickListener(this::onClickLogin);
        suggestedAddress = new ArrayList<>();
        setupAddress();
    }

    /**
     * Configure l'écouteur pour le champ d'adresse.
     */
    private void setupAddress() {
        address.setOnClickListener(this::onClickAddress);
    }

    /**
     * Méthode appelée lors du clic sur le bouton d'inscription.
     * Vérifie la connexion internet et les champs, puis envoie la requête d'inscription.
     *
     * @param view La vue qui a été cliquée.
     */
    private void onClickSignup(View view) {
        if (hasInternetCapability(this)) {
            Toast.makeText(this, R.string.no_internet_error, Toast.LENGTH_LONG).show();
        } else if (checkFields()) {
            String name = this.name.getText().toString();
            String firstname = this.firstname.getText().toString();
            String email = this.email.getText().toString();
            String address = selectedAddress.getLibelle();
            String postalCode = selectedAddress.getCodePostal();
            String city = selectedAddress.getVille();
            String password = this.password.getText().toString();
            API_REQUEST.utilisateur.create(name, firstname, email, address, postalCode, city, password,
                    response -> {
                        Toast.makeText(this, response, Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(this, LoginActivity.class));
                    },
                    error -> {
                        Toast.makeText(this, error.toString(), Toast.LENGTH_SHORT).show();
                        Log.e("SignupActivity", "Error during sign-in", error);
                    }
            );
        }
    }

    /**
     * Méthode appelée lors du clic sur le bouton de connexion.
     * Lance l'activité de connexion.
     *
     * @param view La vue qui a été cliquée.
     */
    private void onClickLogin(View view) {
        startActivity(new Intent(this, LoginActivity.class));
    }

    /**
     * Vérifie les champs du formulaire d'inscription.
     *
     * @return true si tous les champs sont valides, false sinon.
     */
    private boolean checkFields() {
        String nom = this.name.getText().toString();
        String prenom = this.firstname.getText().toString();
        String email = this.email.getText().toString();
        String password = this.password.getText().toString();
        String passwordConfirmation = this.passwordConfirmation.getText().toString();
        // utilisation de l'opérateur & pour évaluer toutes les conditions
        return checkName(nom) &
                checkFirstname(prenom) &
                checkEmail(email) &
                checkAddress() &
                checkPassword(password) &
                checkPasswordConfirmation(password, passwordConfirmation);
    }

    /**
     * Vérifie si une adresse a été sélectionnée.
     *
     * @return true si une adresse est sélectionnée, false sinon.
     */
    private boolean checkAddress() {
        if (selectedAddress == null) {
            address.setError(getString(R.string.empty_field_error));
            return false;
        } else {
            address.setError(null);
            return true;
        }
    }

    /**
     * Vérifie si le champ nom est valide.
     *
     * @param nom Le nom à vérifier.
     * @return true si le nom est valide, false sinon.
     */
    private boolean checkName(String nom) {
        if (nom.trim().isEmpty()) {
            this.name.setError(getString(R.string.empty_field_error));
            return false;
        }
        return true;
    }

    /**
     * Vérifie si le champ prénom est valide.
     *
     * @param firstname Le prénom à vérifier.
     * @return true si le prénom est valide, false sinon.
     */
    private boolean checkFirstname(String firstname) {
        if (firstname.trim().isEmpty()) {
            this.firstname.setError(getString(R.string.empty_field_error));
            return false;
        }
        return true;
    }

    /**
     * Vérifie si le champ email est valide.
     *
     * @param email L'email à vérifier.
     * @return true si l'email est valide, false sinon.
     */
    private boolean checkEmail(String email) {
        if (email.trim().isEmpty()) {
            this.email.setError(getString(R.string.empty_field_error));
            return false;
        }
        if (!email.matches(EMAIL_PATTERN)) {
            this.email.setError(getString(R.string.invalid_email_error));
            return false;
        }
        return true;
    }

    /**
     * Vérifie si le champ mot de passe est valide.
     *
     * @param password Le mot de passe à vérifier.
     * @return true si le mot de passe est valide, false sinon.
     */
    private boolean checkPassword(String password) {
        if (password.trim().isEmpty()) {
            this.password.setError(getString(R.string.empty_field_error));
            return false;
        }
        if (password.length() < 8) {
            this.password.setError(getString(R.string.password_length_error));
            return false;
        }
        if (!password.matches(PASSWORD_PATTERN)) {
            this.password.setError(getString(R.string.password_pattern_error));
            return false;
        }
        return true;
    }

    /**
     * Vérifie si le champ de confirmation du mot de passe est valide.
     *
     * @param password             Le mot de passe.
     * @param passwordConfirmation La confirmation du mot de passe.
     * @return true si la confirmation du mot de passe est valide, false sinon.
     */
    private boolean checkPasswordConfirmation(String password, String passwordConfirmation) {
        if (passwordConfirmation.trim().isEmpty()) {
            this.passwordConfirmation.setError(getString(R.string.empty_field_error));
            return false;
        }
        if (!password.equals(passwordConfirmation)) {
            this.passwordConfirmation.setError(getString(R.string.password_confirmation_error));
            return false;
        }
        return true;
    }

    /**
     * Méthode appelée lors du clic sur le champ d'adresse.
     * Affiche un dialog pour rechercher et sélectionner une adresse.
     *
     * @param v La vue qui a été cliquée.
     */
    private void onClickAddress(View v) {
        // Préparer le dialog
        dialog = new Dialog(SignupActivity.this);
        dialog.setContentView(R.layout.dialog_search_address);
        dialog.getWindow().setLayout(650, 800);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();

        // Récupérer les éléments du dialog
        EditText research = dialog.findViewById(R.id.editText_research);
        ListView list = dialog.findViewById(R.id.listView_list);
        TextView title = dialog.findViewById(R.id.textView_title);
        title.setText(R.string.address_search_title);

        // Initialiser l'adapter
        addressAdapter = new AddressAdapter(
                SignupActivity.this,
                android.R.layout.simple_list_item_1,
                suggestedAddress);
        list.setAdapter(addressAdapter);

        // Ajout de l'écouteur sur le champ de recherche
        research.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                API_REQUEST.ban.getSuggestions(s.toString(), response -> {
                    suggestedAddress.clear();
                    suggestedAddress.addAll(Arrays.asList(response));
                    addressAdapter.notifyDataSetChanged();
                }, VolleyError::printStackTrace);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        if (selectedAddress != null) {
            research.setText(selectedAddress.toString());
        }

        list.setOnItemClickListener((adapterView, view, i, l) -> {
            selectedAddress = addressAdapter.getItem(i);
            address.setText(selectedAddress.toString());
            dialog.dismiss();
        });
    }
}
