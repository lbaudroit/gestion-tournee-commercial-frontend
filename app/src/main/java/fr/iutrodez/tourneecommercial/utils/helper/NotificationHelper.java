package fr.iutrodez.tourneecommercial.utils.helper;

import android.app.AlertDialog;
import android.content.Context;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import fr.iutrodez.tourneecommercial.R;
import fr.iutrodez.tourneecommercial.model.Client;
import fr.iutrodez.tourneecommercial.model.Coordonnees;
import fr.iutrodez.tourneecommercial.utils.api.ApiRequest;

import java.util.List;

/**
 * Classe utilitaire pour gérer les notifications basées sur la localisation.
 *
 * @author Benjamin NICOL, Enzo CLUZEL, Ahmed BRIBACH, Leïla BAUDROIT
 */
public class NotificationHelper {
    private final Context context;
    private final NotificationListener listener;

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
     * Vérifie et notifie si des prospects sont à proximité.
     */
    public void checkProspectNotification(Coordonnees coordinates) {
        ApiRequest.getInstance().parcours.getProspectsForNotifications(context, coordinates.getLatitude(), coordinates.getLongitude(),
                prospects -> {
                    if (!prospects.isEmpty()) {
                        listener.onProspectNotification(prospects);
                    }
                }, error -> {
                    // Do nothing
                });
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

    /**
     * Affiche un pop up de notification.
     * Celui-ci joue un son et affiche un message pendant 10 secondes.
     * L'utilisateur est informé toutes les secondes du temps restant.
     * L'utilisateur peut également fermer la notification manuellement en appuyant sur le bouton "Fermer".
     *
     * @param contenue le message à afficher
     */
    public void triggerNotification(String title, String contenue) {
        this.playNotificationSound();
        AlertDialog dialog = new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(contenue)
                .setPositiveButton("Fermer", (dialogInterface, which) -> dialogInterface.dismiss())
                .create();
        dialog.show();
        handleAutoCloseNotification(dialog, contenue);
    }

    /**
     * Affiche un message pendant 20 secondes.
     *
     * @param dialog   le dialog affiché
     * @param contenue le message à afficher
     */
    private void handleAutoCloseNotification(AlertDialog dialog, String contenue) {
        Handler handler = new Handler(Looper.getMainLooper());
        final int[] secondsLeft = {20};
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                if (secondsLeft[0] > 0) {
                    dialog.setMessage(contenue + "\n" + context.getString(R.string.time_remaining, secondsLeft[0]));
                    secondsLeft[0]--;
                    handler.postDelayed(this, 1000);
                } else {
                    dialog.dismiss();
                }
            }
        };
        handler.post(runnable);
    }

    /**
     * Interface pour écouter les notifications de prospects et de clients.
     */
    public interface NotificationListener {
        void onProspectNotification(List<Client> prospects);
    }
}
