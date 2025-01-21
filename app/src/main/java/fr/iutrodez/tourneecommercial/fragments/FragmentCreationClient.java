package fr.iutrodez.tourneecommercial.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import fr.iutrodez.tourneecommercial.R;

import fr.iutrodez.tourneecommercial.ActivitePrincipale;

public class FragmentCreationClient extends Fragment {

    public ActivitePrincipale parent;
    private Switch aSwitch;
    private EditText nomEntreprise, adresse, codePostal, ville, nom, prenom, numTel;
    private static final String API_URL = "http://localhost:9090/client/creer/";
    public static FragmentCreationClient newInstance() {
        return new FragmentCreationClient();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        parent = (ActivitePrincipale) context;
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Charger le layout du fragment
        return inflater.inflate(R.layout.activite_creation_client, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialiser les composants
        aSwitch = view.findViewById(R.id.statut);
        nomEntreprise = view.findViewById(R.id.nomEntreprise);
        adresse = view.findViewById(R.id.adresse);
        codePostal = view.findViewById(R.id.code_postal);
        ville = view.findViewById(R.id.ville);
        nom = view.findViewById(R.id.nom);
        prenom = view.findViewById(R.id.prenom);
        numTel = view.findViewById(R.id.num_tel);

        // Ajouter un écouteur au Switch
        aSwitch.setOnClickListener(this::changeStatut);

        // Configurer le bouton d'enregistrement
        view.findViewById(R.id.enregistrer).setOnClickListener(this::enregistrer);
    }

    /**
     * Change le statut True client False Prospect
     * @param view
     */
    public void changeStatut(View view) {
        if (aSwitch.isChecked()) {
            aSwitch.setText("Client");
        } else {
            aSwitch.setText("Prospect");
        }
    }
    private JSONObject createClientJson() throws JSONException {
        JSONObject clientData = new JSONObject();
        clientData.put("nom_entreprise", nomEntreprise.getText().toString());
        clientData.put("adresse", adresse.getText().toString());
        clientData.put("code_postal", codePostal.getText().toString());
        clientData.put("ville", ville.getText().toString());
        clientData.put("nom", nom.getText().toString());
        clientData.put("prenom", prenom.getText().toString());
        clientData.put("telephone", numTel.getText().toString());
        clientData.put("statut", aSwitch.isChecked() ? "Client" : "Prospect");
        System.out.println(clientData.toString());
        return clientData;
    }
    /**
     * Enregistrement du client
     * @param view
     */
    public void enregistrer(View view) {
        try {
            JSONObject postData = createClientJson();
            System.out.print("jaccept");
            RequestQueue requestQueue = Volley.newRequestQueue(getContext());

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                    Request.Method.PUT,
                    API_URL,
                    postData,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            Toast.makeText(getContext(), "Client créé avec succès", Toast.LENGTH_SHORT).show();
                            // Retourner à la liste des clients ou effectuer une autre action
                            if (parent != null) {
                                System.out.println("marche");
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(getContext(),
                                    "Erreur lors de la création du client: " + error.toString(),
                                    Toast.LENGTH_LONG).show();
                        }
                    }
            );

            requestQueue.add(jsonObjectRequest);

        } catch (JSONException e) {
            Toast.makeText(getContext(),
                    "Erreur lors de la création des données: " + e.getMessage(),
                    Toast.LENGTH_LONG).show();
        }

    }
}
