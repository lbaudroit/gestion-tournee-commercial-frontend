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
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.android.volley.VolleyError;
import fr.iutrodez.tourneecommercial.MainActivity;
import fr.iutrodez.tourneecommercial.R;
import fr.iutrodez.tourneecommercial.model.Adresse;
import fr.iutrodez.tourneecommercial.utils.adapter.AddressAdapter;
import fr.iutrodez.tourneecommercial.utils.api.ApiRequest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Fragment pour afficher l'interface de création client ou de modification client
 *
 * @author Benjamin NICOL, Enzo CLUZEL, Ahmed BRIBACH, Leïla BAUDROIT
 */
public class ClientCreationFragment extends Fragment {

    private final ApiRequest API_REQUEST = ApiRequest.getInstance();
    private List<Adresse> suggestedAddress;
    private AddressAdapter addressAdapter;

    private Adresse selectedAddress;
    private Dialog dialog;
    private TextView address;
    private MainActivity parent;
    private Switch clientOrProspect;
    private EditText businessName, name, firstname, phoneNumber, description;
    private String idModified;


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof MainActivity) {
            parent = (MainActivity) context;
        } else {
            throw new ClassCastException("Le contexte doit être une instance d'ActivitePrincipale.");
        }
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.client_creation_fragment, container, false);

        // Initialisation des vues
        clientOrProspect = view.findViewById(R.id.switch_status);
        businessName = view.findViewById(R.id.editText_businessName);
        address = view.findViewById(R.id.textView_address);
        name = view.findViewById(R.id.editText_name);
        firstname = view.findViewById(R.id.editText_firstname);
        phoneNumber = view.findViewById(R.id.editText_phoneNumber);
        description = view.findViewById(R.id.editText_description);

        // Configuration des listeners
        clientOrProspect.setOnClickListener(this::changeStatut);
        Button save = view.findViewById(R.id.button_save);

        // On récupère les arguments mis dans le fragment
        Bundle args = getArguments();

        // On vérifie si l'argument id existe
        if (args != null && args.containsKey("id")) {
            // Modification
            idModified = args.getString("id");
            // On récupère le client par rapport à l'id
            getClient(idModified);
        }
        save.setOnClickListener(this::save);
        suggestedAddress = new ArrayList<>();
        setupAddress();
        return view;
    }

    /**
     * Configure le champ d'adresse pour afficher un dialogue de recherche d'adresse.
     */
    private void setupAddress() {
        address.setOnClickListener(this::onClickAddress);
    }

    /**
     * Méthode appelée lorsque le champ d'adresse est cliqué.
     * Affiche un dialogue pour rechercher et sélectionner une adresse.
     *
     * @param v La vue qui a été cliquée.
     */
    private void onClickAddress(View v) {
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
        title.setText(R.string.address_search_title);

        // Initialiser l'adapter
        addressAdapter = new AddressAdapter(
                parent,
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

    /**
     * Méthode appelée lorsque le bouton de sauvegarde est cliqué.
     * Sauvegarde les informations du client ou du prospect.
     *
     * @param view La vue qui a été cliquée.
     */
    private void save(View view) {
        if (areCorrectFields()) {
            String businessName = this.businessName.getText().toString();
            String addressLabel = selectedAddress.getLibelle();
            String postalCode = selectedAddress.getCodePostal();
            String city = selectedAddress.getVille();
            String description = this.description.getText().toString();
            String contactName = this.name.getText().toString();
            String contactFirstname = this.firstname.getText().toString();
            String contactPhone = this.phoneNumber.getText().toString();
            boolean isClient = clientOrProspect.isChecked();
            if (idModified != null) {
                API_REQUEST.client.update(parent, idModified, businessName, addressLabel, postalCode, city, description, contactName,
                        contactFirstname, contactPhone, isClient, response -> Toast.makeText(parent, response, Toast.LENGTH_LONG).show(), error -> Toast.makeText(parent, R.string.create_client_error, Toast.LENGTH_LONG).show());
            } else {
                API_REQUEST.client.create(parent, businessName, addressLabel, postalCode, city, description, contactName,
                        contactFirstname, contactPhone, isClient, response -> Toast.makeText(parent, response, Toast.LENGTH_LONG).show(), error -> Toast.makeText(parent, R.string.create_client_error, Toast.LENGTH_LONG).show());
            }
        }
    }

    /**
     * Méthode d'appel API pour récupérer un client par son identifiant.
     *
     * @param id L'identifiant du client à récupérer.
     */
    private void getClient(String id) {

        API_REQUEST.client.getOne(parent, id, client -> {
            clientOrProspect.setChecked(client.isClientEffectif());
            clientOrProspect.setText(client.isClientEffectif() ? R.string.switch_item_client : R.string.switch_item_prospect);
            name.setText(client.getContact().getNom());
            firstname.setText(client.getContact().getPrenom());
            selectedAddress = client.getAdresse();
            address.setText(selectedAddress.toString());
            phoneNumber.setText(client.getContact().getNumeroTelephone());
            businessName.setText(client.getNomEntreprise());
            description.setText(client.getDescriptif());
        }, error -> Toast.makeText(parent, R.string.default_error_msg
                + error.toString(), Toast.LENGTH_LONG).show());
    }

    /**
     * Méthode appelée lorsque le statut client/prospect est changé.
     *
     * @param view La vue qui a été cliquée.
     */
    private void changeStatut(View view) {
        if (clientOrProspect.isChecked()) {
            clientOrProspect.setText(R.string.switch_item_client);
        } else {
            clientOrProspect.setText(R.string.switch_item_prospect);
        }
    }

    /**
     * Vérifie si les champs du formulaire sont correctement remplis.
     *
     * @return true si tous les champs sont corrects, false sinon.
     */
    private boolean areCorrectFields() {
        return isCorrectBusinessName() &
                isCorrectContact() &
                isCorrectAddress();
    }

    /**
     * Vérifie si l'adresse sélectionnée est correcte.
     *
     * @return true si l'adresse est correcte, false sinon.
     */
    private boolean isCorrectAddress() {
        if (selectedAddress == null) {
            address.setError(getString(R.string.empty_field_error));
            return false;
        } else {
            address.setError(null);
            return true;
        }
    }

    /**
     * Vérifie si le nom de l'entreprise est correct.
     *
     * @return true si le nom de l'entreprise est correct, false sinon.
     */
    private boolean isCorrectBusinessName() {
        if (businessName.getText().toString().trim().isEmpty()) {
            businessName.setError(getString(R.string.empty_field_error));
            return false;
        }
        return true;
    }

    /**
     * Vérifie si les informations de contact sont correctes.
     *
     * @return true si les informations de contact sont correctes, false sinon.
     */
    private boolean isCorrectContact() {
        boolean isCorrect = true;
        if (isNotFilled(name.getText().toString())) {
            name.setError(getString(R.string.empty_field_error));
            isCorrect = false;
        }
        if (isNotFilled(firstname.getText().toString())) {
            firstname.setError(getString(R.string.empty_field_error));
            isCorrect = false;
        }
        if (isNotFilled(phoneNumber.getText().toString())) {
            phoneNumber.setError(getString(R.string.empty_field_error));
            isCorrect = false;
        }

        if (!isCorrectPhoneNumber(phoneNumber.getText().toString())) {
            phoneNumber.setError(getString(R.string.invalid_field_error,
                    "il ne correspond pas à un numéro de téléphone"));
            isCorrect = false;
        }
        return isCorrect;
    }

    /**
     * Vérifie si le numéro de téléphone est correct.
     *
     * @param phoneNumber Le numéro de téléphone à vérifier.
     * @return true si le numéro de téléphone est correct, false sinon.
     */
    private boolean isCorrectPhoneNumber(String phoneNumber) {
        return phoneNumber.matches("[0-9]{10}");
    }

    /**
     * Vérifie si un champ de texte est vide.
     *
     * @param text Le texte à vérifier.
     * @return true si le texte est vide, false sinon.
     */
    private boolean isNotFilled(String text) {
        return text.trim().isEmpty();
    }

}