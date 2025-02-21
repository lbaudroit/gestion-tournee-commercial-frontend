package fr.iutrodez.tourneecommercial.fragments;

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
import fr.iutrodez.tourneecommercial.modeles.Itineraire;
import fr.iutrodez.tourneecommercial.utils.FullscreenFetchStatusDisplay;
import fr.iutrodez.tourneecommercial.utils.adapter.ItineraryListAdapter;
import fr.iutrodez.tourneecommercial.utils.api.ApiRequest;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static fr.iutrodez.tourneecommercial.utils.helper.ViewHelper.setVisibilityFor;

/**
 * Fragment pour afficher et gérer la liste des itinéraires.
 */
public class ItineraryFragment extends Fragment {
    private ItineraryListAdapter itineraryListAdapter;
    private Button add;

    public MainActivity parent;
    private ListView list;

    private FullscreenFetchStatusDisplay status;
    private boolean isLoading = false;
    private int currentPage = 0;
    private int totalPages = 0;
    private final List<Itineraire> itineraries = new ArrayList<>();
    public static final ApiRequest API_REQUEST = ApiRequest.getInstance();

    @Override
    public void onAttach(@NotNull Context context) {
        super.onAttach(context);
        parent = (MainActivity) context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View frag = inflater.inflate(R.layout.list_of_itinerary_fragment, container, false);
        list = frag.findViewById(R.id.listView_itinerary);
        add = frag.findViewById(R.id.button_add);

        status = frag.findViewById(R.id.fetchStatus_status);
        status.setShowContentFunction(() -> setContentVisibility(View.VISIBLE));
        status.setHideContentFunction(() -> setContentVisibility(View.GONE));

        fetchNumberOfItinerarypages();
        fetchItinerariesNextpage();

        setupList(list, add);
        return frag;
    }


    /**
     * Gère le clic sur le bouton d'ajout d'un nouvel itinéraire.
     *
     * @param view la vue qui a été cliquée
     */
    private void onClickAdd(View view) {
        parent.navigateToFragment(MainActivity.ITINERARY_CREATION_FRAGMENT, false);
    }

    /**
     * Récupère le nombre de pages d'itinéraires depuis l'API.
     */
    private void fetchNumberOfItinerarypages() {
        status.loading();


        API_REQUEST.itineraire.getNumberOfPages(requireContext(),
                response -> {
                    totalPages = response;
                    status.hide();
                },
                error -> status.error(R.string.fetch_itinerary_error));
    }

    /**
     * Récupère la page suivante d'itinéraires depuis l'API.
     */
    private void fetchItinerariesNextpage() {
        status.loading();

        API_REQUEST.itineraire.getPage(parent, currentPage, response -> {
            itineraries.addAll(response);
            itineraryListAdapter.notifyDataSetChanged();
            currentPage++;

            status.hide();
        }, error -> status.error(R.string.fetch_itinerary_error));
    }

    /**
     * Gère le clic sur le bouton de suppression d'un itinéraire.
     *
     * @param itinerary l'itinéraire à supprimer
     * @param position  la position de l'itinéraire dans la liste
     */
    private void onClickDelete(Itineraire itinerary, int position) {
        String message = parent.getString(R.string.confirm_delete_route, itinerary.getNom());
        new AlertDialog.Builder(getContext())
                .setTitle(R.string.delete_route)
                .setMessage(message)
                .setPositiveButton(R.string.yes, (dialog, which) -> deleteItinerary(itinerary))
                .setNegativeButton(R.string.no, (dialog, which) -> dialog.dismiss())
                .show();
    }

    /**
     * Supprime l'itinéraire spécifié via l'API.
     *
     * @param itinerary l'itinéraire à supprimer
     */
    private void deleteItinerary(Itineraire itinerary) {
        API_REQUEST.itineraire.delete(parent, itinerary.getId(), response -> {
            itineraries.remove(itinerary);
            itineraryListAdapter.notifyDataSetChanged();
            Toast.makeText(getContext(), R.string.itinerary_deleted_success, Toast.LENGTH_SHORT).show();
        }, error -> Toast.makeText(getContext(), R.string.itinerary_deletion_error, Toast.LENGTH_SHORT).show());
    }

    private void onclickList(int position) {
        Itineraire itineraire = itineraries.get(position); // Récupérer l'itinéraire cliqué
        if (parent.isMapUsed()) {
            new AlertDialog.Builder(getContext())
                    .setTitle(getString(R.string.launch_route))
                    .setMessage(getString(R.string.unable_to_launch_route))
                    .setNegativeButton(R.string.ok, (dialog, which) -> dialog.dismiss())
                    .show();

        } else {
            new AlertDialog.Builder(getContext())
                    .setTitle(getString(R.string.launch_route))
                    .setMessage(getString(R.string.confirm_add_route, itineraire.getNom()))
                    .setPositiveButton(R.string.yes, (dialog, which) -> itineraryToMap(itineraire)) // Passer l'itinéraire
                    .setNegativeButton(R.string.no, (dialog, which) -> dialog.dismiss())
                    .show();
        }

    }


    private void itineraryToMap(Itineraire itineraire) {
        parent.markMapAs(true);
        Bundle bundle = new Bundle();

        bundle.putLong("id", itineraire.getId()); // Correction du type (getId() est un long)
        bundle.putString("name", itineraire.getNom()); // Correction du type (getId() est un long)

        parent.navigateToNavbarItem(MainActivity.MAP_FRAGMENT, false, bundle);
    }

    /**
     * Gère le clic sur le bouton de modification d'un itinéraire.
     *
     * @param itinerary l'itinéraire à modifier
     * @param position  la position de l'itinéraire dans la liste
     */
    private void onClickModify(Itineraire itinerary, int position) {
        Bundle bundle = new Bundle();
        bundle.putLong("idItineraire", itinerary.getId());
        System.out.println("Navigating to FragmentCreationItineraire with id: " + itinerary.getId());

        parent.navigateToFragment(MainActivity.ITINERARY_CREATION_FRAGMENT, false, bundle);
    }

    /**
     * Configure la ListView et le bouton d'ajout.
     *
     * @param list la ListView à configurer
     * @param add  le Button à configurer
     */
    private void setupList(ListView list, Button add) {
        itineraryListAdapter = new ItineraryListAdapter(
                parent,
                R.layout.list_of_itinerary_items,
                itineraries,
                this::onClickModify,
                this::onClickDelete);
        list.setAdapter(itineraryListAdapter);

        list.setOnItemClickListener((parent, view, position, id) -> {
            onclickList(position); // Passer la position à onclickList()
        });

        add.setOnClickListener(this::onClickAdd);
        list.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (!isLoading && (firstVisibleItem + visibleItemCount >= totalItemCount) && currentPage < totalPages && totalItemCount > 0) {
                    isLoading = true;
                    fetchItinerariesNextpage();
                    isLoading = false;
                }
            }
        });
    }

    /**
     * Met à jour la visibilité de l'ensemble des éléments de contenu du fragment
     *
     * @param visibility un entier parmi {@code View.GONE}, {@code View.VISIBLE}, ou {@code View.INVISIBLE}
     */
    public void setContentVisibility(int visibility) {
        setVisibilityFor(visibility, add, list);
    }
}
