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
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.AuthFailureError;
import com.android.volley.VolleyError;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import fr.iutrodez.tourneecommercial.ActiviteInscription;
import fr.iutrodez.tourneecommercial.ActivitePrincipale;
import fr.iutrodez.tourneecommercial.R;
import fr.iutrodez.tourneecommercial.utils.AdaptateurAdresse;
import fr.iutrodez.tourneecommercial.modeles.Client;

import fr.iutrodez.tourneecommercial.utils.ApiRequest;

public class FragmentCreationClient extends Fragment {
    private ActivitePrincipale parent;
    private Switch aSwitch;

    private AdaptateurAdresse adapter;

    private List<JSONObject> suggestionsAutoComplete ;
    private AutoCompleteTextView adresse;
    private Handler handler = new Handler();
    private Runnable fetchSuggestionsRunnable;
    private EditText nomEntreprise, codePostal, ville, nom, prenom, numTel;

    private String idModif;
    private Runnable onEnregistrer;

    public static FragmentCreationClient newInstance() {
        return new FragmentCreationClient();
    }

    private Context context;
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context=context;
        if (context instanceof ActivitePrincipale) {
            parent = (ActivitePrincipale) context;
        } else {
            throw new ClassCastException("Le contexte doit être une instance d'ActivitePrincipale.");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activite_creation_client, container, false);

        suggestionsAutoComplete = new ArrayList<>();

        // Initialisation des vues
        aSwitch = view.findViewById(R.id.statut);
        nomEntreprise = view.findViewById(R.id.nomEntreprise);
        adresse = view.findViewById(R.id.adresse);
        codePostal = view.findViewById(R.id.code_postal);
        ville = view.findViewById(R.id.ville);
        nom = view.findViewById(R.id.nom);
        prenom = view.findViewById(R.id.prenom);
        numTel = view.findViewById(R.id.num_tel);

        /*adapter = new AdaptateurAdresse(context,
                android.R.layout.simple_dropdown_item_1line,
                suggestionsAutoComplete,
                FragmentCreationClient.this::onClickSuggestions);

        adresse.setAdapter(adapter);*/
        // Configuration des listeners
        aSwitch.setOnClickListener(this::changeStatut);
        view.findViewById(R.id.enregistrer).setOnClickListener(this::enregistrer);

