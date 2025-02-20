package fr.iutrodez.tourneecommercial.utils.helper;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Looper;

import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;

/**
 * Classe utilitaire permettant de gérer la localisation de l'utilisateur.
 */
public class LocationHelper {
    private final Context context;
    private final FusedLocationProviderClient fusedLocationClient;

    /**
     * Constructeur de LocationHelper.
     * @param context Le contexte de l'application.
     */
    public LocationHelper(Context context) {
        this.context = context;
        this.fusedLocationClient = LocationServices.getFusedLocationProviderClient(context);
    }

    /**
     * Vérifie si l'application dispose des permissions de localisation.
     * @return true si les permissions sont accordées, false sinon.
     */
    public boolean checkPermissions() {
        return ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(context,Manifest.permission.ACCESS_COARSE_LOCATION)
                == PackageManager.PERMISSION_GRANTED ;
    }

    /**
     * Récupère la dernière localisation connue.
     * @param callback Le callback qui reçoit la localisation.
     */
    public void getCurrentLocation(LocationCallback callback) {
        if (!checkPermissions()) return;

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        fusedLocationClient.getLastLocation().addOnSuccessListener(location -> {
            if (location != null) {
                callback.onLocationResult(LocationResult.create(java.util.Collections.singletonList(location)));
            } else {
                refreshLocation(callback);
            }
        });
    }

    /**
     * Demande une mise à jour de la localisation.
     * @param callback Le callback qui reçoit la nouvelle localisation.
     */
    public void refreshLocation(LocationCallback callback) {
        LocationRequest locationRequest = new LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 5000)
                .setMinUpdateIntervalMillis(1000)
                .setMaxUpdateDelayMillis(5000)
                .build();

        if (!checkPermissions()) return;

        fusedLocationClient.requestLocationUpdates(locationRequest, new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult != null && !locationResult.getLocations().isEmpty()) {
                    callback.onLocationResult(locationResult);
                    fusedLocationClient.removeLocationUpdates(this);
                }
            }
        }, Looper.getMainLooper());
    }

    /**
     * Démarre la mise à jour continue de la localisation.
     * @param callback Le callback qui reçoit les mises à jour de localisation.
     */
    public void startContinuousLocationUpdates(LocationCallback callback) {
        LocationRequest locationRequest = new LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 5000)
                .setMinUpdateIntervalMillis(1000)
                .setMaxUpdateDelayMillis(5000)
                .build();
        if (!checkPermissions()) return;

        fusedLocationClient.requestLocationUpdates(locationRequest, callback, Looper.getMainLooper());
    }

    /**
     * Arrête les mises à jour de localisation en cours.
     * @param callback Le callback associé aux mises à jour à stopper.
     */
    public void stopLocationUpdates(LocationCallback callback) {
        fusedLocationClient.removeLocationUpdates(callback);
    }
}
