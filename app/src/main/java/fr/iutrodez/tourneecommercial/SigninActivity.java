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
import fr.iutrodez.tourneecommercial.modeles.Adresse;
import fr.iutrodez.tourneecommercial.utils.AdressAdapter;
import fr.iutrodez.tourneecommercial.utils.api.ApiRequest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class SigninActivity extends AppCompatActivity {

    private final static ApiRequest API_REQUEST = ApiRequest.getInstance();
    private final static String PASSWORD_PATTERN = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@#$%^&+=_]).+$";
    private final static String EMAIL_PATTERN = "^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,6}$";

    private List<Adresse> suggestedAdress;

    private Dialog dialog;
    private EditText name;
    private EditText firstname;
    private EditText email;
    private TextView adress;
    private EditText password;
    private EditText passwordConfirmation;

    private AdressAdapter adressAdapter;

    private Adresse selectedAdress;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signin_activity);
        Objects.requireNonNull(getSupportActionBar()).setTitle(R.string.signin);

        name = findViewById(R.id.editText_name);
        firstname = findViewById(R.id.editText_firstname);
        email = findViewById(R.id.editText_email);
        adress = findViewById(R.id.textView_adress);
        password = findViewById(R.id.editText_password);
        passwordConfirmation = findViewById(R.id.editText_passwordConfirmation);
        Button signin = findViewById(R.id.button_signin);

        signin.setOnClickListener(this::onClickSignin);
        findViewById(R.id.button_login).setOnClickListener(this::onClickLogin);
        suggestedAdress = new ArrayList<>();
        setupAdress();
    }

    private void setupAdress() {
        adress.setOnClickListener(this::onClickAdress);
    }

    private void onClickSignin(View view) {
        if (checkFields()) {
            String name = this.name.getText().toString();
            String firstname = this.firstname.getText().toString();
            String email = this.email.getText().toString();
            String address = selectedAdress.getLibelle();
            String postalCode = selectedAdress.getCodePostal();
            String city = selectedAdress.getVille();
            String password = this.password.getText().toString();
            API_REQUEST.utilisateur.create(name, firstname, email, address, postalCode, city, password,
                    response -> {
                        Toast.makeText(this, response, Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(this, LoginActivity.class));
                    },
                    error -> {
                        Toast.makeText(this, error.toString(), Toast.LENGTH_SHORT).show();
                        Log.e("SigninActivity", "Error during sign-in", error);
                    }
            );
        }
    }

    private void onClickLogin(View view) {
        startActivity(new Intent(this, LoginActivity.class));
    }

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

    private boolean checkAddress() {
        if (selectedAdress == null) {
            adress.setError(getString(R.string.empty_field_error));
            return false;
        } else {
            adress.setError(null);
            return true;
        }
    }

    private boolean checkName(String nom) {
        if (nom.trim().isEmpty()) {
            this.name.setError(getString(R.string.empty_field_error));
            return false;
        }
        return true;
    }

    private boolean checkFirstname(String firstname) {
        if (firstname.trim().isEmpty()) {
            this.firstname.setError(getString(R.string.empty_field_error));
            return false;
        }
        return true;
    }

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

    private void onClickAdress(View v) {
        // Préparer le dialog
        dialog = new Dialog(SigninActivity.this);
        dialog.setContentView(R.layout.dialog_searchable_spinner);
        dialog.getWindow().setLayout(650, 800);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();

        // Récupérer les éléments du dialog
        EditText research = dialog.findViewById(R.id.editText_research);
        ListView list = dialog.findViewById(R.id.listView_list);
        TextView title = dialog.findViewById(R.id.title);
        title.setText(R.string.adress_research_title);

        // Initialiser l'adapter
        adressAdapter = new AdressAdapter(
                SigninActivity.this,
                android.R.layout.simple_list_item_1,
                suggestedAdress);
        list.setAdapter(adressAdapter);

        // Ajout de l'écouteur sur le champ de recherche
        research.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                API_REQUEST.ban.getSuggestions(s.toString(), response -> {
                    suggestedAdress.clear();
                    suggestedAdress.addAll(Arrays.asList(response));
                    adressAdapter.notifyDataSetChanged();
                }, VolleyError::printStackTrace);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        if (selectedAdress != null) {
            research.setText(selectedAdress.toString());
        }

        list.setOnItemClickListener((adapterView, view, i, l) -> {
            selectedAdress = adressAdapter.getItem(i);
            adress.setText(selectedAdress.toString());
            dialog.dismiss();
        });
    }
}
