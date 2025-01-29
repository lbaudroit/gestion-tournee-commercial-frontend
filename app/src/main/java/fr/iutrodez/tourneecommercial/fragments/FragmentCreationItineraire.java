package fr.iutrodez.tourneecommercial.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.fragment.app.Fragment;

import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

import fr.iutrodez.tourneecommercial.ActivitePrincipale;
import fr.iutrodez.tourneecommercial.R;
import fr.iutrodez.tourneecommercial.modeles.Client;
import fr.iutrodez.tourneecommercial.modeles.dto.ItineraireDTO;
import fr.iutrodez.tourneecommercial.utils.AdaptateurListeClient;
import fr.iutrodez.tourneecommercial.utils.ApiRequest;

public class FragmentCreationItineraire extends Fragment {

    private Optional<Long> idItineraireModifie;

    private List<Client> tousClients;
    private EditText nom;
    private AutoCompleteTextView clientAutocomplete;
    private Button btnAjouterClient;
    private Button btnGenererItineraire;
    private Button btnValider;

    private Integer distance;

    private final static int MAX_CLIENTS = 8;

    public ActivitePrincipale parent;
    private Client clientSelectionne;
    private List<Client> clientsAjoutes;
    private AdaptateurListeClient adaptateurClientsAjoutes;
    private AdaptateurListeClient adaptateurClientsPotentiels;

    public static FragmentCreationItineraire newInstance() {
        return new FragmentCreationItineraire();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        parent = (ActivitePrincipale) context;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        nom = view.findViewById(R.id.nom_itineraire);
        clientAutocomplete = view.findViewById(R.id.client_autocomplete);

        btnAjouterClient = view.findViewById(R.id.ajouter);
        btnGenererItineraire = view.findViewById(R.id.generer);
        btnValider = view.findViewById(R.id.valider);

        // Ajouter les écouteurs sur les boutons
        btnAjouterClient.setOnClickListener(this::ajouter);
        btnGenererItineraire.setOnClickListener(this::generer);
        btnValider.setOnClickListener(this::valider);

        // Remplir la liste des clients disponibles
        tousClients = new ArrayList<>();
        ApiRequest.recupererClients(getContext(), new ApiRequest.ApiResponseCallback<JSONArray>() {
            @Override
            public void onSuccess(JSONArray response) {
                for (int i = 0; i < response.length(); i++) {
                    JSONObject obj = response.optJSONObject(i);
                    tousClients.add(ApiRequest.jsonToClient(obj));
                }
                Toast.makeText(getContext(),
                        "Réussite",
                        Toast.LENGTH_SHORT).show();

                // Gestion de l'autocomplete
                setupAutocomplete();
            }

            @Override
            public void onError(VolleyError error) {
                Toast.makeText(getContext(),
                        "Erreur : impossible de récupérer les clients disponibles",
                        Toast.LENGTH_SHORT).show();
            }
        });

        // Gestion de la liste des clients déjà ajoutés
        ListView listeClientsAjoutes = view.findViewById(R.id.list_clients);

        // Création, association de l'adaptateur
        clientsAjoutes = new ArrayList<>();
        adaptateurClientsAjoutes = new AdaptateurListeClient(
                parent,
                R.layout.listitem_client,
                clientsAjoutes,
                null,
                this::supprimerClientListe);
        listeClientsAjoutes.setAdapter(adaptateurClientsAjoutes);

        // Remplissage avec les données en cas de modification
        if (savedInstanceState != null && savedInstanceState.containsKey("id_itineraire")) {
            long idItineraire = savedInstanceState.getLong("id_itineraire");
            preparerPourModification(idItineraire);
        } else {
            idItineraireModifie = Optional.empty();
        }

        // Modification du titre dans l'action bar
        ActionBar actionBar = parent.getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(getString(R.string.creation_itineraire));
        }

