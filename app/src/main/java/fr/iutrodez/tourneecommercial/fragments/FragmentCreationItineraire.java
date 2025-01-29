package fr.iutrodez.tourneecommercial.fragments;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.util.Consumer;
import androidx.fragment.app.Fragment;

import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import fr.iutrodez.tourneecommercial.ActivitePrincipale;
import fr.iutrodez.tourneecommercial.R;
import fr.iutrodez.tourneecommercial.modeles.Client;
import fr.iutrodez.tourneecommercial.utils.AdaptateurListeClient;
import fr.iutrodez.tourneecommercial.utils.ApiRequest;

public class FragmentCreationItineraire extends Fragment {

    //Elements de la vue
    private EditText nom;
    private TextView selectionClient;
    private Button btnAjouterClient;
    private Button btnGenererItineraire;
    private Button btnValiderItineraire;

    //Listes
    private List<Client> clientsItineraire;
    private List<Client> tousClients;

    //Elements secondaires de la vue
    private AdaptateurListeClient adaptateurClientsItineraire;
    private AdaptateurListeClient adaptateurClientsDisponibles;

    //Autres variables
    private final static int MAX_CLIENTS = 8;
    private Integer distance;
    private Dialog dialog;
    private Client clientSelectionne;

    //Méthodes principales
    public static FragmentCreationItineraire newInstance() {
        return new FragmentCreationItineraire();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Récupération des éléments de la vue
        nom = view.findViewById(R.id.nom_itineraire);
        selectionClient = view.findViewById(R.id.client_selectionne);
        btnAjouterClient = view.findViewById(R.id.ajouter);
        btnGenererItineraire = view.findViewById(R.id.generer);
        btnValiderItineraire = view.findViewById(R.id.valider);

        // Ajouter les écouteurs sur les boutons
        btnAjouterClient.setOnClickListener(this::ajouter);
        btnGenererItineraire.setOnClickListener(this::generer);
        btnValiderItineraire.setOnClickListener(this::valider);

        // Remplir la liste des clients disponibles
        tousClients = new ArrayList<>();
        recuperationDesClients();

        // Gestion de la liste des clients séléctionnés
        ListView listeClientsAjoutes = view.findViewById(R.id.list_clients);

        // Création, association de l'adaptateur et association de la liste à l'adaptateur
        clientsItineraire = new ArrayList<>();
        adaptateurClientsItineraire = new AdaptateurListeClient(
                getContext(),
                R.layout.listitem_client,
                clientsItineraire,
                null,
                this::supprimerClientListe);
        listeClientsAjoutes.setAdapter(adaptateurClientsItineraire);

        // Modification du titre dans l'action bar
        ActionBar actionBar = ((AppCompatActivity) this.getActivity()).getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(getString(R.string.creation_itineraire));
        }

        // Blocage des boutons
        disableView(btnGenererItineraire);
        disableView(btnValiderItineraire);
        disableView(btnAjouterClient);


