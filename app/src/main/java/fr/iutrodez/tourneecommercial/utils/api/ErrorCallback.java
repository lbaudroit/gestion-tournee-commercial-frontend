package fr.iutrodez.tourneecommercial.utils.api;

import com.android.volley.VolleyError;

public interface ErrorCallback {
    void onError(VolleyError error);
}
