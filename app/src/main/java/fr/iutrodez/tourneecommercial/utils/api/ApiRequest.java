package fr.iutrodez.tourneecommercial.utils.api;

import android.content.Context;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public class ApiRequest {
    //Déclaration des sous-classes
    public AuthApiRequest auth;
    public ClientApiRequest client;
    public ItineraireApiRequest itineraire;
    public ParcoursApiRequest parcours;
    public UtilisateurApiRequest utilisateur;


    // Déclaration du singleton
    private static ApiRequest instance = null;

    public static ApiRequest getInstance() {
        return instance;
    }

    public static ApiRequest buildInstance(Context context) {
        instance = new ApiRequest(context);
        return instance;
    }

    private ApiRequest(Context context) {
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        this.auth = new AuthApiRequest(requestQueue);
        /*this.client = new ClientApiRequest(requestQueue);
        this.itineraire = new ItineraireApiRequest(requestQueue);
        this.parcours = new ParcoursApiRequest(requestQueue);
        this.utilisateur = new UtilisateurApiRequest(requestQueue);*/
    }


}
