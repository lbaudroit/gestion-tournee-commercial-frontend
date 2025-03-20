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
import fr.iutrodez.tourneecommercial.model.dto.ParcoursReducedDTO;

import java.util.List;
import java.util.function.BiConsumer;

/**
 * Adapteur pour la liste d'historique.
 *
 * @author Benjamin NICOL, Enzo CLUZEL, Ahmed BRIBACH, Leïla BAUDROIT
 */
public class HistoryListAdapter extends ArrayAdapter<ParcoursReducedDTO> {

    private final int viewIdentifier;
    private final LayoutInflater inflater;
    final BiConsumer<ParcoursReducedDTO, Integer> onClickButtonDelete;

    /**
     * Constructeur de l'adaptateur de la liste d'historique.
     *
     * @param context             Le contexte de l'application.
     * @param resource            L'identifiant de la ressource de la vue.
     * @param objects             La liste des objets `ParcoursReducedDTO` à afficher.
     * @param onClickButtonDelete Le consommateur pour gérer les clics sur le bouton de suppression.
     */
    public HistoryListAdapter(@NonNull Context context,
                              int resource,
                              @NonNull List<ParcoursReducedDTO> objects,
                              BiConsumer<ParcoursReducedDTO, Integer> onClickButtonDelete) {
        super(context, resource, objects);
        this.viewIdentifier = resource;
        this.onClickButtonDelete = onClickButtonDelete;
        inflater = (LayoutInflater) getContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        System.out.println("getView: " + position);
        if (convertView == null) {
            convertView = inflater.inflate(viewIdentifier, parent, false);
        }
        TextView title = convertView.findViewById(R.id.textView_title);
        TextView subTitle = convertView.findViewById(R.id.textView_subTitle);

        final ParcoursReducedDTO ParcoursInfos = getItem(position);
        assert ParcoursInfos != null;

        title.setText(ParcoursInfos.getNom());
        subTitle.setText(ParcoursInfos.getDate());
        convertView.findViewById(R.id.imageButton_delete).setOnClickListener(v -> this.onClickButtonDelete.accept(ParcoursInfos, position));
        convertView.setOnClickListener(v -> {
            ListView listView = (ListView) parent;
            listView.performItemClick(v, position, listView.getItemIdAtPosition(position));
        });
        System.out.println("getView: " + position);
        return convertView;
    }
}
