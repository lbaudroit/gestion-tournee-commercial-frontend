package fr.iutrodez.tourneecommercial.utils.api;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Looper;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import fr.iutrodez.tourneecommercial.modeles.Parcours;

public class OfflineRequestManager {
    private static final String PREF_NAME = "OfflineRequests";
    private static final String KEY_PENDING_PARCOURS = "pending_parcours";
    private static OfflineRequestManager instance;
    private final Context context;
    private final Handler handler;
    private boolean isProcessing = false;

    private OfflineRequestManager(Context context) {
        this.context = context.getApplicationContext();
        this.handler = new Handler(Looper.getMainLooper());
    }

    public static synchronized OfflineRequestManager getInstance(Context context) {
        if (instance == null) {
            instance = new OfflineRequestManager(context);
        }
        return instance;
    }

    public void saveParcours(Parcours parcours) {
        List<Parcours> pendingParcours = getPendingParcours();
        pendingParcours.add(parcours);
        savePendingParcours(pendingParcours);
        processQueue();
    }

    private List<Parcours> getPendingParcours() {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        String json = prefs.getString(KEY_PENDING_PARCOURS, "[]");
        return new Gson().fromJson(json, new TypeToken<List<Parcours>>(){}.getType());
    }

    private void savePendingParcours(List<Parcours> parcours) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        String json = new Gson().toJson(parcours);
        prefs.edit().putString(KEY_PENDING_PARCOURS, json).apply();
    }

    public void processQueue() {
        if (!isNetworkAvailable() || isProcessing) {
            return;
        }

        List<Parcours> pendingParcours = getPendingParcours();
        if (pendingParcours.isEmpty()) {
            return;
        }

        isProcessing = true;
        processParcours(new ArrayList<>(pendingParcours));
    }

    private void processParcours(List<Parcours> parcours) {
        if (parcours.isEmpty()) {
            isProcessing = false;
            return;
        }

        Parcours currentParcours = parcours.get(0);
        ApiRequest.getInstance().parcours.create(
                context,
                currentParcours,
                response -> {
                    // Succès : supprimer le parcours de la liste et continuer
                    List<Parcours> remaining = getPendingParcours();
                    remaining.remove(0);
                    savePendingParcours(remaining);

                    parcours.remove(0);
                    handler.post(() -> processParcours(parcours));
                },
                error -> {
                    if (!isNetworkAvailable()) {
                        // Si pas de réseau, arrêter le traitement
                        isProcessing = false;
                    } else {
                        // Si erreur mais réseau disponible, essayer le suivant
                        parcours.remove(0);
                        handler.post(() -> processParcours(parcours));
                    }
                }
        );
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager == null) {
            return false;
        }
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}