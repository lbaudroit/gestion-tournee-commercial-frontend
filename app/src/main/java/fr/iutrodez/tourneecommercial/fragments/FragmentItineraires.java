package fr.iutrodez.tourneecommercial.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.List;

import fr.iutrodez.tourneecommercial.ActivitePrincipale;
import fr.iutrodez.tourneecommercial.R;
import fr.iutrodez.tourneecommercial.modeles.Itineraire;
import fr.iutrodez.tourneecommercial.utils.AdaptateurListeItineraire;

public class FragmentItineraires extends Fragment {

    public static FragmentItineraires newInstance() {
        return new FragmentItineraires();
    }

    public ActivitePrincipale parent;

    private ListView liste;
    private Button ajouter;
    private AdaptateurListeItineraire adaptateur;

    // TODO remove test data
    private List<Itineraire> itineraires = List.of(
            new Itineraire("Soupe", 50),
            new Itineraire("Risotto", 48),
            new Itineraire("Patate", 103)
    );

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        parent = (ActivitePrincipale) context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View frag = inflater.inflate(R.layout.fragment_liste_itineraires, container, false);
        liste = frag.findViewById(R.id.list_itineraires);
        ajouter = frag.findViewById(R.id.ajouter);

        // On utilise un adaptateur custom pour gérer les éléments de liste avec leurs boutons
        adaptateur = new AdaptateurListeItineraire(
                this.parent,
                R.layout.listitem_itineraire,
                itineraires);
        liste.setAdapter(adaptateur);

        ajouter.setOnClickListener(this::onClickAjouter);
        return frag;
    }

    private void onClickAjouter(View view) {
        // TODO add itineraire
        Toast.makeText(this.parent, R.string.todo, Toast.LENGTH_SHORT).show();
    }
}