        // Gestion du champ de recherche des clients
        creationRechercheClient();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.fragment_creation_itineraire, container, false);
    }

    //Fonction outils
    private void recuperationDesClients() {
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
                //setupAutocomplete();
            }

            @Override
            public void onError(VolleyError error) {
                Toast.makeText(getContext(),
                        "Erreur : impossible de récupérer les clients disponibles",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void creationRechercheClient() {
        selectionClient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Préparer le dialog
                dialog = new Dialog(requireContext());
                dialog.setContentView(R.layout.dialog_searchable_spinner);
                dialog.getWindow().setLayout(650, 800);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();

                // Récupérer les éléments du dialog
                EditText editText = dialog.findViewById(R.id.edit_text);
                ListView listView = dialog.findViewById(R.id.list_view);

                // Initialiser l'adapter
                adaptateurClientsDisponibles = new AdaptateurListeClient(
                        requireContext(),
                        R.layout.listitem_client,
                        getClientsDisponibles(),
                        null,
                        null);
                listView.setAdapter(adaptateurClientsDisponibles);

                // Ajout de l'écouteur sur le champ de recherche
                editText.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        adaptateurClientsDisponibles.getFilter().filter(s);
                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                    }
                });

                listView.setOnItemClickListener((parent, view, position, id) -> {
                    selectionClient.setText(Objects.requireNonNull(adaptateurClientsDisponibles.getItem(position)).getNomEntreprise());
                    clientSelectionne = adaptateurClientsDisponibles.getItem(position);
                    enableView(btnAjouterClient);
                    dialog.dismiss();
                });
            }
        });
    }

    private void supprimerClientListe(Client client) {
        clientsItineraire.remove(client);
        adaptateurClientsItineraire.notifyDataSetChanged();

        // On réactive les boutons d'ajout s'il y a moins de 8 clients
        if (clientsItineraire.size() < MAX_CLIENTS) {
            enableView(selectionClient);
        }
        enableView(btnGenererItineraire);
        disableView(btnValiderItineraire);
    }

    private List<Client> getClientsDisponibles() {
        List<Client> disponibles = new ArrayList<>(tousClients);
        disponibles.removeAll(clientsItineraire);
        return disponibles;
    }

    private void ajouter(View view) {
        Toast.makeText(getContext(),
                "Ajout d'un client",
                Toast.LENGTH_SHORT).show();

        adaptateurClientsItineraire.add(clientSelectionne);
        adaptateurClientsDisponibles.remove(clientSelectionne);
        clientSelectionne = null;

        // On ré-active le bouton de génération
        enableView(btnGenererItineraire);

        // On désactive les boutons d'ajout s'il y a 8 clients
        if (clientsItineraire.size() == MAX_CLIENTS) {
            disableView(selectionClient);
        }

        // On vide le champ et on rebloque le bouton ajouter
        selectionClient.setText("");
        disableView(btnAjouterClient);

        // On désactive le bouton de validation tant qu'il n'y a pas de génération
        disableView(btnValiderItineraire);
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
        ApiRequest.genererItineraire(getContext(), clientsItineraire,
                new ApiRequest.ApiResponseCallback<JSONObject>() {
                    @Override
                    public void onSuccess(JSONObject response) {
                        try {
                            distance = response.getInt("kilometres");

                            // Réordonner les clients
                            JSONArray nouveauxClients = (JSONArray) response.get("clients");

                            clientsItineraire.clear();
                            for (int i = 0; i < nouveauxClients.length(); i++) {
                                JSONObject obj = nouveauxClients.optJSONObject(i);
                                clientsItineraire.add(ApiRequest.jsonToClient(obj));
                            }
                            adaptateurClientsItineraire.notifyDataSetChanged();

                            // On désactive la génération et on propose l'option de validation
                            disableView(btnGenererItineraire);
                            enableView(btnValiderItineraire);
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

    private void valider(View view) {
        String nomItineraire = nom.getText().toString();
        if (nomItineraire.trim().isEmpty()) {
            nom.setError(getString(R.string.empty_field_error));
            return;
        }

        if (clientsItineraire.isEmpty()) {
            Toast.makeText(getContext(),
                    "Erreur : aucun client sélectionné",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        Consumer<Exception> onExceptionCallback = (e) -> {
            Toast.makeText(getContext(),
                    "Erreur : impossible de créer l'itinéraire",
                    Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        };

        try {
            ApiRequest.creationItineraire(getContext(), nomItineraire, clientsItineraire, distance,
                    new ApiRequest.ApiResponseCallback<JSONObject>() {
                        @Override
                        public void onSuccess(JSONObject response) {
                            Toast.makeText(getContext(),
                                    "Itinéraire créé avec succès",
                                    Toast.LENGTH_SHORT).show();
                            ((ActivitePrincipale) getActivity()).navigateToFragment(ActivitePrincipale.FRAGMENT_ITINERAIRES, false);
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
