package fr.iutrodez.tourneecommercial.utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

import fr.iutrodez.tourneecommercial.modeles.Adresse;

public class AdaptateurAdresse extends ArrayAdapter<Adresse> {
    private final int identifiantVueItem;
    private final LayoutInflater inflater;

    public AdaptateurAdresse(@NonNull Context contexte,
                             int resource,
                             @NonNull List<Adresse> objects) {
        super(contexte, resource, objects);
        this.identifiantVueItem = resource;
        inflater = (LayoutInflater) getContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(identifiantVueItem, parent, false);
        }
        TextView titre = convertView.findViewById(android.R.id.text1);
        final Adresse adresse = getItem(position);
        assert adresse != null;
        titre.setText(adresse.toString());
        return convertView;
    }
}
