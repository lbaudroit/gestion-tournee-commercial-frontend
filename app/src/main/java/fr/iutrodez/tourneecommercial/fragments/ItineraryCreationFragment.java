package fr.iutrodez.tourneecommercial.fragments;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import fr.iutrodez.tourneecommercial.MainActivity;
import fr.iutrodez.tourneecommercial.R;
import fr.iutrodez.tourneecommercial.modeles.Client;
import fr.iutrodez.tourneecommercial.utils.ClientListAdapter;
import fr.iutrodez.tourneecommercial.utils.api.ApiRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

public class ItineraryCreationFragment extends Fragment {

    private static final ApiRequest API_REQUEST = ApiRequest.getInstance();

    //Elements de la vue
    private EditText name;
    private TextView clientSelector;
    private Button addClientButton;
    private Button generateItineraryButton;
    private Button validateItineraryButton;
    public MainActivity parent;

    //Listes
    private List<Client> itineraryClients;
    private List<Client> allClients;

    //Elements secondaires de la vue
    private ClientListAdapter itineraryClientsAdapter;
    private ClientListAdapter freeClientsAdapter;
    private Dialog dialog;

    //Autres variables
    private final static int MAX_CLIENTS = 8;
    private Integer distance;
    private Client selectedClient;
    private Long modifiedItineraryId = null;

