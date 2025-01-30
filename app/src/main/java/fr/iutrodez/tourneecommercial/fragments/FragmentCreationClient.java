package fr.iutrodez.tourneecommercial.fragments;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.android.volley.VolleyError;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import fr.iutrodez.tourneecommercial.ActivitePrincipale;
import fr.iutrodez.tourneecommercial.R;
import fr.iutrodez.tourneecommercial.modeles.Adresse;
import fr.iutrodez.tourneecommercial.modeles.Client;
import fr.iutrodez.tourneecommercial.utils.AdaptateurAdresse;
import fr.iutrodez.tourneecommercial.utils.ApiRequest;

/**
 * Fragment pour afficher l'interface de création client ou de modification client
 *
 * @author Ahmed BRIBACH
 * Leila Baudroit
 * Enzo CLUZEL
 * Benjamin NICOL
 */
public class FragmentCreationClient extends Fragment {
    private List<Adresse> adressesProposes;
    private AdaptateurAdresse adaptateurAdresse;

    private Adresse selectedAdresse;
    private Dialog dialog;
    private TextView adresse_view;
    private ActivitePrincipale parent;
    private Switch aSwitch;
    private EditText nomEntreprise, nom, prenom, numTel, description;

    private String idModif;

    /**
     * Fonction à appeler lors de l'enregistrement
     */
    private Runnable onEnregistrer;

    public static FragmentCreationClient newInstance() {
        return new FragmentCreationClient();
    }

    private Context context;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
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

        // Initialisation des vues
        aSwitch = view.findViewById(R.id.statut);
        nomEntreprise = view.findViewById(R.id.nomEntreprise);
        adresse_view = view.findViewById(R.id.adresse_view);
        nom = view.findViewById(R.id.nom);
        prenom = view.findViewById(R.id.prenom);
        numTel = view.findViewById(R.id.num_tel);
        description = view.findViewById(R.id.description);

        // Configuration des listeners
        aSwitch.setOnClickListener(this::changeStatut);
        Button enregistrer = view.findViewById(R.id.enregistrer);


        // On récupère les arguments mis dans le fragment
        Bundle args = getArguments();

