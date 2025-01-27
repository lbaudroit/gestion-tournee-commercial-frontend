package fr.iutrodez.tourneecommercial;

import android.os.Bundle;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

import fr.iutrodez.tourneecommercial.modeles.Client;
import fr.iutrodez.tourneecommercial.utils.AdaptateurListeClient;
import fr.iutrodez.tourneecommercial.utils.ApiRequest;

public class ActiviteCreationItineraire extends AppCompatActivity {

    private List<Client> tousClients;
    private EditText nom;
    private AutoCompleteTextView clientAutocomplete;
    private Button btnAjouterClient;
    private Button btnGenererItineraire;
    private Button btnValider;

    private final static int MAX_CLIENTS = 8;


    private Client clientSelectionne;
    private List<Client> clientsAjoutes;
    private AdaptateurListeClient adaptateurClientsAjoutes;
    private AdaptateurListeClient adaptateurClientsPotentiels;

    private List<Client> getClientsDisponibles() {
        ArrayList<Client> disponibles = new ArrayList<>(tousClients);
        disponibles.removeAll(clientsAjoutes);
        return disponibles;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.fragment_creation_itineraire);

        nom = findViewById(R.id.nom_itineraire);
        clientAutocomplete = findViewById(R.id.client_autocomplete);

        btnAjouterClient = findViewById(R.id.ajouter);
        btnGenererItineraire = findViewById(R.id.generer);
        btnValider = findViewById(R.id.valider);

        // Ajouter les écouteurs sur les boutons
        btnAjouterClient.setOnClickListener(this::ajouter);
        btnGenererItineraire.setOnClickListener(this::generer);
        btnValider.setOnClickListener(this::valider);

        // Remplir la liste des clients disponibles
        tousClients = new ArrayList<>();
        ApiRequest.recupererClients(this, new ApiRequest.ApiArrayResponseCallback() {
            @Override
            public void onSuccess(JSONArray response) {
                for (int i = 0; i < response.length(); i++) {
                    JSONObject obj = response.optJSONObject(i);
                    tousClients.add(ApiRequest.jsonToClient(obj));
                }
                Toast.makeText(ActiviteCreationItineraire.this,
                        "Réussite",
                        Toast.LENGTH_SHORT).show();

                // Gestion de l'autocomplete
                setupAutocomplete();
            }

            @Override
            public void onError(VolleyError error) {
                Toast.makeText(ActiviteCreationItineraire.this,
                        "Erreur : impossible de récupérer les clients disponibles",
                        Toast.LENGTH_SHORT).show();
            }
        });

        // Gestion de la liste des clients déjà ajoutés
        ListView listeClientsAjoutes = findViewById(R.id.list_clients);

        // Création, association de l'adaptateur
        clientsAjoutes = new ArrayList<>();
        adaptateurClientsAjoutes = new AdaptateurListeClient(
                this,
                R.layout.listitem_client,
                clientsAjoutes,
                null,
                this::supprimerClientListe);
        listeClientsAjoutes.setAdapter(adaptateurClientsAjoutes);

        // Remplissage avec les données
        // TODO remplir avec les données de l'itinéraire si on est en modification
        // Retirer les clients de la liste des clients disponibles

        // Modification du titre dans l'action bar
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(getString(R.string.creation_itineraire));
        }

        // Blocage des boutons
        disableView(btnGenererItineraire);
        disableView(btnValider);
        disableView(btnAjouterClient);
    }

    private void setupAutocomplete() {
        List<Client> potentiels = getClientsDisponibles();

        // On crée l'adapteur s'il n'existe pas déjà et on associe les données
        adaptateurClientsPotentiels = new AdaptateurListeClient(ActiviteCreationItineraire.this,
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

        // Ensure the adapter is updated on the UI thread
        runOnUiThread(() -> {
            // Your code to update the adapter
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

    private void ajouter(View view) {
        Toast.makeText(ActiviteCreationItineraire.this,
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
        ApiRequest.genererItineraire(this, clientsAjoutes, new ApiRequest.ApiArrayResponseCallback() {
            @Override
            public void onSuccess(JSONArray response) {
                // Réordonner les clients
                clientsAjoutes.clear();
                for (int i = 0; i < response.length(); i++) {
                    JSONObject obj = response.optJSONObject(i);
                    clientsAjoutes.add(ApiRequest.jsonToClient(obj));
                }
                adaptateurClientsAjoutes.notifyDataSetChanged();

                // On désactive la génération et on propose l'option de validation
                disableView(btnGenererItineraire);
                enableView(btnValider);
            }

            @Override
            public void onError(VolleyError error) {
                Toast.makeText(ActiviteCreationItineraire.this,
                        "Erreur : impossible de générer l'itinéraire",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void valider(View view) {
        String nomItineraire = nom.getText().toString();
        if (nomItineraire.trim().isEmpty()) {
            nom.setError(getString(R.string.empty_field_error));
        }

        if (clientsAjoutes.isEmpty()) {
            clientAutocomplete.setError(getString(R.string.aucun_client_saisi_error));
        }

        Consumer<Exception> onExceptionCallback = (e) -> {
            Toast.makeText(ActiviteCreationItineraire.this,
                    "Erreur : impossible de créer l'itinéraire",
                    Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        };

        try {
            ApiRequest.creationItineraire(this, nomItineraire, clientsAjoutes, new ApiRequest.ApiArrayResponseCallback() {
                @Override
                public void onSuccess(JSONArray response) {
                    Toast.makeText(ActiviteCreationItineraire.this,
                            "Itinéraire créé avec succès",
                            Toast.LENGTH_SHORT).show();
                    finish();
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