        adresse.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() > 2) {
                    fetchAdressSuggestions(charSequence.toString());
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        Bundle args =getArguments();
        // TODO : modifications
        if(args != null && args.containsKey("id") ) {
            //TODO : Bundle
            idModif = args.getString("id");
            try {
                recupererClient(idModif);
            } catch (JSONException exception) {

            }
            onEnregistrer = FragmentCreationClient.this::modifier;
        } else {
            onEnregistrer = FragmentCreationClient.this::creer;
        }
        return view;
    }


    /**
     * Récupére les suggestions d'adresses à l'aide de l'API du gouvernement
     * @param text le texte d'exemple pour les suggestions
     */
    private void fetchAdressSuggestions(String text) {
        if (fetchSuggestionsRunnable != null) {
            handler.removeCallbacks(fetchSuggestionsRunnable);
        }
        fetchSuggestionsRunnable = () -> ApiRequest.fetchAddressSuggestions(context, text,
                new ApiRequest.ApiResponseCallback<JSONObject>() {
                    @Override
                    public void onSuccess(JSONObject response) {
                        OnSuccessFetchSuggestions(response);
                    }

                    @Override
                    public void onError(VolleyError error) {
                        error.printStackTrace();
                    }
                });
        handler.postDelayed(fetchSuggestionsRunnable, 300);
    }

    public void creer() {
        try {
            JSONObject postData = createClientJson();
            System.out.println(postData.toString());
            String url = "client/creer";
            ApiRequest.creationClient(requireContext(), url, postData, new ApiRequest.ApiResponseCallback<JSONObject>() {
                @Override
                public void onSuccess(JSONObject response) {
                    Toast.makeText(requireContext(), "Client créé avec succès", Toast.LENGTH_SHORT).show();
                    // Retourner au fragment de liste des clients

                    //parent.navigateToNavbarItem(ActivitePrincipale.FRAGMENT_CLIENTS,true);
                }

                @Override
                public void onError(VolleyError error) {
                    Toast.makeText(requireContext(), "Erreur: " + error.toString(), Toast.LENGTH_LONG).show();
                }
            });

        } catch (JSONException e) {
            Toast.makeText(requireContext(), "Erreur: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    public void modifier() {
        try {
            JSONObject postData = createClientJson();
            System.out.println(postData.toString());
            ApiRequest.modifierClient(requireContext(), idModif,postData, new ApiRequest.ApiResponseCallback<JSONObject>() {
                @Override
                public void onSuccess(JSONObject response) {
                    Toast.makeText(requireContext(), "Client modifiée avec succès", Toast.LENGTH_SHORT).show();
                    // Retourner au fragment de liste des clients

                    //parent.navigateToNavbarItem(ActivitePrincipale.FRAGMENT_CLIENTS,true);
                }

                @Override
                public void onError(VolleyError error) {
                    Toast.makeText(requireContext(), "Erreur: " + error.toString(), Toast.LENGTH_LONG).show();
                }
            });

        } catch (JSONException e) {
            Toast.makeText(requireContext(), "Erreur: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
    /**
     * A utilisé lors du succés d'une requête de récupération de suggestions d'adresse.
     * Met dans un autoCompleteView un adapter avec les suggestions dans
     * @param response
     */
    private void OnSuccessFetchSuggestions(JSONObject response) {
        try {
            List<JSONObject> suggestions = new ArrayList<>();
            JSONArray features = response.getJSONArray("features");
            for (int i = 0; i < features.length(); i++) {
                JSONObject properties = features.getJSONObject(i).getJSONObject("properties");
                suggestions.add(properties);
            }

            /*handler.post(() -> {
                adapter.clear();
                adapter.addAll(suggestions);
                adapter.notifyDataSetChanged();
            });*/
            // TODO not clean
            AdaptateurAdresse adapterAdre = new AdaptateurAdresse(context,
                    android.R.layout.simple_dropdown_item_1line,
                    suggestions,
                    FragmentCreationClient.this::onClickSuggestions);
            adresse.setAdapter(adapterAdre);
            adapterAdre.notifyDataSetChanged();

        } catch (Exception e) {
            e.printStackTrace();
        }
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

    private void recupererClient(String id) throws JSONException {
        ApiRequest.recupererClient(requireContext(), id, new ApiRequest.ApiResponseCallback<JSONObject>() {
            @Override
            public void onSuccess(JSONObject response) {
                Gson gson = new Gson();
                Client client = gson.fromJson(response.toString(),Client.class);
                adresse.setText(client.getAdresse().getLibelle());
                codePostal.setText(client.getAdresse().getCodePostal());
                ville.setText(client.getAdresse().getVille());
                nom.setText(client.getContact().getNom());
                prenom.setText(client.getContact().getPrenom());
                numTel.setText(client.getContact().getTel());
                nomEntreprise.setText(client.getNomEntreprise());

            }


            @Override
            public void onError(VolleyError error) {
                Toast.makeText(requireContext(), "Erreur: " + error.toString(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void changeStatut(View view) {
        if(aSwitch.isChecked()){
            aSwitch.setText("Client");
        } else {
            aSwitch.setText("Prospect");
        }
    }

    private JSONObject createClientJson() throws JSONException {
        JSONObject adresseData = new JSONObject();
        adresseData.put("libelle" , adresse.getText().toString());
        adresseData.put("codePostal", codePostal.getText().toString());
        adresseData.put("ville", ville.getText().toString());

        JSONObject contact = new JSONObject();
        contact.put("nom", nom.getText().toString());
        contact.put("prenom", prenom.getText().toString());
        contact.put("telephone", numTel.getText().toString());

        JSONObject clientData = new JSONObject();
        clientData.put("nomEntreprise", nomEntreprise.getText().toString());
        clientData.put("adresse", adresseData);
        clientData.put("contact", contact);

        return clientData;
    }

    private void enregistrer(View view) {
        onEnregistrer.run();
        /*try {
            JSONObject postData = createClientJson();
            System.out.println(postData.toString());
            String url = "client/creer";
            ApiRequest.creationClient(requireContext(), url, postData, new ApiRequest.ApiResponseCallback<JSONObject>() {
                @Override
                public void onSuccess(JSONObject response) {
                    Toast.makeText(requireContext(), "Client créé avec succès", Toast.LENGTH_SHORT).show();
                    // Retourner au fragment de liste des clients

                    parent.navigateToNavbarItem(ActivitePrincipale.FRAGMENT_CLIENTS,true);
                }

                @Override
                public void onError(VolleyError error) {
                    Toast.makeText(requireContext(), "Erreur: " + error.toString(), Toast.LENGTH_LONG).show();
                }
            });

        } catch (JSONException e) {
            Toast.makeText(requireContext(), "Erreur: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }*/
    }
}