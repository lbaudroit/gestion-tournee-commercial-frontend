package fr.iutrodez.tourneecommercial.utils.api;

import android.content.Context;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

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
}