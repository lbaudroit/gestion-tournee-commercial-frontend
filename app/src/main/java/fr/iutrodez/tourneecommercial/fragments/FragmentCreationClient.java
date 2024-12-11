package fr.iutrodez.tourneecommercial.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import fr.iutrodez.tourneecommercial.R;

import fr.iutrodez.tourneecommercial.ActivitePrincipale;

public class FragmentCreationClient extends Fragment {

    public ActivitePrincipale parent;

    public static FragmentCreationClient newInstance() {
        return new FragmentCreationClient();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        parent = (ActivitePrincipale) context;
    }

    private Switch aSwitch;

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

    /**
     * Enregistrement du client
     * @param view
     */
    public void enregistrer(View view) {
        System.out.println("enregistrement");
        // Exemple de gestion d'une intention dans un fragment

    }
}
