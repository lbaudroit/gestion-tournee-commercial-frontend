package fr.iutrodez.tourneecommercial.utils.api;

/**
 * Interface pour les callbacks de succès.
 *
 * @param <T> le type de la réponse
 * @author Benjamin NICOL, Enzo CLUZEL, Ahmed BRIBACH, Leïla BAUDROIT
 */
public interface SuccessCallback<T> {
    void onSuccess(T response);
}
