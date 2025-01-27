package fr.iutrodez.tourneecommercial.utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

import fr.iutrodez.tourneecommercial.R;
import fr.iutrodez.tourneecommercial.modeles.Adresse;
import fr.iutrodez.tourneecommercial.modeles.Client;

public class AdaptateurListeClient extends ArrayAdapter<Client> {

    public interface OnClickModifierCallback {

        void onClickModifierClientItem(Client client);
    }

    public interface OnClickSupprimerCallback {

        void onClickSupprimerClientItem(Client client);
    }

    /**
     * Identifiant de la vue permettant d’afficher chaque item de la liste
     */
    private final int identifiantVueItem;

    /**
     * Objet utilitaire permettant de dé-sérialiser une vue
     */
    private final LayoutInflater inflater;

    private final OnClickModifierCallback onClickModifier;
    private final OnClickSupprimerCallback onClickSupprimer;

    public AdaptateurListeClient(@NonNull Context contexte,
                                 int resource,
                                 @NonNull List<Client> objects,
                                 OnClickModifierCallback onClickModifier,
                                 OnClickSupprimerCallback onClickSupprimer) {
        super(contexte, resource, objects);
        this.identifiantVueItem = resource;
        inflater = (LayoutInflater) getContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.onClickModifier = onClickModifier;
        this.onClickSupprimer = onClickSupprimer;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        // Opérations de récupération de la vue
        if (convertView == null) {
            convertView = inflater.inflate(identifiantVueItem, parent, false);
        }

        // Récupération des vues à l’intérieur de l’élément de la liste
        TextView titre = convertView.findViewById(R.id.titre);
        TextView sousTitre = convertView.findViewById(R.id.sous_titre);
        ImageButton boutonSuppression = convertView.findViewById(R.id.supprimer);
        Button boutonModification = convertView.findViewById(R.id.modifier);

        // Récupération de l’objet Client correspondant à cette position
        final Client infosClient = getItem(position);

        // Définition du texte des TextViews
        assert infosClient != null;
        Adresse adresse = infosClient.getAdresse();
        titre.setText(infosClient.getNomEntreprise());
        sousTitre.setText(String.format("%s %s", adresse.getCodePostal(), adresse.getVille()));

        // Ajoute le listener de suppression s'il existe, sinon cache le bouton
        if (onClickSupprimer != null) {
            boutonSuppression.setOnClickListener((View v) -> {
                onClickSupprimer.onClickSupprimerClientItem(infosClient);
            });
            boutonSuppression.setVisibility(View.VISIBLE);
        } else {
            boutonSuppression.setVisibility(View.GONE);
        }

        // Ajoute le listener de modification s'il existe, sinon cache le bouton
        if (onClickModifier != null) {
            boutonModification.setOnClickListener((View v) -> {
                onClickModifier.onClickModifierClientItem(infosClient);
            });
            boutonModification.setVisibility(View.VISIBLE);
        } else {
            boutonModification.setVisibility(View.GONE);
        }

        // Gestion du clic sur l’ensemble de l’élément de la liste
        convertView.setOnClickListener(v -> {
            // Obtenez la ListView parente à partir de la vue fournie par le convertView
            ListView listView = (ListView) parent;

            // Simuler un clic sur l’élément de la liste à la position donnée
            listView.performItemClick(v, position, listView.getItemIdAtPosition(position));
        });

        return convertView;
    }
}
