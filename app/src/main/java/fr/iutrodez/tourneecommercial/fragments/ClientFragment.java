package fr.iutrodez.tourneecommercial.fragments;

import static fr.iutrodez.tourneecommercial.utils.WidgetHelpers.disableView;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import fr.iutrodez.tourneecommercial.MainActivity;
import fr.iutrodez.tourneecommercial.R;
import fr.iutrodez.tourneecommercial.modeles.Client;
import fr.iutrodez.tourneecommercial.utils.ClientListAdapter;
import fr.iutrodez.tourneecommercial.utils.api.ApiRequest;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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

    private static final ApiRequest API_REQUEST = ApiRequest.getInstance();

    public static ClientFragment newInstance() {
        return new ClientFragment();
    }

    public MainActivity parent;
    private ClientListAdapter clientListAdapter;

    private Button add;

    private boolean isLoading = false;
    private int currentPage = 0;
    private int numberOfPages = 0;
    private final List<Client> clients = new ArrayList<>();

    @Override
    public void onAttach(@NotNull Context context) {
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
        View frag = inflater.inflate(R.layout.list_of_client_fragment, container, false);
        ListView list = frag.findViewById(R.id.listitem_client);
        add = frag.findViewById(R.id.button_add);
        add.setOnClickListener(this::add);

        // Initialize adapter
        clientListAdapter = new ClientListAdapter(
                this.parent,
                R.layout.list_of_client_items,
                clients, this::modify, this::delete);
        list.setAdapter(clientListAdapter);

        // Initial data loading
        fetchNumberOfClients();
        fetchClientsPage();

        // Scroll listener for pagination
        list.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (!isLoading && (firstVisibleItem + visibleItemCount >= totalItemCount) && currentPage < numberOfPages && totalItemCount > 0) {
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
    public void modify(Client client) {
        Bundle bundle = new Bundle();
        bundle.putString("id", client.get_id());
        parent.navigateToFragment(MainActivity.CLIENT_CREATION_FRAGMENT, false, bundle);

    }

    /**
     * Callback lorsque le bouton supprimer de l'adaptateur est cliqué
     *
     * @param client le client appuyé
     */
    public void delete(Client client) {
        String message = requireContext().getString(R.string.confirm_delete_client, client.getNomEntreprise());
        new AlertDialog.Builder(getContext())
                .setTitle(R.string.delete_route)
                .setMessage(message)
                .setPositiveButton(R.string.yes, (dialog, which) -> deleteClient(client))
                .setNegativeButton(R.string.no, (dialog, which) -> dialog.dismiss())
                .show();
    }

    private void deleteClient(Client client) {
        API_REQUEST.client.delete(getContext(), client.get_id(), response -> {
            clients.remove(client);
            clientListAdapter.notifyDataSetChanged();
            Toast.makeText(getContext(), R.string.client_deleted_success, Toast.LENGTH_SHORT).show();
        }, error -> Toast.makeText(getContext(), R.string.client_deletion_error, Toast.LENGTH_SHORT).show());
    }

    private void fetchNumberOfClients() {
        API_REQUEST.client.getNumberOfPages(requireContext(),
                numberOfPages -> this.numberOfPages = numberOfPages,
                error -> onFetchFail(R.string.fetch_clients_count_error));
    }

    private void fetchClientsPage() {
        isLoading = true;
        API_REQUEST.client.getPage(requireContext(), currentPage, clients -> {
            this.clients.addAll(clients);
            clientListAdapter.notifyDataSetChanged();
            currentPage++;
        }, error -> onFetchFail(R.string.fetch_client_error));
        isLoading = false;
    }

    private void onFetchFail(int messageId) {
        disableView(add);
        Toast.makeText(parent, messageId, Toast.LENGTH_SHORT).show();
    }

    /**
     * Méthode appelée quand le
     *
     * @param view La vue qui a été cliquée.
     */
    public void add(View view) {
        parent.navigateToFragment(MainActivity.CLIENT_CREATION_FRAGMENT, false);
    }
}
