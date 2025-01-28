package fr.iutrodez.tourneecommercial.fragments;

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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import fr.iutrodez.tourneecommercial.ActivitePrincipale;
import fr.iutrodez.tourneecommercial.R;
import fr.iutrodez.tourneecommercial.modeles.Adresse;
import fr.iutrodez.tourneecommercial.modeles.Client;
import fr.iutrodez.tourneecommercial.modeles.Contact;
import fr.iutrodez.tourneecommercial.modeles.Itineraire;
import fr.iutrodez.tourneecommercial.utils.AdaptateurListeClients;
import fr.iutrodez.tourneecommercial.utils.ApiRequest;

public class FragmentClients extends Fragment {

    public static FragmentClients newInstance() {
        return new FragmentClients();
    }

    public ActivitePrincipale parent;
    private ListView liste;
    private AdaptateurListeClients adaptateur;

    private boolean isLoading = false;
    private int currentPage = 0;
    private int totalPages = 0;
    private List<Client> clients = new ArrayList<>();
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Client c = new Client("1","1","nomE",new Adresse("1 Rte de l'Aubrac", "12210", "Laguiole"),new Contact());
        clients.add(c);
        if (context instanceof ActivitePrincipale) {
            parent = (ActivitePrincipale) context;
        } else {
            throw new ClassCastException("Le contexte doit être une instance d'ActivitePrincipale.");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View frag = inflater.inflate(R.layout.activite_liste_client, container, false);
        liste = frag.findViewById(R.id.listitem_client);
        frag.findViewById(R.id.ajouter).setOnClickListener(this::ajouter);

        // Initialize adapter
        adaptateur = new AdaptateurListeClients(
                this.parent,
                R.layout.listitem_client,
                clients);
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

    private void fetchNombreClients() {
        ApiRequest.getNombreClient(requireContext(), new ApiRequest.ApiResponseCallback<JSONObject>() {
            @Override
            public void onSuccess(JSONObject response) {
                try {

                    System.out.println(response);
                    totalPages = response.getInt("nombre") ;

                    System.out.println("Total pages: " + totalPages);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(VolleyError error) {
                Toast.makeText(parent, "Erreur lors de la récupération du nombre de clients", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchClientsPage() {
        ApiRequest.getClientsBy30(requireContext(), currentPage, new ApiRequest.ApiResponseCallback<JSONArray>() {
            @Override
            public void onSuccess(JSONArray response) {
                int len = response.length();
                for (int i = 0; i < len; i++) {
                    try {
                        JSONObject clientJson = response.getJSONObject(i);

                        // Parse client data
                        String nomEntreprise = clientJson.getString("nomEntreprise");

                        // Get address data
                        JSONObject adresse = clientJson.getJSONObject("adresse");
                        String ville = adresse.getString("ville");
                        String codePostal = adresse.getString("codePostal");

                        // Create new client object
                        Client client = new Client(nomEntreprise, ville, codePostal);
                        clients.add(client);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                adaptateur.notifyDataSetChanged();
                currentPage++;
            }

            @Override
            public void onError(VolleyError error) {
                Toast.makeText(parent, "Erreur lors de la récupération des clients", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void ajouter(View view) {
        parent.replaceMainFragment(FragmentCreationClient.newInstance());
    }

    public void modifier(View view) {
        Bundle bundle = new Bundle();
        bundle.putString("id","5588");
        parent.navigateToFragment(ActivitePrincipale.FRAGMENT_CREATION_CLIENT,false,bundle);
    }

    private List<Client> parseClient(JSONObject response) {
        // Parse the JSON response and return a list of Itineraire objects
        List<Client> cli = new ArrayList<>();
        // Add parsing logic here
        return cli;
    }
}