        // On vérifie si l'argument id existe
        if (args != null && args.containsKey("id")) {
            // Modification
            idModif = args.getString("id");
            try {
                // On récupère le client par rapport à l'id
                recupererClient(idModif);
            } catch (JSONException exception) {

            }
            enregistrer.setOnClickListener(this::modifier);
        } else {
            enregistrer.setOnClickListener(this::creer);
        }
        adressesProposes = new ArrayList<>();
        setupAdresse();
        return view;
    }

    private void setupAdresse() {
        adresse_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Préparer le dialog
                dialog = new Dialog(context);
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
                        context,
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
                        ApiRequest.fetchAddressSuggestions(context, s.toString(),
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


    /**
     * Fonction pour créer un client
     */
    private void creer(View view) {
        if (areCorrectFields()) {
            try {
                JSONObject postData = createClientJson();
                String url = "client/creer";
                ApiRequest.creationClient(requireContext(), url, postData, new ApiRequest.ApiResponseCallback<JSONObject>() {
                    @Override
                    public void onSuccess(JSONObject response) {
                        Toast.makeText(requireContext(), "Client créé avec succès", Toast.LENGTH_SHORT).show();
                        // Retourner au fragment de liste des clients

                        parent.navigateToNavbarItem(ActivitePrincipale.FRAGMENT_CLIENTS, true);
                    }

                    @Override
                    public void onError(VolleyError error) {
                        String erreur = new String(error.networkResponse.data, StandardCharsets.UTF_8);
                        JSONObject jsonError = null;
                        try {
                            jsonError = new JSONObject(erreur);

                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                        Toast.makeText(requireContext(), "Erreur: " + jsonError.optString("message"), Toast.LENGTH_LONG).show();
                    }
                });

            } catch (JSONException e) {
                Toast.makeText(requireContext(), "Erreur: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
    }

    /**
     * Fonction pour modifier un client à partir de l'id récupéré dans le bundle
     */
    private void modifier(View view) {
        if (areCorrectFields()) {
            System.out.println("Modification");
            try {
                JSONObject postData = createClientJson();
                ApiRequest.modifierClient(requireContext(), idModif, postData, new ApiRequest.ApiResponseCallback<JSONObject>() {
                    @Override
                    public void onSuccess(JSONObject response) {
                        Toast.makeText(requireContext(), "Client modifiée avec succès", Toast.LENGTH_SHORT).show();
                        // Retourner au fragment de liste des clients
                        parent.navigateToNavbarItem(ActivitePrincipale.FRAGMENT_CLIENTS, true);
                    }

                    @Override
                    public void onError(VolleyError error) {
                        String erreur = new String(error.networkResponse.data, StandardCharsets.UTF_8);
                        JSONObject jsonError = null;
                        try {
                            jsonError = new JSONObject(erreur);

                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                        //Toast.makeText(requireContext(), "Erreur: " + jsonError.optString("message"), Toast.LENGTH_LONG).show();
                    }
                });

            } catch (JSONException e) {
                Toast.makeText(requireContext(), "Erreur: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
    }

    /**
     * Méthode d'appel API pour récupérer "un" client
     *
     * @param id du client à récupérer
     * @throws JSONException
     */
    private void recupererClient(String id) throws JSONException {
        ApiRequest.recupererClient(requireContext(), id, new ApiRequest.ApiResponseCallback<JSONObject>() {
            @Override
            public void onSuccess(JSONObject response) {
                Gson gson = new Gson();
                System.out.println(response.toString());
                Client client = gson.fromJson(response.toString(), Client.class);
                aSwitch.setChecked(client.isClientEffectif());
                aSwitch.setText(client.isClientEffectif() ? "Client" : "Prospect");
                nom.setText(client.getContact().getNom());
                prenom.setText(client.getContact().getPrenom());
                selectedAdresse = client.getAdresse();
                adresse_view.setText(selectedAdresse.toString());
                numTel.setText(client.getContact().getNumeroTelephone());
                nomEntreprise.setText(client.getNomEntreprise());
                description.setText(client.getDescriptif());
            }

            @Override
            public void onError(VolleyError error) {
                Toast.makeText(requireContext(), "Erreur: " + error.toString(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void changeStatut(View view) {
        if (aSwitch.isChecked()) {
            aSwitch.setText("Client");
        } else {
            aSwitch.setText("Prospect");
        }
    }

    private JSONObject createClientJson() throws JSONException {
        JSONObject adresseData = new JSONObject();
        adresseData.put("libelle", selectedAdresse.getLibelle());
        adresseData.put("codePostal", selectedAdresse.getCodePostal());
        adresseData.put("ville", selectedAdresse.getVille());

        JSONObject contact = new JSONObject();
        contact.put("nom", nom.getText().toString());
        contact.put("prenom", prenom.getText().toString());
        contact.put("numeroTelephone", numTel.getText().toString());

        JSONObject clientData = new JSONObject();
        clientData.put("nomEntreprise", nomEntreprise.getText().toString());
        clientData.put("adresse", adresseData);
        clientData.put("contact", contact);
        clientData.put("clientEffectif", aSwitch.isChecked());
        clientData.put("descriptif", description.getText().toString().trim());

        return clientData;
    }

    private boolean areCorrectFields() {
        boolean correct = true;
        if (!isCorrectNomEntreprise()) {
            System.out.println("Nom entreprise incorrect");
            correct = false;
        }
        if (!isCorrectContact(nom.getText().toString(), prenom.getText().toString(), numTel.getText().toString())) {
            System.out.println("Contact incorrect");
            correct = false;
        }
        return correct;

    }

    private boolean isCorrectNomEntreprise() {
        boolean correct = true;
        if (nomEntreprise.getText().toString().trim().isEmpty()) {
            nomEntreprise.setError(getString(R.string.empty_field_error));
            correct = false;
        }
        return correct;
    }

    private boolean isCorrectContact(String nomC, String prenomC, String telephone) {
        boolean correct = true;
        if (isFilledContact(nomC, prenomC, telephone)) {
            if (!isFilled(nom.getText().toString())) {
                nom.setError(getString(R.string.empty_field_error));
                correct = false;
            }
            if (!isFilled(prenom.getText().toString())) {
                prenom.setError(getString(R.string.empty_field_error));
                correct = false;
            }
            if (!isFilled(numTel.getText().toString())) {
                numTel.setError(getString(R.string.empty_field_error));
                correct = false;
            }

            if (!isCorrectPhoneNumber(telephone)) {
                numTel.setError(getString(R.string.invalid_field_error,
                        "il ne correspond pas à un numéro de téléphone"));
                correct = false;
            }

        }
        return correct;
    }

    private boolean isCorrectPhoneNumber(String phoneNumber) {
        boolean correct = true;
        if (!phoneNumber.matches("[0-9]{10}")) {
            correct = false;
        }
        return correct;
    }

    private boolean isFilled(String text) {
        return !text.trim().isEmpty();
    }

    private boolean isFilledContact(String... values) {
        boolean filled = false;
        for (String value : values) {
            if (!value.trim().isEmpty()) {
                filled = true;
            }
        }
        return filled;
    }
}