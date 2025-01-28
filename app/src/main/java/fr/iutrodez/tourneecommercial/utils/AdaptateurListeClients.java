package fr.iutrodez.tourneecommercial.utils;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import java.util.List;

import fr.iutrodez.tourneecommercial.ActivitePrincipale;
import fr.iutrodez.tourneecommercial.R;
import fr.iutrodez.tourneecommercial.modeles.Client;
import fr.iutrodez.tourneecommercial.modeles.Itineraire;


public class AdaptateurListeClients extends ArrayAdapter<Client> {

    /**
     * Identifiant de la vue permettant d’afficher chaque item de la liste
     */
    private final int identifiantVueItem;

    /**
     * Objet utilitaire permettant de dé-sérialiser une vue
     */
    private final LayoutInflater inflater;

    private ActivitePrincipale activitePrincipale;

    public AdaptateurListeClients(@NonNull Context context, int resource , @NonNull List<Client> client) {
        super(context, resource , client);
        this.identifiantVueItem = resource;
        this.activitePrincipale = (ActivitePrincipale) context;
        inflater = (LayoutInflater) getContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        // Opérations de récupération de la vue
        if (convertView == null) {
            convertView = inflater.inflate(identifiantVueItem, parent, false);
        }

        // Récupération des vues
        TextView titre = convertView.findViewById(R.id.list_client_titre);
        TextView sousTitre = convertView.findViewById(R.id.list_client_sous_titre);
        ImageButton boutonSuppression = convertView.findViewById(R.id.supprimer);
        Button boutonModifier = convertView.findViewById(R.id.button);

        // Récupération de l'objet Client
        final Client clientInfo = getItem(position);
        // Définition du texte des TextViews
        assert clientInfo != null;
        titre.setText(clientInfo.getNomEntreprise());
        sousTitre.setText(clientInfo.getAdresse().getCodePostal() + " " + clientInfo.getAdresse().getVille());

        if (clientInfo != null) {
            // Définir les textes
            boutonSuppression.setOnClickListener(this::onClickBtnSuppression);

            // Action pour le bouton "modifier"
            boutonModifier.setOnClickListener(v -> {
                Bundle bundle = new Bundle();
                bundle.putString("id",clientInfo.get_id());
                activitePrincipale.navigateToFragment(ActivitePrincipale.FRAGMENT_CREATION_CLIENT,false,bundle);

                Toast.makeText(getContext(), "Modifier : " + clientInfo.getNomEntreprise(), Toast.LENGTH_SHORT).show();
            });
        }

        // Clic sur l'ensemble de l'élément
        convertView.setOnClickListener(v -> {
            ListView listView = (ListView) parent;
            listView.performItemClick(v, position, listView.getItemIdAtPosition(position));
        });

        return convertView;
    }



    private void onClickBtnSuppression(View vue) {
        // TODO demander et supprimer l'itinéraire
        Toast.makeText(getContext(), R.string.todo, Toast.LENGTH_SHORT).show();
    }
}