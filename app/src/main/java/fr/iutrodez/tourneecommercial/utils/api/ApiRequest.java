package fr.iutrodez.tourneecommercial.utils.api;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * ApiRequest est une classe singleton qui gère divers objets de requêtes API.
 * Elle initialise différentes classes de requêtes API avec une RequestQueue partagée.
 *
 * @author Benjamin NICOL, Enzo CLUZEL, Ahmed BRIBACH, Leïla BAUDROIT
 */
public class ApiRequest {
    private static ApiRequest instance = null;
    public final AuthApiRequest auth;
    public final ClientApiRequest client;
    public final ItineraireApiRequest itineraire;
    public final ParcoursApiRequest parcours;
    public final UtilisateurApiRequest utilisateur;
    public final BanApiRequest ban;

    /**
     * Constructeur privé qui initialise les objets de requêtes API avec une RequestQueue partagée.
     *
     * @param context le contexte de l'application
     */
    private ApiRequest(Context context, String url) {
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        auth = new AuthApiRequest(requestQueue, url);
        client = new ClientApiRequest(requestQueue, url);
        itineraire = new ItineraireApiRequest(requestQueue, url);
        parcours = new ParcoursApiRequest(requestQueue, url);
        utilisateur = new UtilisateurApiRequest(requestQueue, url);
        ban = new BanApiRequest(requestQueue);
    }

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
    public static ApiRequest buildInstance(Context context, String url) {
        instance = new ApiRequest(context, url);
        return instance;
    }

    /**
     * Vérifie si l'appareil a la capacité de se connecter à Internet.
     *
     * @param context le contexte de l'application
     * @return true si l'appareil a la capacité de se connecter à Internet, false sinon
     */
    public static boolean hasInternetCapability(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkCapabilities capabilities = connectivityManager.getNetworkCapabilities(connectivityManager.getActiveNetwork());
        return capabilities == null || !capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET);
    }
}