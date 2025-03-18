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
 * Adapter for the Address class
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

    /**
     * Méthode pour obtenir la vue d'un élément à une position spécifique.
     *
     * @param position    La position de l'élément dans la liste.
     * @param convertView La vue réutilisable (peut être null).
     * @param parent      Le parent auquel cette vue sera attachée.
     * @return La vue pour l'élément de la position spécifiée.
     */
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
