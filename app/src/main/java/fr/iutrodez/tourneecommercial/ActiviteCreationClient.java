package fr.iutrodez.tourneecommercial;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import fr.iutrodez.tourneecommercial.modeles.Adresse;

public class ActiviteCreationClient extends AppCompatActivity{
    private Switch aSwitch;

    private static final String API_URL = "http://10.0.2.2:9090/client/creer";
    private EditText nomEntreprise, adresse, codePostal, ville, nom, prenom, numTel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activite_creation_client);
        ActionBar actionBar = getSupportActionBar();
        aSwitch = findViewById(R.id.statut);
        nomEntreprise = findViewById(R.id.nomEntreprise);
        adresse = findViewById(R.id.adresse);
        codePostal = findViewById(R.id.code_postal);
        ville = findViewById(R.id.ville);
        nom = findViewById(R.id.nom);
        prenom = findViewById(R.id.prenom);
        numTel = findViewById(R.id.num_tel);

        if (actionBar != null) {
            actionBar.setTitle("Création nouveau client");
        }
    }

    /**
     * Change le statut True client False Prospect
     * @param view
     */
    public void changeStatut(View view) {
        if(aSwitch.isChecked()){
            aSwitch.setText("Client");
        }else{
            aSwitch.setText("Prospect");
        }
    }

    private JSONObject createClientJson() throws JSONException {


        JSONObject adresseData = new JSONObject();
        adresseData.put("libelle" , adresse.getText().toString());
        adresseData.put("codePostal",codePostal.getText().toString());
        adresseData.put("ville",ville.getText().toString());

        JSONObject contact = new JSONObject();
        contact.put("nom", nom.getText().toString());
        contact.put("prenom", prenom.getText().toString());
        contact.put("telephone", numTel.getText().toString());

        JSONObject clientData = new JSONObject();
        clientData.put("nomEntreprise", nomEntreprise.getText().toString());
        clientData.put("adresse",adresseData);

        clientData.put("contact",contact);

        //clientData.put("statut", aSwitch.isChecked() ? "Client" : "Prospect");

        return clientData;
    }

    /**
     * enregistrement du client
     * @param view
     *
     */
    public void enregistrer(View view){
        try {
            JSONObject postData = createClientJson();
            RequestQueue requestQueue = Volley.newRequestQueue(this);

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                    Request.Method.POST,
                    API_URL,
                    postData,
                    new Response.Listener<JSONObject>() {
                        @Override
                            public void onResponse(JSONObject response) {
                                Toast.makeText(ActiviteCreationClient.this, "Client créé avec succès", Toast.LENGTH_SHORT).show();
                            // Retourner à la liste des clients ou effectuer une autre action
                            System.out.println("marche");
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            System.out.println(error.toString());

                            Toast.makeText(ActiviteCreationClient.this,
                                    "Erreur lors de la création du client: " + error.toString(),
                                    Toast.LENGTH_LONG).show();
                        }
                    }
            );

            requestQueue.add(jsonObjectRequest);

        } catch (JSONException e) {
            System.out.println(e.toString());

            Toast.makeText(this,
                    "Erreur lors de la création des données: " + e.getMessage(),
                    Toast.LENGTH_LONG).show();
        }
    }
}