package fr.iutrodez.tourneecommercial.utils.helper;

import android.content.Context;
import fr.iutrodez.tourneecommercial.modeles.Client;
import fr.iutrodez.tourneecommercial.modeles.Coordonnees;
import fr.iutrodez.tourneecommercial.utils.api.ApiRequest;

import java.util.List;

public class NotificationHelper {
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

    public NotificationHelper(Context context, NotificationListener listener) {
        this.context = context;
        this.listener = listener;
    }

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

    private void checkClientNotification(Coordonnees coordinates, Coordonnees clientCoordinates) {
        if (computeHaversineFormula(coordinates, clientCoordinates) < 200) {
            listener.onClientNotification();
        }
    }

    private boolean shouldCheckForProspectNotification(Coordonnees newCoordinates) {
        return computeHaversineFormula(newCoordinates, lastChangedCoordinates) > 200;
    }

    /**
     * Calcule la distance entre deux points géographiques en utilisant la formule de Haversine.
     *
     * @param otherCoordinates le point au quel on cherche la distance par rapport
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
}
