package fr.iutrodez.tourneecommercial.utils.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import fr.iutrodez.tourneecommercial.modeles.Adresse;

import java.util.List;

public class AddressAdapter extends ArrayAdapter<Adresse> {
    private final int viewIdentifier;
    private final LayoutInflater inflater;

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
