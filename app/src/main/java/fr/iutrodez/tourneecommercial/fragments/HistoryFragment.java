package fr.iutrodez.tourneecommercial.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;
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
 */
public class HistoryFragment extends Fragment {

    public MainActivity parent;
    private HistoryListAdapter historyListAdapter;
    private FullscreenFetchStatusDisplay status;
    private ListView list;
    private boolean isLoading = false;
    private int currentPage = 0;
    private int totalPages = 0;
    private final List<ParcoursReducedDTO> parcours = new ArrayList<>();
    public static final ApiRequest API_REQUEST = ApiRequest.getInstance();

    /**
     * Appelé lorsque le fragment est attaché à son contexte.
     *
     * @param context Le contexte auquel le fragment est attaché.
     */
    @Override
    public void onAttach(@NotNull Context context) {
        super.onAttach(context);
        parent = (MainActivity) context;
    }

    /**
     * Crée et retourne la vue hiérarchique associée au fragment.
     *
     * @param inflater           L'objet LayoutInflater qui peut être utilisé pour gonfler les vues dans le fragment.
     * @param container          Si non-null, c'est le parent auquel la vue du fragment est attachée.
     * @param savedInstanceState Si non-null, ce fragment est reconstruit à partir d'un état précédemment sauvegardé.
     * @return La vue hiérarchique associée au fragment.
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View frag = inflater.inflate(R.layout.list_of_history_fragment, container, false);
        list = frag.findViewById(R.id.listView_history);

        status = frag.findViewById(R.id.fetchStatus_status);
        status.setShowContentFunction(() -> setContentVisibility(View.VISIBLE));
        status.setHideContentFunction(() -> setContentVisibility(View.GONE));
        fetchNumberOfParcoursPage();
        fetchParcoursofNextPage();

        setupList(list);

        return frag;
    }

    /**
     * Appelé pour faire la première création du fragment.
     *
     * @param savedInstanceState Si non-null, ce fragment est reconstruit à partir d'un état précédemment sauvegardé.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /**
     * Met à jour la visibilité de l'ensemble des éléments de contenu du fragment
     *
     * @param visibility un entier parmi {@code View.GONE}, {@code View.VISIBLE}, ou {@code View.INVISIBLE}
     */
    public void setContentVisibility(int visibility) {
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
                    isLoading = false;
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
        System.out.println("CLICK " + parcours.get(position).getNom());
        Bundle bundle = new Bundle();

        bundle.putString("id", historyListAdapter.getItem(position).getId());
        parent.navigateToFragment(MainActivity.COURSE_VIEW_FRAGMENT,false,bundle);

    }

    /**
     * Gère le clic sur le bouton de suppression d'un parcours.
     *
     * @param parcoursReducedDTO L'objet ParcoursReducedDTO correspondant à l'élément à supprimer.
     * @param integer            Un entier associé à l'élément à supprimer.
     */
    private void onDeleteButtonClick(ParcoursReducedDTO parcoursReducedDTO, Integer integer) {
        System.out.println("DELETE " + parcoursReducedDTO.getNom());
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
            System.out.println("size : " + historyListAdapter.getCount());
            currentPage++;
            status.hide();
        }, error -> status.error(R.string.fetch_itinerary_error));
    }

}
