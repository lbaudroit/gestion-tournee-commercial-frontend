package fr.iutrodez.tourneecommercial.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import fr.iutrodez.tourneecommercial.MainActivity;
import fr.iutrodez.tourneecommercial.R;
import fr.iutrodez.tourneecommercial.modeles.Client;
import fr.iutrodez.tourneecommercial.utils.AdaptateurListeClients;
import fr.iutrodez.tourneecommercial.utils.Deprecated_ApiRequest;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Fragment de la navBar pour afficher la liste des clients
 * et pour soit modifier, créer ou supprimer un client
 *
 * @author Ahmed BRIBACH
 * Leila Baudroit
 * Enzo CLUZEL
 * Benjamin NICOL
 */
public class ClientFragment extends Fragment {

    public static ClientFragment newInstance() {
        return new ClientFragment();
    }

    public MainActivity parent;
    private ListView liste;
    private AdaptateurListeClients adaptateur;

    private boolean isLoading = false;
    private int currentPage = 0;
    private int totalPages = 0;
    private List<Client> clients = new ArrayList<>();

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof MainActivity) {
            parent = (MainActivity) context;
        } else {
            throw new ClassCastException("Le contexte doit être une instance d'ActivitePrincipale.");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View frag = inflater.inflate(R.layout.fragment_liste_client, container, false);
        liste = frag.findViewById(R.id.listitem_client);
        frag.findViewById(R.id.button_add).setOnClickListener(this::ajouter);

        // Initialize adapter
        adaptateur = new AdaptateurListeClients(
                this.parent,
                R.layout.listitem_client,
                clients, this::onClickSupprimerAdaptateur, this::onClickModifierAdaptateur);
        liste.setAdapter(adaptateur);

        // Initial data loading
        fetchClientsPage();
        fetchNombreClients();

        // Scroll listener for pagination
        liste.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (!isLoading && (firstVisibleItem + visibleItemCount >= totalItemCount) && currentPage < totalPages && totalItemCount > 0) {
                    isLoading = true;
                    fetchClientsPage();
                    isLoading = false;
                }
            }
        });
        return frag;
    }

    /**
     * Callback lorsque le bouton modifier de l'adaptateur est cliqué
     *
     * @param client le client appuyé
     */
    public void onClickModifierAdaptateur(Client client) {
        Bundle bundle = new Bundle();
        bundle.putString("id", client.get_id());
        parent.navigateToFragment(MainActivity.CLIENT_CREATION_FRAGMENT, false, bundle);

        Toast.makeText(getContext(), "Modifier : " + client.getNomEntreprise(), Toast.LENGTH_SHORT).show();

    }

    /**
     * Callback lorsque le bouton supprimer de l'adaptateur est cliqué
     *
     * @param client le client appuyé
     */
    public void onClickSupprimerAdaptateur(Client client) {
        String message = getContext().getString(R.string.confirmation_suppression_client, client.getNomEntreprise());
        new AlertDialog.Builder(getContext())
                .setTitle(R.string.suppression_itineraire)
                .setMessage(message)
                .setPositiveButton(R.string.oui, (dialog, which) -> deleteClient(client))
                .setNegativeButton(R.string.non, (dialog, which) -> dialog.dismiss())
                .show();
    }

    private void deleteClient(Client client) {
        Deprecated_ApiRequest.removeClient(getContext(), client.get_id(), new Deprecated_ApiRequest.ApiResponseCallback<JSONObject>() {
            @Override
            public void onSuccess(JSONObject response) {
                adaptateur.remove(client);
                adaptateur.notifyDataSetChanged();
                Toast.makeText(getContext(), R.string.client_deleted, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(VolleyError error) {
                Toast.makeText(getContext(), R.string.error_deleting_client, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchNombreClients() {
        Deprecated_ApiRequest.getNombreClient(requireContext(), new Deprecated_ApiRequest.ApiResponseCallback<JSONObject>() {
            @Override
            public void onSuccess(JSONObject response) {
                try {
                    totalPages = response.getInt("nombre");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(VolleyError error) {
                Toast.makeText(parent, R.string.error_get_nb_client,
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchClientsPage() {
        isLoading = true;
        Deprecated_ApiRequest.getClientsBy30(requireContext(), currentPage, new Deprecated_ApiRequest.ApiResponseCallback<JSONArray>() {
            @Override
            public void onSuccess(JSONArray response) {
                try {
                    int len = response.length();
                    List<Client> newClients = new ArrayList<>();
                    for (int i = 0; i < len; i++) {
                        Gson gson = new Gson();
                        Client client = gson.fromJson(response.getJSONObject(i).toString(), Client.class);
                        newClients.add(client);
                    }

                    clients.addAll(newClients);

                    // Notifier l'adaptateur
                    adaptateur.notifyDataSetChanged();
                    currentPage++;
                } catch (JSONException e) {
                    e.printStackTrace();
                } finally {
                    isLoading = false;
                }
            }

            @Override
            public void onError(VolleyError error) {
                Toast.makeText(parent, R.string.error_get_client,
                        Toast.LENGTH_SHORT).show();
                isLoading = false;
            }
        });
    }

    /**
     * Méthode appelée quand le
     *
     * @param view
     */
    public void ajouter(View view) {
        parent.navigateToFragment(MainActivity.CLIENT_CREATION_FRAGMENT, false);
    }
}
