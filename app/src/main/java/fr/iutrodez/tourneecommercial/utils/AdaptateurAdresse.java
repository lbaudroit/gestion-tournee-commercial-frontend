package fr.iutrodez.tourneecommercial.utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.function.Consumer;

public class AdaptateurAdresse extends ArrayAdapter<JSONObject> {

    /**
     * Identifiant de la vue permettant d’afficher chaque item de la liste
     */
    private final int identifiantVueItem;

    /**
     * Objet utilitaire permettant de dé-sérialiser une vue
     */
    private final LayoutInflater inflater;

    private final Consumer<JSONObject> listener;

    public AdaptateurAdresse(@NonNull Context contexte,
                             int resource,
                             @NonNull List<JSONObject> objects, Consumer<JSONObject> listener) {
        super(contexte, resource, objects);
        this.identifiantVueItem = resource;
        inflater = (LayoutInflater) getContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.listener = listener;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        // Opérations de récupération de la vue
        if (convertView == null) {
            convertView = inflater.inflate(identifiantVueItem, parent, false);
        }

        // Récupération des vues à l’intérieur de l’élément de la liste
        TextView titre = convertView.findViewById(android.R.id.text1);

        // Récupération de l’objet ItemFestival correspondant à cette position
        final JSONObject infosAdresse = getItem(position);

        // Définition du texte des TextViews
        assert infosAdresse != null;
        try {
            String address = infosAdresse.getString("name") + ", " + infosAdresse.getString("postcode") + ", " + infosAdresse.getString("city");
            titre.setText(address);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        // Gestion du clic sur l’ensemble de l’élément de la liste
        convertView.setOnClickListener((v) -> listener.accept(infosAdresse));

        return convertView;
    }
}
