package fr.iutrodez.tourneecommercial.utils;

import android.view.View;

public class WidgetHelpers {
    public static void disableView(View view) {
        view.setEnabled(false);
        view.setAlpha(0.5f);
    }

    public static void enableView(View view) {
        view.setEnabled(true);
        view.setAlpha(1f);
    }
}
