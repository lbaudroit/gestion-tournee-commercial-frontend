package fr.iutrodez.tourneecommercial.utils.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import fr.iutrodez.tourneecommercial.R;
import fr.iutrodez.tourneecommercial.model.Visit;

import java.util.List;

/**
 * Adapteur permettant de gérer l’affichage des clients (sous forme de visite) dans l'historique.
 *
 * @author Benjamin NICOL, Enzo CLUZEL, Ahmed BRIBACH, Leïla BAUDROIT
 */
public class ClientListCourseAdapter extends ArrayAdapter<Visit> {


    private final int viewIdentifier;
    private final LayoutInflater inflater;
    private final List<Visit> Visit;
    private final OnClickList onClickList;

    /**
     * Constructeur de l'adaptateur de la liste des clients.
     *
     * @param context     Le contexte de l'application.
     * @param resource    L'identifiant de la vue pour chaque élément de la liste.
     * @param objects     La liste des objets Client à afficher.
     * @param onClickList L'action à effectuer lors du click sur un élément de la liste.
     */
    public ClientListCourseAdapter(@NonNull Context context,
                                   int resource,
                                   @NonNull List<Visit> objects, OnClickList onClickList) {
        super(context, resource, objects);
        this.viewIdentifier = resource;
        this.Visit = objects;
        this.onClickList = onClickList;
        inflater = (LayoutInflater) getContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return Visit.size();
    }

    @Nullable
    @Override
    public Visit getItem(int position) {
        return Visit.get(position);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        // Opérations de récupération de la vue
        if (convertView == null) {
            convertView = inflater.inflate(viewIdentifier, parent, false);
        }


        // Récupération des vues à l’intérieur de l’élément de la liste
        TextView name = convertView.findViewById(R.id.textView_course_client);
        CheckBox visited = convertView.findViewById(R.id.checkbox_course_client);

        // Récupération de l’objet Visit correspondant à cette position
        final Visit etapeInfos = getItem(position);

        // Définition du texte des TextViews
        assert etapeInfos != null;

        name.setText(etapeInfos.getName());
        visited.setChecked(etapeInfos.isVisited());

        if (onClickList != null) {
            convertView.setOnClickListener((View v) -> onClickList.OnClick(etapeInfos));
        }

        return convertView;
    }

    /**
     * Interface pour définir la loqique lors du clic sur un élément de la liste appartenant à cette adapteur.
     */
    public interface OnClickList {
        void OnClick(Visit visit);
    }
}
