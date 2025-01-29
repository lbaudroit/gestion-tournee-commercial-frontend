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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.android.volley.VolleyError;

import org.json.JSONObject;

import java.util.List;
import java.util.function.BiConsumer;

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

    BiConsumer<Itineraire, Integer> onClickBtnSuppression;

    BiConsumer<Itineraire, Integer> onClickBtnModification;

    public AdaptateurListeItineraire(@NonNull Context contexte,
                                     int resource,
                                     @NonNull List<Itineraire> objects,
                                     BiConsumer<Itineraire, Integer> onClickBtnModification,
                                     BiConsumer<Itineraire, Integer> onClickBtnSuppression) {
        super(contexte, resource, objects);
        this.identifiantVueItem = resource;
        this.onClickBtnModification = onClickBtnModification;
        this.onClickBtnSuppression = onClickBtnSuppression;
        inflater = (LayoutInflater) getContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(identifiantVueItem, parent, false);
        }
        TextView titre = convertView.findViewById(R.id.titre);
        TextView sousTitre = convertView.findViewById(R.id.sous_titre);
        ImageButton boutonSuppression = convertView.findViewById(R.id.supprimer);
        Button boutonModification = convertView.findViewById(R.id.modifier);

        final Itineraire infosItineraire = getItem(position);
        assert infosItineraire != null;

        titre.setText(infosItineraire.getNom());
        String texteKm = getContext().getString(R.string.affichage_nombre_km);
        sousTitre.setText(String.format(texteKm, infosItineraire.getKilometres()));
        boutonSuppression.setOnClickListener(v -> this.onClickBtnSuppression.accept(infosItineraire, position));
        boutonModification.setOnClickListener(v -> this.onClickBtnModification.accept(infosItineraire, position));
        convertView.setOnClickListener(v -> {
            ListView listView = (ListView) parent;
            listView.performItemClick(v, position, listView.getItemIdAtPosition(position));
        });

        return convertView;
    }
}
