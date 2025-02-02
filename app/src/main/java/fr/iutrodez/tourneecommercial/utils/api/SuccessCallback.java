package fr.iutrodez.tourneecommercial.utils.api;

public interface SuccessCallback<T> {
    void onSuccess(T response);
}
