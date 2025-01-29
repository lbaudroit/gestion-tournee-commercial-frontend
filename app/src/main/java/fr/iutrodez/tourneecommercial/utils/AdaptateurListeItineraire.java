package fr.iutrodez.tourneecommercial.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.android.volley.VolleyError;

import org.json.JSONObject;

import java.util.List;

import fr.iutrodez.tourneecommercial.R;
import fr.iutrodez.tourneecommercial.modeles.Itineraire;

public class AdaptateurListeItineraire extends ArrayAdapter<Itineraire> {

    /**
     * Identifiant de la vue permettant d’afficher chaque item de la liste
     */
    private final int identifiantVueItem;

    /**
     * Objet utilitaire permettant de dé-sérialiser une vue
     */
    private final LayoutInflater inflater;

    public AdaptateurListeItineraire(@NonNull Context contexte,
                                     int resource,
                                     @NonNull List<Itineraire> objects) {
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
        TextView titre = convertView.findViewById(R.id.titre);
        TextView sousTitre = convertView.findViewById(R.id.sous_titre);
        ImageButton boutonSuppression = convertView.findViewById(R.id.supprimer);
        final Itineraire infosItineraire = getItem(position);
        assert infosItineraire != null;
        titre.setText(infosItineraire.getNom());
        String texteKm = getContext().getString(R.string.affichage_nombre_km);
        sousTitre.setText(String.format(texteKm, infosItineraire.getKilometres()));
        boutonSuppression.setOnClickListener(v -> onClickBtnSuppression(infosItineraire, position));
        convertView.setOnClickListener(v -> {
            ListView listView = (ListView) parent;
            listView.performItemClick(v, position, listView.getItemIdAtPosition(position));
        });

        return convertView;
    }

    private void onClickBtnSuppression(Itineraire itineraire, int position) {
        String message = getContext().getString(R.string.confirmation_suppression_itineraire, itineraire.getNom());
        new AlertDialog.Builder(getContext())
                .setTitle(R.string.suppression_itineraire)
                .setMessage(message)
                .setPositiveButton(R.string.oui, (dialog, which) -> deleteItineraire(itineraire))
                .setNegativeButton(R.string.non, (dialog, which) -> dialog.dismiss())
                .show();
    }

    private void deleteItineraire(Itineraire itineraire) {
        ApiRequest.deleteItineraire(getContext(), itineraire.getId(), new ApiRequest.ApiResponseCallback<JSONObject>() {
            @Override
            public void onSuccess(JSONObject response) {
                remove(itineraire);
                notifyDataSetChanged();
                Toast.makeText(getContext(), R.string.itineraire_deleted, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(VolleyError error) {
                System.out.println(error);
                Toast.makeText(getContext(), R.string.error_deleting_itineraire, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
