package fr.iutrodez.tourneecommercial;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

import fr.iutrodez.tourneecommercial.consommateurapi.FakeData;
import fr.iutrodez.tourneecommercial.modeles.Client;
import fr.iutrodez.tourneecommercial.utils.AdaptateurListeClient;

public class ActiviteCreationItineraire extends AppCompatActivity {

    public ActivitePrincipale parent;
    private EditText nom;
    private ListView listeClientsAjoutes;
    private Button btnAjouterClient;
    private Button btnGenererItineraire;
    private Button btnValider;

    private String testData;


    public static ActiviteCreationItineraire newInstance() {
        return new ActiviteCreationItineraire();
    }

//    @Override
//    public void onAttach(Context context) {
//        super.onAttach(context);
//        parent = (ActivitePrincipale) context;
//    }

//    @Nullable
//    @Override
//    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        // Charger le layout du fragment
//        return inflater.inflate(R.layout.fragment_creation_itineraire, container, false);
//    }

//    @Override
//    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
//        super.onViewCreated(view, savedInstanceState);
//
//        // Initialiser les composants
//
//        // Ajouter les écouteurs
//
//        // Configurer le bouton d'enregistrement
//        view.findViewById(R.id.enregistrer).setOnClickListener(this::enregistrer);
//    }

    List<Client> entreprises = FakeData.getClients();
@Override
protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.fragment_creation_itineraire);

    nom = findViewById(R.id.nom_itineraire);
    listeClientsAjoutes = findViewById(R.id.list_clients);
    btnAjouterClient = findViewById(R.id.ajouter);
    btnGenererItineraire = findViewById(R.id.generer);
    btnValider = findViewById(R.id.valider);

    AdaptateurListeClient adapter = new AdaptateurListeClient(
            this,
            R.layout.listitem_client,
            entreprises,
            null,
            null);
    listeClientsAjoutes.setAdapter(adapter);

    ActionBar actionBar = getSupportActionBar();
    if (actionBar != null) {
        actionBar.setTitle(getString(R.string.creation_itineraire));
    }
}

    public void ajouter(View view) {
        // TODO ouvrir une liste de clients avec auto-complétion
//        AlerteAjoutClient.newInstance().show(this.getSupportFragmentManager(), "dialog");
        Toast.makeText(this, R.string.todo, Toast.LENGTH_SHORT).show();
    }

    public void generer(View view) {
        /*
        TODO Envoyer une requête à l'API pour générer un parcours optimal
        Réordonner les clients à la suite de celle-ci si elle réussit
         */
        Toast.makeText(this, R.string.todo, Toast.LENGTH_SHORT).show();
    }

    public void valider(View view) {
        /*
        TODO Envoyer la demande à l'API pour créer cet itinéraire
        Si elle réussit, fermer la page actuelle et revenir à la liste des itinéraires
         */
        Toast.makeText(this, R.string.todo, Toast.LENGTH_SHORT).show();
    }
}
