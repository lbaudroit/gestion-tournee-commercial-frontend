package fr.iutrodez.tourneecommercial.fragments;

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

import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import fr.iutrodez.tourneecommercial.ActivitePrincipale;
import fr.iutrodez.tourneecommercial.R;
import fr.iutrodez.tourneecommercial.modeles.Itineraire;
import fr.iutrodez.tourneecommercial.utils.AdaptateurListeItineraire;
import fr.iutrodez.tourneecommercial.utils.ApiRequest;

public class FragmentItineraires extends Fragment {

    public static FragmentItineraires newInstance() {
        return new FragmentItineraires();
    }

    public ActivitePrincipale parent;

    private ListView liste;
    private Button ajouter;
    private AdaptateurListeItineraire adaptateur;

    private boolean isLoading = false;
    private int currentPage = 0;
    private int totalPages = 0;
    private List<Itineraire> itineraires = new ArrayList<>();

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        parent = (ActivitePrincipale) context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View frag = inflater.inflate(R.layout.fragment_liste_itineraires, container, false);
        liste = frag.findViewById(R.id.list_itineraires);
        ajouter = frag.findViewById(R.id.ajouter);
        //remplissage de itineraires selon l'API
        fetcheItinerairesPage();
        fetchNombreItineraires();

        // On utilise un adaptateur custom pour gérer les éléments de liste avec leurs boutons
        adaptateur = new AdaptateurListeItineraire(
                this.parent,
                R.layout.listitem_itineraire,
                itineraires);
        liste.setAdapter(adaptateur);
        ajouter.setOnClickListener(this::onClickAjouter);
        liste.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (!isLoading && (firstVisibleItem + visibleItemCount >= totalItemCount) && currentPage < totalPages && totalItemCount > 0) {
                    isLoading = true;
                    fetcheItinerairesPage();
                    isLoading = false;
                }
            }
        });
        return frag;
    }


    private void onClickAjouter(View view) {
        // TODO add itineraire
        Toast.makeText(this.parent, R.string.todo, Toast.LENGTH_SHORT).show();
    }

    private void fetchNombreItineraires() {
        ApiRequest.fetchNombresItineraires(parent, "itineraire/nombre/", new ApiRequest.ApiResponseCallback<JSONObject>() {
            @Override
            public void onSuccess(JSONObject response) {
                // Parse the response and update the itineraires list
                try {
                    totalPages = response.getInt("nombre");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                System.out.println("Total pages: " + totalPages);
                List<Itineraire> fetchedItineraires = parseItineraires(response);
                itineraires.addAll(fetchedItineraires);
                adaptateur.notifyDataSetChanged();
            }

            @Override
            public void onError(VolleyError error) {
                // Handle error
                Toast.makeText(parent, "Failed to fetch itineraries", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetcheItinerairesPage() {
        ApiRequest.fetchItineraires(parent, "itineraire/lazy/?page=" + currentPage, new ApiRequest.ApiResponseCallback<JSONArray>() {
            @Override
            public void onSuccess(JSONArray response) {
                // Parse the response and update the itineraires list
                int len = response.length();
                for (int i = 0; i < len; i++) {
                    try {
                        JSONObject itineraire = response.getJSONObject(i);
                        System.out.println(itineraire.toString());
                        String nom = itineraire.getString("nom");
                        int duree = itineraire.getInt("distance");
                        long id = itineraire.getLong("id");
                        itineraires.add(new Itineraire(nom, duree, id));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                adaptateur.notifyDataSetChanged();
            }

            @Override
            public void onError(VolleyError error) {
                // Handle error
                Toast.makeText(parent, "Failed to fetch itineraries", Toast.LENGTH_SHORT).show();
            }
        });
        currentPage++;
    }

    private List<Itineraire> parseItineraires(JSONObject response) {
        // Parse the JSON response and return a list of Itineraire objects
        List<Itineraire> fetchedItineraires = new ArrayList<>();
        // Add parsing logic here
        return fetchedItineraires;
    }
}