    public static ItineraryCreationFragment newInstance() {
        return new ItineraryCreationFragment();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        parent = (MainActivity) context;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Récupération des éléments de la vue
        name = view.findViewById(R.id.editText_name);
        clientSelector = view.findViewById(R.id.textView_clientSelector);
        addClientButton = view.findViewById(R.id.button_add);
        generateItineraryButton = view.findViewById(R.id.button_gereateItinerary);
        validateItineraryButton = view.findViewById(R.id.button_generateItinerary);

        // Ajouter les écouteurs sur les boutons
        addClientButton.setOnClickListener(this::add);
        generateItineraryButton.setOnClickListener(this::generate);
        validateItineraryButton.setOnClickListener(this::valdiate);

        // Remplir la liste des clients disponibles
        allClients = new ArrayList<>();
        getAllClients();

        // Création, association de l'adaptateur et association de la liste à l'adaptateur
        ListView addedClientsList = view.findViewById(R.id.listView_client);
        itineraryClients = new ArrayList<>();
        itineraryClientsAdapter = new ClientListAdapter(
                parent,
                R.layout.list_of_client_items,
                itineraryClients,
                null,
                this::deleteClientFromList);
        addedClientsList.setAdapter(itineraryClientsAdapter);

        // Blocage des boutons
        disableView(generateItineraryButton);
        disableView(validateItineraryButton);
        disableView(addClientButton);

        // Gestion du champ de recherche des clients
        createClientResearch();

        //Récupération du bundle si on est en modification
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            Long itineraryId = bundle.getLong("idItineraire");
            System.out.println("Modification de l'itinéraire " + itineraryId);
            prepareForModification(itineraryId);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.itinerary_creation_fragment, container, false);
    }

    private void getAllClients() {
        API_REQUEST.client.getAll(getContext(), response -> {
            allClients = response;
        }, error -> {
            Toast.makeText(getContext(),
                    R.string.fetch_clients_error,
                    Toast.LENGTH_SHORT).show();
        });
    }

    private void createClientResearch() {
        clientSelector.setOnClickListener(v -> {
            // Préparer le dialog
            dialog = new Dialog(parent);
            dialog.setContentView(R.layout.dialog_search_address);
            dialog.getWindow().setLayout(650, 800);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.show();

            // Récupérer les éléments du dialog
            EditText research = dialog.findViewById(R.id.editText_research);
            ListView list = dialog.findViewById(R.id.listView_list);
            TextView title = dialog.findViewById(R.id.textView_title);
            title.setText(R.string.clients_search_title);

            // Initialiser l'adapter
            freeClientsAdapter = new ClientListAdapter(
                    parent,
                    R.layout.list_of_client_items,
                    getFreeClients(),
                    null,
                    null);
            list.setAdapter(freeClientsAdapter);

            // Ajout de l'écouteur sur le champ de recherche
            research.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    freeClientsAdapter.getFilter().filter(s);
                }

                @Override
                public void afterTextChanged(Editable s) {
                }
            });

            list.setOnItemClickListener((parent, view, position, id) -> {
                clientSelector.setText(Objects.requireNonNull(freeClientsAdapter.getItem(position)).getNomEntreprise());
                selectedClient = freeClientsAdapter.getItem(position);
                enableView(addClientButton);
                dialog.dismiss();
            });
        });
    }

    private void deleteClientFromList(Client client) {
        itineraryClients.remove(client);
        itineraryClientsAdapter.notifyDataSetChanged();

        // On réactive les boutons d'ajout s'il y a moins de 8 clients
        if (itineraryClients.size() < MAX_CLIENTS) {
            enableView(clientSelector);
        }
        if (itineraryClients.isEmpty()) {
            disableView(generateItineraryButton);
        } else {
            enableView(generateItineraryButton);
        }
        disableView(validateItineraryButton);
    }

    private List<Client> getFreeClients() {
        List<Client> free = new ArrayList<>(allClients);
        free.removeAll(itineraryClients);
        return free;
    }

    private void add(View view) {
        Toast.makeText(getContext(),
                R.string.add_client,
                Toast.LENGTH_SHORT).show();

        itineraryClientsAdapter.add(selectedClient);
        selectedClient = null;

        // On ré-active le bouton de génération
        enableView(generateItineraryButton);

        // On désactive les boutons d'ajout s'il y a 8 clients
        if (itineraryClients.size() == MAX_CLIENTS) {
            disableView(clientSelector);
        }

        // On vide le champ et on rebloque le bouton ajouter
        clientSelector.setText("");
        disableView(addClientButton);

        // On désactive le bouton de validation tant qu'il n'y a pas de génération
        disableView(validateItineraryButton);
    }

    private void disableView(View view) {
        view.setEnabled(false);
        view.setAlpha(0.5f);
    }

    private void enableView(View view) {
        view.setEnabled(true);
        view.setAlpha(1f);
    }

    private void generate(View view) {
        API_REQUEST.itineraire.generate(getContext(), itineraryClients, response -> {
            distance = response.getDistance();
            itineraryClients.clear();
            itineraryClients.addAll(response.getClients());
            itineraryClientsAdapter.notifyDataSetChanged();
            disableView(generateItineraryButton);
            enableView(validateItineraryButton);
        }, error -> {
            Toast.makeText(getContext(),
                    R.string.generate_itinerary_error,
                    Toast.LENGTH_SHORT).show();
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
    private void valdiate(View view) {
        // Vérification du nom
        String nomItineraire = name.getText().toString();
        if (nomItineraire.trim().isEmpty()) {
            name.setError(getString(R.string.empty_field_error));

            return;
        }

        // Vérification des clients
        if (itineraryClients.isEmpty()) {
            clientSelector.setError(getString(R.string.no_clients_error));
            return;
        }

        // Envoie de la requête au back-end
        Consumer<Exception> onExceptionCallback = (e) -> {
            Toast.makeText(getContext(),
                    R.string.create_itinerary_error,
                    Toast.LENGTH_SHORT).show();
            Log.e("ItineraryCreationFragment", "Error during itinerary creation", e);
        };

        if (modifiedItineraryId != null) {
            modifyItinerary(onExceptionCallback);
        } else {
            createItinerary(onExceptionCallback);
        }
    }

    private void createItinerary(Consumer<Exception> onExceptionCallback) {

        String itineraryName = name.getText().toString();
        API_REQUEST.itineraire.create(getContext(), itineraryName, distance, itineraryClients, response -> {
            Toast.makeText(getContext(),
                    R.string.create_route_success,
                    Toast.LENGTH_SHORT).show();
            parent.navigateToFragment(MainActivity.ITINERARY_FRAGMENT, false);
        }, onExceptionCallback::accept);
    }

    private void modifyItinerary(Consumer<Exception> onExceptionCallback) {
        if (modifiedItineraryId != null) {
            String itineraryName = name.getText().toString();
            API_REQUEST.itineraire.update(getContext(), modifiedItineraryId, itineraryName, distance, itineraryClients, response -> {
                Toast.makeText(getContext(),
                        R.string.update_itinerary_success,
                        Toast.LENGTH_SHORT).show();
                parent.navigateToFragment(MainActivity.ITINERARY_FRAGMENT, false);
            }, onExceptionCallback::accept);
        }
    }


    /**
     * Prépare les champs pour une modification d'itinéraire
     * - rajoute les clients existants
     * - affiche le nom de l'itinéraire
     * - désactive le bouton de génération
     * - met à jour le texte du bouton de validation
     *
     * @param idItineraire l'identifiant de l'itinéraire à modifier
     */
    private void prepareForModification(Long idItineraire) {
        API_REQUEST.itineraire.getOne(getContext(), idItineraire, response -> {
            // Réordonner les clients
            List<Client> addedClients = response.getClients();
            itineraryClients.clear();
            itineraryClients.addAll(addedClients);
            itineraryClientsAdapter.notifyDataSetChanged();

            // Remplir les informations
            name.setText(response.getNom());
            distance = response.getDistance();
            modifiedItineraryId = response.getId();

            // On désactive la génération
            disableView(generateItineraryButton);

            // On vérifie si on a atteint le nombre maximum de clients
            if (itineraryClients.size() == MAX_CLIENTS) {
                disableView(clientSelector);
            }
            // On active le bouton de validation et on change son texte en "Modifier"
            validateItineraryButton.setText(R.string.edit);

            // On rajoute un TextWatcher pour vérifier si le nom change
            name.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    enableView(validateItineraryButton);
                }

                @Override
                public void afterTextChanged(Editable s) {
                }
            });
        }, error -> {
            Log.e("ItineraryCreationFragment", "Error during itinerary modification preparation", error);
            Toast.makeText(getContext(),
                    R.string.fetch_route_error,
                    Toast.LENGTH_SHORT).show();
        });
    }
}
