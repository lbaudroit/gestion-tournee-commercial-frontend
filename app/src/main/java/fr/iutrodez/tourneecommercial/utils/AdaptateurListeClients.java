package fr.iutrodez.tourneecommercial.utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import fr.iutrodez.tourneecommercial.R;
import fr.iutrodez.tourneecommercial.modeles.Client;

import java.util.List;


public class AdaptateurListeClients extends ArrayAdapter<Client> {

    /**
     * Interface pour définir la logique du bouton modifier de l'adapteur
     */
    public interface OnClickModifierCallback {

        void onClickModifierClientItem(Client client);
    }

    /**
     * Interface pour définir la logique du bouton supprimer de l'adapteur
     */
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

    private final OnClickSupprimerCallback onClickSupprimerCallback;
    private final OnClickModifierCallback onClickModifierCallback;

    public AdaptateurListeClients(@NonNull Context context, int resource, @NonNull List<Client> client,
                                  OnClickSupprimerCallback onClickSupprimerCallback,
                                  OnClickModifierCallback onClickModifierCallback) {
        super(context, resource, client);
        this.identifiantVueItem = resource;
        this.onClickSupprimerCallback = onClickSupprimerCallback;
        this.onClickModifierCallback = onClickModifierCallback;
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
        TextView titre = convertView.findViewById(R.id.textView_title);
        TextView sousTitre = convertView.findViewById(R.id.textView_subTitle);
        ImageButton boutonSuppression = convertView.findViewById(R.id.imageButton_delete);
        Button boutonModifier = convertView.findViewById(R.id.button_modify);

        // Récupération de l'objet Client
        final Client clientInfo = getItem(position);
        // Définition du texte des TextViews
        assert clientInfo != null;
        titre.setText(clientInfo.getNomEntreprise());
        sousTitre.setText(clientInfo.getAdresse().getCodePostal() + " " + clientInfo.getAdresse().getVille());

        if (clientInfo != null) {
            boutonSuppression.setOnClickListener(v -> {
                onClickSupprimerCallback.onClickSupprimerClientItem(clientInfo);
            });

            // Action pour le bouton "modifier"
            boutonModifier.setOnClickListener(v -> {
                onClickModifierCallback.onClickModifierClientItem(clientInfo);
            });
        }

        // Clic sur l'ensemble de l'élément
        convertView.setOnClickListener(v -> {
            ListView listView = (ListView) parent;
            listView.performItemClick(v, position, listView.getItemIdAtPosition(position));
        });

        return convertView;
    }

}