package fr.iutrodez.tourneecommercial.utils.api;

import com.android.volley.VolleyError;

/**
 * Interface pour gérer les erreurs lors d'une requête API.
 *
 * @author Benjamin NICOL, Enzo CLUZEL, Ahmed BRIBACH, Leïla BAUDROIT
 */
public interface ErrorCallback {
    void onError(VolleyError error);
}
