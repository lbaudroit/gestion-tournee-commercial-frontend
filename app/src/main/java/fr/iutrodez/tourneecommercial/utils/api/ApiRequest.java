package fr.iutrodez.tourneecommercial.utils.api;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import java.util.Arrays;

/**
 * ApiRequest est une classe singleton qui gère divers objets de requêtes API.
 * Elle initialise différentes classes de requêtes API avec une RequestQueue partagée.
 */
public class ApiRequest {
    public AuthApiRequest auth;
    public ClientApiRequest client;
    public ItineraireApiRequest itineraire;
    public ParcoursApiRequest parcours;
    public UtilisateurApiRequest utilisateur;
    public BanApiRequest ban;

    private static ApiRequest instance = null;

    /**
     * Retourne l'instance singleton de ApiRequest.
     *
     * @return l'instance singleton de ApiRequest
     */
    public static ApiRequest getInstance() {
        return instance;
    }

    /**
     * Construit et retourne l'instance singleton de ApiRequest.
     *
     * @param context le contexte de l'application
     * @return l'instance singleton de ApiRequest
     */
    public static ApiRequest buildInstance(Context context) {
        instance = new ApiRequest(context);
        return instance;
    }

    /**
     * Constructeur privé qui initialise les objets de requêtes API avec une RequestQueue partagée.
     *
     * @param context le contexte de l'application
     */
    private ApiRequest(Context context) {
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        auth = new AuthApiRequest(requestQueue);
        client = new ClientApiRequest(requestQueue);
        itineraire = new ItineraireApiRequest(requestQueue);
        parcours = new ParcoursApiRequest(requestQueue);
        utilisateur = new UtilisateurApiRequest(requestQueue);
        ban = new BanApiRequest(requestQueue);
    }

    public static boolean hasInternetCapability(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        Network[] networks = connectivityManager.getAllNetworks();
        return networks.length > 0 && Arrays.stream(networks)
                .anyMatch(network -> {
                    NetworkCapabilities capabilities = connectivityManager.getNetworkCapabilities(network);
                    return capabilities != null && capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET);
                });
    }
}