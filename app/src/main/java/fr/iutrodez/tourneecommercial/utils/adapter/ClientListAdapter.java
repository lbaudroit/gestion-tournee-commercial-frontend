package fr.iutrodez.tourneecommercial.utils.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import fr.iutrodez.tourneecommercial.R;
import fr.iutrodez.tourneecommercial.modeles.Adresse;
import fr.iutrodez.tourneecommercial.modeles.Client;

import java.util.ArrayList;
import java.util.List;

public class ClientListAdapter extends ArrayAdapter<Client> {

    public interface OnClickModifyCallback {

        void onClickModifyClient(Client client);
    }

    public interface OnClickSupprimerCallback {

        void onClickDeleteClient(Client client);
    }

    /**
     * Identifiant de la vue permettant d’afficher chaque item de la liste
     */
    private final int viewIdentifier;

    /**
     * Objet utilitaire permettant de dé-sérialiser une vue
     */
    private final LayoutInflater inflater;

    private final OnClickModifyCallback onClickModify;
    private final OnClickSupprimerCallback onClickDelete;

    private final List<Client> originalClients;
    private final List<Client> filteredClients;
    private ClientListAdapter.filteredClient filteredClient;

    public ClientListAdapter(@NonNull Context context,
                             int resource,
                             @NonNull List<Client> objects,
                             OnClickModifyCallback onClickModify,
                             OnClickSupprimerCallback onClickDelete) {
        super(context, resource, objects);
        this.viewIdentifier = resource;
        this.originalClients = new ArrayList<>(objects);
        this.filteredClients = objects;
        inflater = (LayoutInflater) getContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.onClickModify = onClickModify;
        this.onClickDelete = onClickDelete;
    }

    @Override
    public int getCount() {
        return filteredClients.size();
    }

    @Nullable
    @Override
    public Client getItem(int position) {
        return filteredClients.get(position);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        // Opérations de récupération de la vue
        if (convertView == null) {
            convertView = inflater.inflate(viewIdentifier, parent, false);
        }

        // Récupération des vues à l’intérieur de l’élément de la liste
        TextView title = convertView.findViewById(R.id.textView_title);
        TextView subTitle = convertView.findViewById(R.id.textView_subTitle);
        ImageButton delete = convertView.findViewById(R.id.imageButton_delete);
        Button modify = convertView.findViewById(R.id.button_modify);

        // Récupération de l’objet Client correspondant à cette position
        final Client clientInfos = getItem(position);

        // Définition du texte des TextViews
        assert clientInfos != null;
        Adresse adress = clientInfos.getAdresse();
        title.setText(clientInfos.getNomEntreprise());
        subTitle.setText(String.format("%s %s", adress.getCodePostal(), adress.getVille()));

        // Ajoute le listener de suppression s'il existe, sinon cache le bouton
        if (onClickDelete != null) {
            delete.setOnClickListener((View v) -> onClickDelete.onClickDeleteClient(clientInfos));
            delete.setVisibility(View.VISIBLE);
        } else {
            delete.setVisibility(View.GONE);
        }

        // Ajoute le listener de modification s'il existe, sinon cache le bouton
        if (onClickModify != null) {
            modify.setOnClickListener((View v) -> onClickModify.onClickModifyClient(clientInfos));
            modify.setVisibility(View.VISIBLE);
        } else {
            modify.setVisibility(View.GONE);
        }

        // Gestion du clic sur l’ensemble de l’élément de la liste
        convertView.setOnClickListener(v -> {
            // Obtenez la ListView parente à partir de la vue fournie par le convertView
            ListView listView = (ListView) parent;

            // Simuler un clic sur l’élément de la liste à la position donnée
            listView.performItemClick(v, position, listView.getItemIdAtPosition(position));
        });

        return convertView;
    }

    @NonNull
    @Override
    public Filter getFilter() {
        if (filteredClient == null) {
            filteredClient = new filteredClient();
        }
        return filteredClient;
    }

    private class filteredClient extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();
            List<Client> filteredList = new ArrayList<>();

            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(originalClients);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();

                for (Client client : originalClients) {
                    if (client.getNomEntreprise().toLowerCase().contains(filterPattern)) {
                        filteredList.add(client);
                    }
                    if (client.getAdresse().getVille().toLowerCase().contains(filterPattern)) {
                        filteredList.add(client);
                    }
                    if (client.getAdresse().getCodePostal().toLowerCase().contains(filterPattern)) {
                        filteredList.add(client);
                    }
                }
            }

            results.values = filteredList;
            results.count = filteredList.size();
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            filteredClients.clear();
            filteredClients.addAll((List) results.values);
            notifyDataSetChanged();
        }
    }

}
