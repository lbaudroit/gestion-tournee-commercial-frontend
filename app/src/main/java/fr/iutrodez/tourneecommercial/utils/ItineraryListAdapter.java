package fr.iutrodez.tourneecommercial.utils;

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
import fr.iutrodez.tourneecommercial.modeles.Itineraire;

import java.util.List;
import java.util.function.BiConsumer;

public class ItineraryListAdapter extends ArrayAdapter<Itineraire> {

    /**
     * Identifiant de la vue permettant d’afficher chaque item de la liste
     */
    private final int viewIdentifier;

    /**
     * Objet utilitaire permettant de dé-sérialiser une vue
     */
    private final LayoutInflater inflater;

    BiConsumer<Itineraire, Integer> onClickButtonDelete;

    BiConsumer<Itineraire, Integer> onClickButtonModify;

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
        String distanceText = getContext().getString(R.string.affichage_nombre_km);
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
