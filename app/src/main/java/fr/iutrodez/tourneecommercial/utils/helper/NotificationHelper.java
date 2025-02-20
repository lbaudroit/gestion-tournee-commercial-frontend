package fr.iutrodez.tourneecommercial.utils.helper;

import android.content.Context;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import fr.iutrodez.tourneecommercial.modeles.Client;
import fr.iutrodez.tourneecommercial.modeles.Coordonnees;
import fr.iutrodez.tourneecommercial.utils.api.ApiRequest;

import java.util.List;

/**
 * Classe utilitaire pour gérer les notifications basées sur la localisation.
 */
public class NotificationHelper {
    /**
     * Interface pour écouter les notifications de prospects et de clients.
     */
    public interface NotificationListener {
        /**
         * Appelé lorsque au moins un prospect est à moins de 1 Km.
         */
        void onProspectNotification(List<Client> prospects);

        /**
         * Appelé lorsque le client est à moins de 200 mètres.
         */
        void onClientNotification();
    }

    private final Context context;
    private final NotificationListener listener;
    private Coordonnees lastChangedCoordinates;

    /**
     * Constructeur pour initialiser le NotificationHelper.
     *
     * @param context  le contexte de l'application
     * @param listener l'écouteur de notifications
     */
    public NotificationHelper(Context context, NotificationListener listener) {
        this.context = context;
        this.listener = listener;
    }

    /**
     * Méthode appelée lorsque la localisation change.
     *
     * @param coordinates       les nouvelles coordonnées
     * @param clientCoordinates les coordonnées du client
     */
    public void locationChanged(Coordonnees coordinates, Coordonnees clientCoordinates) {
        if (lastChangedCoordinates == null) {
            lastChangedCoordinates = coordinates;
            checkProspectNotification();
        }
        if (shouldCheckForProspectNotification(coordinates)) {
            lastChangedCoordinates = coordinates;
            checkProspectNotification();
        }
        checkClientNotification(coordinates, clientCoordinates);
    }

    /**
     * Vérifie et notifie si des prospects sont à proximité.
     */
    private void checkProspectNotification() {
        ApiRequest.getInstance().parcours.getProspectsForNotifications(context, lastChangedCoordinates.getLatitude(), lastChangedCoordinates.getLongitude(),
                prospects -> {
                    if (!prospects.isEmpty()) {
                        listener.onProspectNotification(prospects);
                    }
                }, error -> {
                    // Do nothing
                });
    }

    /**
     * Vérifie et notifie si le client est à proximité.
     *
     * @param coordinates       les nouvelles coordonnées
     * @param clientCoordinates les coordonnées du client
     */
    private void checkClientNotification(Coordonnees coordinates, Coordonnees clientCoordinates) {
        if (computeHaversineFormula(coordinates, clientCoordinates) < 200) {
            listener.onClientNotification();
        }
    }

    /**
     * Détermine si une vérification de notification de prospect est nécessaire.
     *
     * @param newCoordinates les nouvelles coordonnées
     * @return true si une vérification est nécessaire, sinon false
     */
    private boolean shouldCheckForProspectNotification(Coordonnees newCoordinates) {
        return computeHaversineFormula(newCoordinates, lastChangedCoordinates) > 200;
    }

    /**
     * Calcule la distance entre deux points géographiques en utilisant la formule de Haversine.
     *
     * @param coordinates      les coordonnées du premier point
     * @param otherCoordinates les coordonnées du second point
     * @return la distance entre les deux points en mètres
     */
    private static int computeHaversineFormula(Coordonnees coordinates, Coordonnees otherCoordinates) {
        double earthRadiusInKm = 6371.0;
        double deltaLat = Math.toRadians(otherCoordinates.getLatitude() - coordinates.getLatitude());
        double deltaLon = Math.toRadians(otherCoordinates.getLongitude() - coordinates.getLongitude());
        double startLat = Math.toRadians(coordinates.getLatitude());
        double endLat = Math.toRadians(otherCoordinates.getLatitude());

        double a = Math.pow(Math.sin(deltaLat / 2), 2)
                + Math.cos(startLat) * Math.cos(endLat)
                * Math.pow(Math.sin(deltaLon / 2), 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return (int) Math.round(earthRadiusInKm * c * 1000);
    }

    /**
     * Joue un son de notification.
     */
    public void playNotificationSound() {
        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Ringtone ringtone = RingtoneManager.getRingtone(context, notification);
        ringtone.play();
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            if (ringtone.isPlaying()) {
                ringtone.stop();
            }
        }, 2000);
    }
}
