package fr.iutrodez.tourneecommercial.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.android.volley.VolleyError;

import org.json.JSONException;
import org.json.JSONObject;

import fr.iutrodez.tourneecommercial.ActivitePrincipale;
import fr.iutrodez.tourneecommercial.R;
import fr.iutrodez.tourneecommercial.utils.ApiRequest;

public class FragmentParametres extends Fragment {

    public ActivitePrincipale parent;

    private EditText nom;
    private EditText prenom;
    private EditText email;
    private JSONObject userParams;

    public static FragmentParametres newInstance() {
        return new FragmentParametres();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof ActivitePrincipale) {
            parent = (ActivitePrincipale) context;
        } else {
            throw new ClassCastException("Le contexte doit être une instance d'ActivitePrincipale.");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        // Récupérer les parametres de l'utilisateur
        return inflater.inflate(R.layout.fragment_parametres, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Récupérer les parametres de l'utilisateur
        nom = view.findViewById(R.id.field_nom);
        prenom = view.findViewById(R.id.field_prenom);
        email = view.findViewById(R.id.field_email);
        view.findViewById(R.id.btn_modification).setOnClickListener(this::modifier);

        ApiRequest.getParametres(getContext(), new ApiRequest.ApiResponseCallback<JSONObject>() {
            @Override
            public void onSuccess(JSONObject response) {
                try {
                    nom.setText(response.getString("nom"));
                    prenom.setText(response.getString("prenom"));
                    email.setText(response.getString("email"));
                    userParams = response;
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(VolleyError error) {
                Toast.makeText(getContext(), R.string.error_fetching_params, Toast.LENGTH_LONG).show();
            }
        });
    }

    public void modifier(View view) {
        if (checkFields()) {
            try {
                // Ajouter gestion etteur
                userParams.put("nom", nom.getText().toString());
                userParams.put("prenom", prenom.getText().toString());
                userParams.put("email", email.getText().toString());
                System.out.println(userParams.toString());
                ApiRequest.modifierParametres(getContext(), userParams, new ApiRequest.ApiResponseCallback<JSONObject>() {
                    @Override
                    public void onSuccess(JSONObject response) {
                        Toast.makeText(getContext(), R.string.params_updated, Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onError(VolleyError error) {
                        Toast.makeText(getContext(), R.string.error_updating_params, Toast.LENGTH_LONG).show();
                    }
                });
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private boolean checkFields() {
        String nom = this.nom.getText().toString();
        String prenom = this.prenom.getText().toString();
        String email = this.email.getText().toString();
        // utilisation de l'opérateur & pour évaluer toutes les conditions
        return checkNom(nom) &
                checkPrenom(prenom) &
                checkEmail(email);
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
}