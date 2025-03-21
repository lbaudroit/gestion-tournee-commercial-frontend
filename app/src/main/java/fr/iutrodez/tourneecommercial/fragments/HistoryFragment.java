package fr.iutrodez.tourneecommercial.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import fr.iutrodez.tourneecommercial.MainActivity;
import fr.iutrodez.tourneecommercial.R;
import fr.iutrodez.tourneecommercial.model.dto.ParcoursReducedDTO;
import fr.iutrodez.tourneecommercial.utils.FullscreenFetchStatusDisplay;
import fr.iutrodez.tourneecommercial.utils.adapter.HistoryListAdapter;
import fr.iutrodez.tourneecommercial.utils.api.ApiRequest;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static fr.iutrodez.tourneecommercial.utils.helper.ViewHelper.setVisibilityFor;

/**
 * Fragment affichant l'historique des parcours.
 *
 * @author Benjamin NICOL, Enzo CLUZEL, Ahmed BRIBACH, Leïla BAUDROIT
 */
public class HistoryFragment extends Fragment {

    public static final ApiRequest API_REQUEST = ApiRequest.getInstance();
    private final List<ParcoursReducedDTO> parcours = new ArrayList<>();
    public MainActivity parent;
    private HistoryListAdapter historyListAdapter;
    private FullscreenFetchStatusDisplay status;
    private TextView noEntry;
    private ListView list;
    private boolean isLoading = false;
    private int currentPage = 0;
    private int totalPages = 0;

    @Override
    public void onAttach(@NotNull Context context) {
        super.onAttach(context);
        parent = (MainActivity) context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View frag = inflater.inflate(R.layout.list_of_history_fragment, container, false);
        list = frag.findViewById(R.id.listView_history);
        noEntry = frag.findViewById(R.id.no_entries_text);

        status = frag.findViewById(R.id.fetchStatus_status);
        status.setShowContentFunction(() -> setContentVisibility(View.VISIBLE));
        status.setHideContentFunction(() -> setContentVisibility(View.GONE));
        fetchNumberOfParcoursPage();
        fetchParcoursofNextPage();

        setupList(list);

        return frag;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /**
     * Met à jour la visibilité de l'ensemble des éléments de contenu du fragment
     *
     * @param visibility un entier parmi {@code View.GONE}, {@code View.VISIBLE}, ou {@code View.INVISIBLE}
     */
    private void setContentVisibility(int visibility) {
        setVisibilityFor(visibility, list);
    }

    /**
     * Configure la liste des parcours.
     *
     * @param list La ListView à configurer.
     */
    private void setupList(ListView list) {
        historyListAdapter = new HistoryListAdapter(
                parent,
                R.layout.list_of_history_items,
                parcours,
                this::onDeleteButtonClick
        );

        list.setAdapter(historyListAdapter);

        list.setOnItemClickListener((parent, view, position, id) -> {
            onclickList(position); // Passer la position à onclickList()
        });

        list.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (!isLoading && (firstVisibleItem + visibleItemCount >= totalItemCount) && currentPage < totalPages && totalItemCount > 0) {
                    isLoading = true;
                    fetchParcoursofNextPage();
                }
            }
        });
    }

    /**
     * Gère le clic sur un élément de la liste.
     *
     * @param position La position de l'élément cliqué dans la liste.
     */
    private void onclickList(int position) {
        Bundle bundle = new Bundle();

        bundle.putString("id", historyListAdapter.getItem(position).getId());
        parent.navigateToFragment(MainActivity.COURSE_VIEW_FRAGMENT, false, bundle);

    }

    /**
     * Gère le clic sur le bouton de suppression d'un parcours.
     *
     * @param parcoursReducedDTO L'objet ParcoursReducedDTO correspondant à l'élément à supprimer.
     * @param integer            Un entier associé à l'élément à supprimer.
     */
    private void onDeleteButtonClick(ParcoursReducedDTO parcoursReducedDTO, Integer integer) {
        String message = parent.getString(R.string.confirm_delete_parcours, parcoursReducedDTO.getNom());

        new AlertDialog.Builder(parent)
                .setTitle(R.string.delete_parcours)
                .setMessage(message)
                .setPositiveButton(R.string.yes, (dialog, which) -> delete(parcoursReducedDTO))
                .setNegativeButton(R.string.no, (dialog, which) -> dialog.dismiss())
                .show();
    }

    /**
     * Fais appel à la suppression niveau de l'API
     */
    private void delete(ParcoursReducedDTO parcoursReducedDTO) {
        API_REQUEST.parcours.delete(requireContext(), parcoursReducedDTO.getId(),
                response -> {
                    parcours.remove(parcoursReducedDTO);
                    historyListAdapter.notifyDataSetChanged();
                    if (historyListAdapter.isEmpty()) {
                        noEntry.setVisibility(View.VISIBLE);
                    }
                    Toast.makeText(parent, R.string.parcours_delete_success, Toast.LENGTH_SHORT).show();
                },
                error -> status.error(R.string.parcours_deletion_error));
    }

    /**
     * Récupère le nombre de pages d'itinéraires depuis l'API.
     */
    private void fetchNumberOfParcoursPage() {
        status.loading();
        API_REQUEST.parcours.getNumberOfPages(requireContext(),
                response -> {
                    totalPages = response;
                    status.hide();
                },
                error -> status.error(R.string.fetch_itinerary_error));
    }

    /**
     * Récupère la page suivante d'itinéraires depuis l'API.
     */
    private void fetchParcoursofNextPage() {
        status.loading();

        API_REQUEST.parcours.getPage(parent, currentPage, response -> {
            list.setVisibility(View.VISIBLE);
            status.hide();
            parcours.addAll(response);
            historyListAdapter.notifyDataSetChanged();

            if (historyListAdapter.isEmpty()) {
                noEntry.setVisibility(View.VISIBLE);
            }
            currentPage++;
            isLoading = false;
            status.hide();
        }, error -> status.error(R.string.fetch_itinerary_error));
    }

}
