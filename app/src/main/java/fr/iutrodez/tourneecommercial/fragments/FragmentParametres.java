package fr.iutrodez.tourneecommercial.fragments;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import fr.iutrodez.tourneecommercial.ActivitePrincipale;
import fr.iutrodez.tourneecommercial.R;
import fr.iutrodez.tourneecommercial.utils.AdaptateurAdresse;
import fr.iutrodez.tourneecommercial.utils.ApiRequest;

public class FragmentParametres extends Fragment {

    public ActivitePrincipale parent;

    private EditText nom;
    private EditText prenom;
    private EditText email;
    private AutoCompleteTextView adresse;
    private EditText codePostal;
    private EditText ville;

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
        adresse = view.findViewById(R.id.field_adresse);
        codePostal = view.findViewById(R.id.field_code_postal);
        ville = view.findViewById(R.id.field_ville);
        view.findViewById(R.id.btn_modification).setOnClickListener(this::modifier);

        ApiRequest.getParametres(getContext(), new ApiRequest.ApiResponseCallback<JSONObject>() {
            @Override
            public void onSuccess(JSONObject response) {
                try {
                    nom.setText(response.getString("nom"));
                    prenom.setText(response.getString("prenom"));
                    email.setText(response.getString("email"));
                    adresse.setText(response.getString("libelleAdresse"));
                    codePostal.setText(response.getString("codePostale"));
                    ville.setText(response.getString("ville"));
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
        // Add TextWatcher for address autocomplete
        adresse.addTextChangedListener(new TextWatcher() {
            private Handler handler = new Handler();
            private Runnable fetchSuggestionsRunnable;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 2) {
                    if (fetchSuggestionsRunnable != null) {
                        handler.removeCallbacks(fetchSuggestionsRunnable);
                    }
                    fetchSuggestionsRunnable = () -> ApiRequest.fetchAddressSuggestions(getContext(), s.toString(),
                            new ApiRequest.ApiResponseCallback<JSONObject>() {
                                @Override
                                public void onSuccess(JSONObject response) {
                                    try {
                                        List<JSONObject> suggestions = new ArrayList<>();
                                        JSONArray features = response.getJSONArray("features");
                                        for (int i = 0; i < features.length(); i++) {
                                            JSONObject properties = features.getJSONObject(i).getJSONObject("properties");
                                            suggestions.add(properties);
                                        }
                                        AdaptateurAdresse adapter = new AdaptateurAdresse(getContext(),
                                                android.R.layout.simple_dropdown_item_1line,
                                                suggestions,
                                                FragmentParametres.this::onClickSuggestions);
                                        adresse.setAdapter(adapter);
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
            this.adresse.setText(adresse.getString("name"));
            codePostal.setText(adresse.getString("postcode"));
            ville.setText(adresse.getString("city"));
            this.adresse.dismissDropDown();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void modifier(View view) {
        if (checkFields()) {
            try {
                // Ajouter gestion etteur
                userParams.put("nom", nom.getText().toString());
                userParams.put("prenom", prenom.getText().toString());
                userParams.put("email", email.getText().toString());
                userParams.put("libelleAdresse", adresse.getText().toString());
                userParams.put("codePostale", codePostal.getText().toString());
                userParams.put("ville", ville.getText().toString());
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
        String adresse = this.adresse.getText().toString();
        String codePostal = this.codePostal.getText().toString();
        String ville = this.ville.getText().toString();
        // utilisation de l'opérateur & pour évaluer toutes les conditions
        return checkNom(nom) &
                checkPrenom(prenom) &
                checkEmail(email) &
                checkAdresse(adresse, codePostal, ville);
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

    private boolean checkAdresse(String adresse, String codePostal, String ville) {
        final boolean[] retour = {true}; // utilisation d'un tableau pour pouvoir modifier la valeur dans une lambda
        if (adresse.trim().isEmpty()) {
            this.adresse.setError(getString(R.string.empty_field_error));
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
        ApiRequest.validationAdresse(getContext(), adresse, codePostal, ville, new ApiRequest.ApiResponseCallback<JSONObject>() {
            @Override
            public void onSuccess(JSONObject response) {
                try {
                    if (!((JSONObject) response.getJSONArray("features").get(0)).getJSONObject("properties").getString("name").equals(adresse)) {
                        FragmentParametres.this.adresse.setError("Adresse non valide");
                        FragmentParametres.this.codePostal.setError("Adresse non valide");
                        FragmentParametres.this.ville.setError("Adresse non valide");
                        retour[0] = false;
                    }

                } catch (Exception e) {
                    FragmentParametres.this.adresse.setError("Adresse non valide");
                    FragmentParametres.this.codePostal.setError("Adresse non valide");
                    FragmentParametres.this.ville.setError("Adresse non valide");
                    retour[0] = false;
                }
            }

            @Override
            public void onError(VolleyError error) {

            }
        });
        return retour[0];
    }
}