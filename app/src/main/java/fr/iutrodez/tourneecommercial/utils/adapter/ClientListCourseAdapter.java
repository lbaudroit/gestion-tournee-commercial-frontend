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

import java.util.List;

import fr.iutrodez.tourneecommercial.R;
import fr.iutrodez.tourneecommercial.model.Visit;

public class ClientListCourseAdapter extends ArrayAdapter<Visit> {


    public interface OnClickList {
        public void OnClick(Visit visit);
    }
    /**
     * Identifiant de la vue permettant d’afficher chaque item de la liste
     */
    private final int viewIdentifier;

    /**
     * Objet utilitaire permettant de dé-sérialiser une vue
     */
    private final LayoutInflater inflater;

    private final List<Visit> Visit;

    private final OnClickList onClickList;

    /**
     * Constructeur de l'adaptateur de la liste des clients.
     *
     * @param context     Le contexte de l'application.
     * @param resource    L'identifiant de la vue pour chaque élément de la liste.
     * @param objects     La liste des objets Client à afficher.
     * @param onClickList
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

    /**
     * Retourne le nombre d'éléments dans la liste filtrée.
     *
     * @return Le nombre d'éléments dans la liste filtrée.
     */
    @Override
    public int getCount() {
        return Visit.size();
    }

    /**
     * Retourne l'objet Client à la position spécifiée.
     *
     * @param position La position de l'élément dans la liste.
     * @return L'objet Client à la position spécifiée.
     */
    @Nullable
    @Override
    public Visit getItem(int position) {
        return Visit.get(position);
    }

    /**
     * Retourne la vue pour un élément de la liste à la position spécifiée.
     *
     * @param position    La position de l'élément dans la liste.
     * @param convertView La vue réutilisable.
     * @param parent      Le parent de la vue.
     * @return La vue pour l'élément de la liste.
     */
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
            convertView.setOnClickListener((View v)->onClickList.OnClick(etapeInfos));
        }

        return convertView;
    }
}
