package fr.iutrodez.tourneecommercial.utils.api;

/**
 * 
 * @param <T>
 */
public interface SuccessCallback<T> {
    void onSuccess(T response);
}
