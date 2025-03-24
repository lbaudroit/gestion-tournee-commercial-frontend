package fr.iutrodez.tourneecommercial.utils.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import fr.iutrodez.tourneecommercial.model.Adresse;

import java.util.List;

/**
 * Adapteur pour les adresses.
 *
 * @author Benjamin NICOL, Enzo CLUZEL, Ahmed BRIBACH, Leïla BAUDROIT
 */
public class AddressAdapter extends ArrayAdapter<Adresse> {
    private final int viewIdentifier;
    private final LayoutInflater inflater;

    /**
     * Constructeur de l'AddressAdapter.
     *
     * @param context  Le contexte de l'application.
     * @param resource L'identifiant de la vue à utiliser pour chaque élément.
     * @param objects  La liste des objets Adresse à afficher.
     */
    public AddressAdapter(@NonNull Context context,
                          int resource,
                          @NonNull List<Adresse> objects) {
        super(context, resource, objects);
        this.viewIdentifier = resource;
        inflater = (LayoutInflater) getContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(viewIdentifier, parent, false);
        }
        TextView title = convertView.findViewById(android.R.id.text1);
        final Adresse address = getItem(position);
        assert address != null;
        title.setText(address.toString());
        return convertView;
    }
}
