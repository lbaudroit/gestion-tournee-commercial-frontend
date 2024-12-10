package fr.iutrodez.tourneecommercial.utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

import fr.iutrodez.tourneecommercial.R;
import fr.iutrodez.tourneecommercial.modeles.Itineraire;

public class AdaptateurListeItineraire extends ArrayAdapter<Itineraire> {

    /**
     * Identifiant de la vue permettant d’afficher chaque item de la liste
     */
    private final int identifiantVueItem;

    /**
     * Objet utilitaire permettant de dé-sérialiser une vue
     */
    private final LayoutInflater inflater;

    public AdaptateurListeItineraire(@NonNull Context contexte,
                                     int resource,
                                     @NonNull List<Itineraire> objects) {
        super(contexte, resource, objects);
        this.identifiantVueItem = resource;
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

        // Récupération des vues à l’intérieur de l’élément de la liste
        TextView titre = convertView.findViewById(R.id.titre);
        TextView sousTitre = convertView.findViewById(R.id.sous_titre);
        ImageButton boutonSuppression = convertView.findViewById(R.id.supprimer);

        // Récupération de l’objet ItemFestival correspondant à cette position
        final Itineraire infosItineraire = getItem(position);

        // Définition du texte des TextViews
        assert infosItineraire != null;
        titre.setText(infosItineraire.getNom());

        String texteKm = getContext().getString(R.string.affichage_nombre_km);
        sousTitre.setText(String.format(texteKm, infosItineraire.getKilometres()));

        // Gestion du clic sur la checkbox
        boutonSuppression.setOnClickListener(this::onClickBtnSuppression);

        // Gestion du clic sur l’ensemble de l’élément de la liste
        convertView.setOnClickListener(v -> {
            // Obtenez la ListView parente à partir de la vue fournie par le convertView
            ListView listView = (ListView) parent;

            // Simuler un clic sur l’élément de la liste à la position donnée
            listView.performItemClick(v, position, listView.getItemIdAtPosition(position));
        });

        return convertView;
    }

    private void onClickBtnSuppression(View vue) {
        // TODO demander et supprimer l'itinéraire
        Toast.makeText(getContext(), R.string.todo, Toast.LENGTH_SHORT).show();
    }
}