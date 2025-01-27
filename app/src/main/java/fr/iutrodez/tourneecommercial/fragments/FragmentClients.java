package fr.iutrodez.tourneecommercial.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

import fr.iutrodez.tourneecommercial.ActivitePrincipale;
import fr.iutrodez.tourneecommercial.R;
import fr.iutrodez.tourneecommercial.modeles.Client;

public class FragmentClients extends Fragment {


    public static FragmentClients newInstance() {
        return new FragmentClients();
    }

    public ActivitePrincipale parent;

    private List<Client> client;

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
        String url = "http://192.168.16.35:8080/client/";

        RequestQueue queue = Volley.newRequestQueue(parent);

        // Créer une requête GET
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Afficher la réponse dans le TextView
                        Gson gson = new Gson();

                        // Définir le type pour une liste de clients
                        Type listType = (Type) TypeToken.getParameterized(List.class, Client.class).getType();

                        // Convertir le JSON en liste d'objets Client
                        List<Client> clients = gson.fromJson(response, listType);

                        // Afficher les clients ou effectuer une autre action
                        for (Client client : clients) {

                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println(error);
                // Afficher une erreur en cas de problème
            }
        });

        // Ajouter la requête à la file
        queue.add(stringRequest);

        return inflater.inflate(R.layout.activite_liste_client, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Configurer le bouton d'enregistrement
        view.findViewById(R.id.ajouter).setOnClickListener(this::ajouter);
    }

    public void ajouter(View view) {
        Bundle bundle = new Bundle();
        bundle.putString("param1", "Enzo est trop géniale meme si il dit que non ce con"); // Remplacez par vos paramètres
        bundle.putString("param2", "valeur2");
        parent.navigateToNavbarItem(ActivitePrincipale.FRAGMENT_HISTORIQUE, false, bundle);
    }
}
