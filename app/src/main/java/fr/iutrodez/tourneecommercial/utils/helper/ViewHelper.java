package fr.iutrodez.tourneecommercial.utils.helper;

import android.view.View;

public class ViewHelper {

    /**
     * Désactive une vue affichée et diminue son opacité.
     * @param view la vue à modifier
     */
    public static void disableView(View view) {
        view.setEnabled(false);
        view.setAlpha(0.5f);
    }

    /**
     * Active une vue affichée et rétablit son opacité à 100¨%
     * @param view la vue à modifier
     */
    public static void enableView(View view) {
        view.setEnabled(true);
        view.setAlpha(1f);
    }

    /**
     * Met à jour la visibilité d'un ensemble d'éléments de vue.
     * @param visibility un entier parmi {@code View.GONE}, {@code View.VISIBLE}, ou {@code View.INVISIBLE}
     */
    public static void setVisibilityFor(int visibility, View... views) {

        if (visibility != View.GONE
                && visibility != View.VISIBLE
                && visibility != View.INVISIBLE) {
            throw new IllegalArgumentException("Visibility must be one of View.GONE, View.VISIBLE, or View.INVISIBLE");
        }

        for (View view : views) {
            view.setVisibility(visibility);
        }
    }
}
