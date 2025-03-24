package fr.iutrodez.tourneecommercial.utils.helper;

import android.content.Context;
import android.util.Log;
import fr.iutrodez.tourneecommercial.model.Parcours;

import java.io.*;

/**
 * Cette classe permet de gérer la sérialisation et désérialisation des parcours.
 * Il existe deux cas différents :
 * - "localsave" : lorsqu'on met le parcours en pause
 * - "tosend" : lorsque l'envoi du parcours une fois terminé n'a pas fonctionné
 * <p>
 * Elle contient également les utilitaires pour un système de verrou, permettant que deux
 * requêtes vers l'API ne soient pas faites en simultané pour envoyer les parcours en attente.
 *
 * @author Benjamin NICOL, Enzo CLUZEL, Ahmed BRIBACH, Leïla BAUDROIT
 */
public class SavedParcoursHelper {

    public static final String SAVED_FILE_NAME = "localsave.ser";

    private static final String TO_SEND_FILE_NAME = "tosend.ser";

    private static final String TO_SEND_LOCK_FILE_NAME = "send.lock";

    private final Context context;

    public SavedParcoursHelper(Context context) {
        this.context = context;
    }

    /**
     * Verrouille l'envoi de fichiers.
     */
    public void lockForSending() {
        try {
            System.out.println(new File(context.getFilesDir(), TO_SEND_LOCK_FILE_NAME).createNewFile() ? "Lock created" : "Lock already exists");
        } catch (IOException e) {
            Log.e("Error", e.getMessage());
        }
    }

    /**
     * Supprime le fichier de verrouillage pour l'envoi de fichiers.
     */
    public void unlockForSending() {
        System.out.println(new File(context.getFilesDir(), TO_SEND_LOCK_FILE_NAME).delete() ? "Lock deleted" : "Lock does not exist");
    }

    /**
     * Vérifie que l'on a la main pour envoyer le fichier de parcours non envoyé
     *
     * @return true si la modification est verrouillée, false sinon
     */
    public boolean isLockedForSending() {
        return new File(context.getFilesDir(), TO_SEND_LOCK_FILE_NAME).exists();
    }

    /**
     * @return la liste des fichiers à envoyer à l'API.
     */
    public File getFileToSend() {
        return new File(context.getFilesDir(), TO_SEND_FILE_NAME);
    }

    /**
     * Sérialise un parcours pour l'envoyer plus tard.
     *
     * @param parcours le parcours à sérialiser
     */
    public void serializeToSendLater(Parcours parcours) {
        serializeParcours(context, TO_SEND_FILE_NAME, parcours);
    }

    /**
     * Renvoie le fichier contenant la sauvegarde du parcours courant.
     *
     * @return le fichier contenant la sauvegarde du parcours courant, ou null si celui-ci n'existe pas
     */
    public File getFileForLocalSave() {
        File f = new File(context.getFilesDir(), SAVED_FILE_NAME);
        if (!f.exists() || !f.isFile()) {
            return null;
        }
        return f;
    }

    /**
     * Déserialise un parcours à partir d'un fichier.
     *
     * @param file le fichier à désérialiser
     * @return le parcours désérialisé, ou null si le fichier n'existe pas ou est vide
     */
    public Parcours deserializeParcoursFromFile(File file) {
        if (!file.exists()) {
            return null;
        }
        if (file.length() == 0) {
            return null;
        }
        try (FileInputStream fileIn = context.openFileInput(file.getName());
             ObjectInputStream in = new ObjectInputStream(fileIn)) {
            return (Parcours) in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            Log.e("MapFragment", "Error deserializing map data", e);
        }
        return null;
    }

    /**
     * Sérialise les données du parcours courant dans un fichier
     *
     * @param mapData Les données de la carte à sérialiser.
     */
    public void serializeParcours(Parcours mapData) {
        serializeParcours(context, SAVED_FILE_NAME, mapData);
    }

    /**
     * Sérialise un parcours et l'ajoute dans le fichier au nom correspondant
     *
     * @param context  le contexte  de l'application
     * @param filename le nom du fichier à créer
     * @param parcours le parcours à sérialiser
     */
    private void serializeParcours(Context context, String filename, Parcours parcours) {
        try (FileOutputStream fos = context.openFileOutput(filename, Context.MODE_PRIVATE);
             ObjectOutputStream oos = new ObjectOutputStream(fos)) {
            oos.writeObject(parcours);
            oos.flush();
            Log.d("MapFragment", "Serialized map data");
        } catch (IOException e) {
            Log.e("MapFragment", "Error serializing map data", e);
        }
    }

    /**
     * Désérialise les données du parcours n'ayant pas pu être envoyé.
     *
     * @return Les données du parcours ou null si le fichier n'existe pas ou est vide.
     */
    public Parcours deserializeSavedParcours() {
        File file = new File(context.getFilesDir(), SAVED_FILE_NAME);
        return deserializeParcoursFromFile(file);
    }

    /**
     * Supprime les données de la carte du parcours courant sauvegardé.
     */
    public void deleteSavedParcours() {
        File file = new File(context.getFilesDir(), SAVED_FILE_NAME);
        if (file.exists()) {
            Log.e("MapFragment", file.delete() ? "Deleted map data" : "Failed to delete map data");
        }
    }

}
