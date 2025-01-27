package fr.iutrodez.tourneecommercial.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import java.lang.reflect.Type;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import fr.iutrodez.tourneecommercial.ActiviteCreationClient;
import fr.iutrodez.tourneecommercial.ActivitePrincipale;
import fr.iutrodez.tourneecommercial.R;
import fr.iutrodez.tourneecommercial.modeles.Client;
import fr.iutrodez.tourneecommercial.modeles.Itineraire;
import fr.iutrodez.tourneecommercial.utils.AdaptateurListeClients;
import fr.iutrodez.tourneecommercial.utils.AdaptateurListeItineraire;
import fr.iutrodez.tourneecommercial.utils.ApiRequest;

public class FragmentClients extends Fragment {


    public static FragmentClients newInstance() {
        return new FragmentClients();
    }

    public ActivitePrincipale parent;


    private ListView liste ;

    private AdaptateurListeClients adaptateur;

    private boolean isLoading = false;
    private int currentPage = 0;
    private int totalPages = 0;
    private List<Client> clients = new ArrayList<>();

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
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


        return frag;

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Configurer le bouton d'enregistrement
        view.findViewById(R.id.ajouter).setOnClickListener(this::ajouter);

        liste = view.findViewById(R.id.listitem_client);
        adaptateur = new AdaptateurListeClients(
                this.parent,
                R.layout.listitem_client,
                clients);
        liste.setAdapter(adaptateur);
        getClientsBy30();
        getNumberPage();

        liste.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (!isLoading && (firstVisibleItem + visibleItemCount >= totalItemCount) && currentPage < totalPages && totalItemCount > 0) {
                    isLoading = true;
                    getNumberPage();
                    isLoading = false;
                }
            }
        });

    }

    private void getClientsBy30(){
        ApiRequest.getClientsBy30(requireContext(), currentPage, new ApiRequest.ApiResponseCallback<JSONArray>() {
            @Override
            public void onSuccess(JSONArray response) {
                // Ajouter les données dans l'adaptateur
                int len = response.length();
                for (int i = 0; i < len; i++) {
                    try {
                        JSONObject client = response.getJSONObject(i);
                        System.out.println(clients.toString());

                        clients.add();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                adaptateur.notifyDataSetChanged();

                // Afficher un message de succès
                Toast.makeText(requireContext(), "Clients récupérés avec succès", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(VolleyError error) {
                // Afficher un message d'erreur
                Toast.makeText(requireContext(), "Erreur: " + error.toString(), Toast.LENGTH_LONG).show();
            }
        });

    }
    private void getNumberPage() {
        ApiRequest.getNombreClient(requireContext(), new ApiRequest.ApiResponseCallback<JSONObject>() {
            @Override
            public void onSuccess(JSONObject response) {
                // Ajouter les données dans l'adaptateur
                totalPages = Integer.parseInt(response.toString());

                // Afficher un message de succès
                Toast.makeText(requireContext(), "Clients récupérés avec succès", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(VolleyError error) {
                // Afficher un message d'erreur
                Toast.makeText(requireContext(), "Erreur: " + error.toString(), Toast.LENGTH_LONG).show();
            }
        });
        currentPage++;

    }

    public void ajouter(View view) {
        parent.replaceMainFragment(FragmentCreationClient.newInstance());
    }
    private List<Client> parseClient(JSONObject response) {
        // Parse the JSON response and return a list of Itineraire objects
        List<Client> cli = new ArrayList<>();
        // Add parsing logic here
        return cli;
    }
}
