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
import fr.iutrodez.tourneecommercial.utils.ItineraryListAdapter;
import fr.iutrodez.tourneecommercial.utils.api.ApiRequest;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Fragment pour afficher et gérer la liste des itinéraires.
 */
public class ItineraryFragment extends Fragment {
    private ItineraryListAdapter itineraryListAdapter;
    private boolean isLoading = false;
    private int currentPage = 0;
    private int totalPages = 0;
    private final List<Itineraire> itineraries = new ArrayList<>();

    public static final ApiRequest API_REQUEST = ApiRequest.getInstance();
    public MainActivity parent;

    /**
     * Crée une nouvelle instance de ItineraryFragment.
     *
     * @return une nouvelle instance de ItineraryFragment
     */
    public static ItineraryFragment newInstance() {
        return new ItineraryFragment();
    }

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
        ListView list = frag.findViewById(R.id.listView_itinerary);
        Button add = frag.findViewById(R.id.button_add);

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
        API_REQUEST.itineraire.getNumberOfPages(requireContext(), response -> totalPages = response,
                error -> Toast.makeText(getContext(), R.string.fetch_itinerary_error, Toast.LENGTH_SHORT).show());
    }

    /**
     * Récupère la page suivante d'itinéraires depuis l'API.
     */
    private void fetchItinerariesNextpage() {
        API_REQUEST.itineraire.getPage(parent, currentPage, response -> {
            itineraries.addAll(response);
            itineraryListAdapter.notifyDataSetChanged();
            currentPage++;
        }, error -> Toast.makeText(getContext(), R.string.fetch_itinerary_error, Toast.LENGTH_SHORT).show());
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
}
