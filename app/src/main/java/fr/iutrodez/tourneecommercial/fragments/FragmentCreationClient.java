package fr.iutrodez.tourneecommercial.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.VolleyError;

import fr.iutrodez.tourneecommercial.ActivitePrincipale;
import fr.iutrodez.tourneecommercial.R;
import fr.iutrodez.tourneecommercial.utils.ApiRequest;

public class FragmentCreationClient extends Fragment {
    private ActivitePrincipale parent;
    private Switch aSwitch;
    private EditText nomEntreprise, adresse, codePostal, ville, nom, prenom, numTel;

    public static FragmentCreationClient newInstance() {
        return new FragmentCreationClient();
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
        View view = inflater.inflate(R.layout.activite_creation_client, container, false);

        // Initialisation des vues
        aSwitch = view.findViewById(R.id.statut);
        nomEntreprise = view.findViewById(R.id.nomEntreprise);
        adresse = view.findViewById(R.id.adresse);
        codePostal = view.findViewById(R.id.code_postal);
        ville = view.findViewById(R.id.ville);
        nom = view.findViewById(R.id.nom);
        prenom = view.findViewById(R.id.prenom);
        numTel = view.findViewById(R.id.num_tel);

        // Configuration des listeners
        aSwitch.setOnClickListener(this::changeStatut);
        view.findViewById(R.id.enregistrer).setOnClickListener(this::enregistrer);

        return view;
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
        try {
            JSONObject postData = createClientJson();
            String url = "client/creer";
            ApiRequest.creationClient(requireContext(), url, postData, new ApiRequest.ApiResponseCallback<JSONObject>() {
                @Override
                public void onSuccess(JSONObject response) {
                    Toast.makeText(requireContext(), "Client créé avec succès", Toast.LENGTH_SHORT).show();
                    // Retourner au fragment de liste des clients
                    parent.replaceMainFragment(FragmentClients.newInstance());
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
}