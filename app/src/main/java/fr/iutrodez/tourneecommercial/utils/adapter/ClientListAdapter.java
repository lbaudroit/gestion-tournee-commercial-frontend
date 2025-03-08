package fr.iutrodez.tourneecommercial.utils.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import fr.iutrodez.tourneecommercial.R;
import fr.iutrodez.tourneecommercial.model.Adresse;
import fr.iutrodez.tourneecommercial.model.Client;

import java.util.ArrayList;
import java.util.List;

/**
 * Adapter permettant de gérer l’affichage des clients dans une ListView
 */
public class ClientListAdapter extends ArrayAdapter<Client> {

    /**
     * Interface permettant de définir une action à effectuer lorsqu’un client est modifié
     */
    public interface OnClickModifyCallback {

        void onClickModifyClient(Client client);
    }

    /**
     * Interface permettant de définir une action à effectuer lorsqu’un client est supprimé
     */
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

    /**
     * Constructeur de l'adaptateur de la liste des clients.
     *
     * @param context       Le contexte de l'application.
     * @param resource      L'identifiant de la vue pour chaque élément de la liste.
     * @param objects       La liste des objets Client à afficher.
     * @param onClickModify Le callback pour la modification d'un client.
     * @param onClickDelete Le callback pour la suppression d'un client.
     */
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

    /**
     * Retourne le nombre d'éléments dans la liste filtrée.
     *
     * @return Le nombre d'éléments dans la liste filtrée.
     */
    @Override
    public int getCount() {
        return filteredClients.size();
    }

    /**
     * Retourne l'objet Client à la position spécifiée.
     *
     * @param position La position de l'élément dans la liste.
     * @return L'objet Client à la position spécifiée.
     */
    @Nullable
    @Override
    public Client getItem(int position) {
        return filteredClients.get(position);
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

    /**
     * Retourne le filtre pour la recherche de clients.
     *
     * @return Le filtre pour la recherche de clients.
     */
    @NonNull
    @Override
    public Filter getFilter() {
        if (filteredClient == null) {
            filteredClient = new filteredClient();
        }
        return filteredClient;
    }

    /**
     * Classe interne pour filtrer les clients.
     */
    private class filteredClient extends Filter {

        /**
         * Effectue le filtrage des clients en fonction de la contrainte.
         *
         * @param constraint La contrainte de filtrage.
         * @return Les résultats du filtrage.
         */
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

        /**
         * Publie les résultats du filtrage et met à jour la liste affichée.
         *
         * @param constraint La contrainte de filtrage.
         * @param results    Les résultats du filtrage.
         */
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            filteredClients.clear();
            filteredClients.addAll((List) results.values);
            notifyDataSetChanged();
        }
    }

}
