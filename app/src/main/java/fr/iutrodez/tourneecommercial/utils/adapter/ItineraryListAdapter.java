package fr.iutrodez.tourneecommercial.utils.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import fr.iutrodez.tourneecommercial.R;
import fr.iutrodez.tourneecommercial.model.Itineraire;

import java.util.List;
import java.util.function.BiConsumer;

/**
 * Adapter for the list of itineraries
 */
public class ItineraryListAdapter extends ArrayAdapter<Itineraire> {

    private final int viewIdentifier;
    private final LayoutInflater inflater;

    BiConsumer<Itineraire, Integer> onClickButtonDelete;

    BiConsumer<Itineraire, Integer> onClickButtonModify;

    /**
     * Constructeur de l'adaptateur pour la liste des itinéraires.
     *
     * @param context             Le contexte de l'application.
     * @param resource            L'identifiant de la vue de l'élément de la liste.
     * @param objects             La liste des itinéraires à afficher.
     * @param onClickButtonModify Action à exécuter lors du clic sur le bouton de modification.
     * @param onClickButtonDelete Action à exécuter lors du clic sur le bouton de suppression.
     */
    public ItineraryListAdapter(@NonNull Context context,
                                int resource,
                                @NonNull List<Itineraire> objects,
                                BiConsumer<Itineraire, Integer> onClickButtonModify,
                                BiConsumer<Itineraire, Integer> onClickButtonDelete) {
        super(context, resource, objects);
        this.viewIdentifier = resource;
        this.onClickButtonModify = onClickButtonModify;
        this.onClickButtonDelete = onClickButtonDelete;
        inflater = (LayoutInflater) getContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    /**
     * Méthode pour obtenir la vue d'un élément de la liste.
     *
     * @param position    La position de l'élément dans la liste.
     * @param convertView La vue réutilisable de l'élément.
     * @param parent      Le parent auquel cette vue sera attachée.
     * @return La vue de l'élément à la position spécifiée.
     */
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(viewIdentifier, parent, false);
        }
        TextView title = convertView.findViewById(R.id.textView_title);
        TextView subTitle = convertView.findViewById(R.id.textView_subTitle);

        final Itineraire itineraryInfos = getItem(position);
        assert itineraryInfos != null;

        title.setText(itineraryInfos.getNom());
        String distanceText = getContext().getString(R.string.display_distance_km);
        subTitle.setText(String.format(distanceText, itineraryInfos.getKilometres()));
        convertView.findViewById(R.id.imageButton_delete).setOnClickListener(v -> this.onClickButtonDelete.accept(itineraryInfos, position));
        convertView.findViewById(R.id.button_modify).setOnClickListener(v -> this.onClickButtonModify.accept(itineraryInfos, position));
        convertView.setOnClickListener(v -> {
            ListView listView = (ListView) parent;
            listView.performItemClick(v, position, listView.getItemIdAtPosition(position));
        });

        return convertView;
    }
}