        // Blocage des boutons
        disableView(btnGenererItineraire);
        disableView(btnValider);
        disableView(btnAjouterClient);
    }

    /**
     * Prépare les champs pour une modification d'itinéraire
     * - rajoute les clients existants
     * - affiche le nom de l'itinéraire
     * - désactive le bouton de génération
     * - met à jour le texte du bouton de validation
     * @param idItineraire l'identifiant de l'itinéraire à modifier
     */
    private void preparerPourModification(Long idItineraire) {
        ApiRequest.recupererItineraire(getContext(), idItineraire, new ApiRequest.ApiResponseCallback<JSONObject>() {
            @Override
            public void onSuccess(JSONObject response) {
                System.out.println("Itinéraire récupéré");
                // Réordonner les clients
                ItineraireDTO itineraireExistant = ApiRequest.itineraireDTOToClient(response);
                List<Client> clientsExistants = itineraireExistant.getClients();
                clientsAjoutes.clear();
                clientsAjoutes.addAll(clientsExistants);
                adaptateurClientsAjoutes.notifyDataSetChanged();

                // Remplir les informations
                nom.setText(itineraireExistant.getNom());
                distance = itineraireExistant.getDistance();
                idItineraireModifie = Optional.of(itineraireExistant.getId());

                // On désactive la génération et on propose l'option de validation
                disableView(btnGenererItineraire);
                enableView(btnValider);

                btnAjouterClient.setText(R.string.modifier);
            }

            @Override
            public void onError(VolleyError error) {
                error.printStackTrace();
                Toast.makeText(getContext(),
                        "Erreur : impossible de récupérer l'itinéraire",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.fragment_creation_itineraire, container, false);
    }

    private void setupAutocomplete() {
        List<Client> potentiels = getClientsDisponibles();

        // On crée l'adapteur s'il n'existe pas déjà et on associe les données
        adaptateurClientsPotentiels = new AdaptateurListeClient(parent,
                R.layout.listitem_client,
                potentiels,
                null,
                null
        );

        clientAutocomplete.setAdapter(adaptateurClientsPotentiels);

        clientAutocomplete.setOnItemClickListener(
                (parent, view, position, id) -> {
                    Client selectedClient = (Client) parent.getItemAtPosition(position);
                    clientSelectionne = selectedClient;
                    clientAutocomplete.setText(selectedClient.getNomEntreprise());
                    clientAutocomplete.dismissDropDown();

                    enableView(btnAjouterClient);
                });

        // Set the threshold for the AutoCompleteTextView
        clientAutocomplete.setThreshold(1);

        // Ensure the AutoCompleteTextView retains focus
        clientAutocomplete.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                clientAutocomplete.showDropDown();
            }
        });

        clientAutocomplete.setOnClickListener(v -> {
            clientAutocomplete.showDropDown();
        });
    }

    private void supprimerClientListe(Client client) {
        clientsAjoutes.remove(client);
        adaptateurClientsAjoutes.notifyDataSetChanged();

        // On réactive les boutons d'ajout s'il y a moins de 8 clients
        if (clientsAjoutes.size() < MAX_CLIENTS) {
            enableView(clientAutocomplete);
        }

        enableView(btnGenererItineraire);
        disableView(btnValider);
    }

    private List<Client> getClientsDisponibles() {
        ArrayList<Client> disponibles = new ArrayList<>(tousClients);
        disponibles.removeAll(clientsAjoutes);
        return disponibles;
    }

    private void ajouter(View view) {
        Toast.makeText(getContext(),
                "Ajout d'un client",
                Toast.LENGTH_SHORT).show();

        adaptateurClientsAjoutes.add(clientSelectionne);
        adaptateurClientsPotentiels.remove(clientSelectionne);
        clientSelectionne = null;

        // On ré-active le bouton de génération
        enableView(btnGenererItineraire);

        // On désactive les boutons d'ajout s'il y a 8 clients
        if (clientsAjoutes.size() == MAX_CLIENTS) {
            disableView(clientAutocomplete);
        }

        // On vide le champ et on rebloque le bouton ajouter
        clientAutocomplete.setText("");
        disableView(btnAjouterClient);

        // On désactive le bouton de validation tant qu'il n'y a pas de génération
        disableView(btnValider);
    }

    private void disableView(View view) {
        view.setEnabled(false);
        view.setAlpha(0.5f);
    }

    private void enableView(View view) {
        view.setEnabled(true);
        view.setAlpha(1f);
    }

    private void generer(View view) {
        ApiRequest.genererItineraire(getContext(), clientsAjoutes,
                new ApiRequest.ApiResponseCallback<JSONObject>() {
                    @Override
                    public void onSuccess(JSONObject response) {
                        try {
                            distance = response.getInt("kilometres");

                            // Réordonner les clients
                            JSONArray nouveauxClients = (JSONArray) response.get("clients");

                            clientsAjoutes.clear();
                            for (int i = 0; i < nouveauxClients.length(); i++) {
                                JSONObject obj = nouveauxClients.optJSONObject(i);
                                clientsAjoutes.add(ApiRequest.jsonToClient(obj));
                            }
                            adaptateurClientsAjoutes.notifyDataSetChanged();

                            // On désactive la génération et on propose l'option de validation
                            disableView(btnGenererItineraire);
                            enableView(btnValider);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(VolleyError error) {
                        Toast.makeText(getContext(),
                                "Erreur : impossible de générer l'itinéraire",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    /**
     * Méthode appelée lors du clic sur le bouton de validation.
     * Vérifie la validité des champs et envoie la requête de création
     * ou modification de l'itinéraire.
     * Retourne ensuite à la page des itinéraires ou affiche un message d'erreur
     * en cas de problème à interagir avec l'API
     *
     * @param view le bouton cliqué
     */
    private void valider(View view) {
        // Vérification du nom
        String nomItineraire = nom.getText().toString();
        if (nomItineraire.trim().isEmpty()) {
            nom.setError(getString(R.string.empty_field_error));
        }

        // Vérification des clients
        if (clientsAjoutes.isEmpty()) {
            clientAutocomplete.setError(getString(R.string.aucun_client_saisi_error));
        }

        // Envoie de la requête au back-end
        Consumer<Exception> onExceptionCallback = (e) -> {
            Toast.makeText(getContext(),
                    "Erreur : impossible de créer l'itinéraire",
                    Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        };

        if (idItineraireModifie.isPresent()) {
            requeteModifierItineraire(onExceptionCallback);
        } else {
            requeteCreerItineraire(onExceptionCallback);
        }
    }

    private void requeteCreerItineraire(Consumer<Exception> onExceptionCallback) {

        String nomItineraire = nom.getText().toString();
        try {
            ApiRequest.creationItineraire(getContext(), nomItineraire, clientsAjoutes, distance,
                    new ApiRequest.ApiResponseCallback<JSONObject>() {
                        @Override
                        public void onSuccess(JSONObject response) {
                            Toast.makeText(getContext(),
                                    "Itinéraire créé avec succès",
                                    Toast.LENGTH_SHORT).show();
                            parent.navigateToFragment(ActivitePrincipale.FRAGMENT_ITINERAIRES, false);
                        }

                        @Override
                        public void onError(VolleyError error) {
                            onExceptionCallback.accept(error);
                        }
                    });
        } catch (JSONException e) {
            onExceptionCallback.accept(e);
        }
    }
    private void requeteModifierItineraire(Consumer<Exception> onExceptionCallback) {
        if (idItineraireModifie.isPresent()) {
            String nomItineraire = nom.getText().toString();
            try {
                ApiRequest.modificationItineraire(parent, idItineraireModifie.get(), nomItineraire, clientsAjoutes, distance,
                        new ApiRequest.ApiResponseCallback<JSONObject>() {
                            @Override
                            public void onSuccess(JSONObject response) {
                                Toast.makeText(getContext(),
                                        "Itinéraire modifié avec succès",
                                        Toast.LENGTH_SHORT).show();
                                parent.navigateToFragment(ActivitePrincipale.FRAGMENT_ITINERAIRES, false);
                            }

                            @Override
                            public void onError(VolleyError error) {
                                onExceptionCallback.accept(error);
                            }
                        });
            } catch (JSONException e) {
                onExceptionCallback.accept(e);
            }
        }
    }
}
