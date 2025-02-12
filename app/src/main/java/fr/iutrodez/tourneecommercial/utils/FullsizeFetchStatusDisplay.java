package fr.iutrodez.tourneecommercial.utils;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;

import fr.iutrodez.tourneecommercial.R;

/**
 * Composant permettant d'afficher des informations sur l'état de chargement des données
 * depuis l'API.
 * Possède trois états :
 * - hidden : n'est pas affiché, ne prend ni hauteur, ni largeur
 * - loading : affiche une barre de chargement
 * - error : affiche un message d'erreur (nécessite de fournir un message d'erreur)
 *
 * @author Benjamin NICOL
 * @author Leïla BAUDROIT
 * @author Enzo CLUZEL
 * @author Ahmed BRIBACH
 */
public class FullsizeFetchStatusDisplay extends LinearLayout {
    private ProgressBar progressBar;
    private TextView error;
    private Runnable hideContentFunction;
    private Runnable showContentFunction;
    public FullsizeFetchStatusDisplay(Context context) {
        super(context);
    }

    public FullsizeFetchStatusDisplay(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public FullsizeFetchStatusDisplay(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public FullsizeFetchStatusDisplay(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    /**
     * Initialise le composant à l'aide du Layout XML associé.
     * Récupère ainsi les composants ProgressBar et TextView
     * @param context le contexte d'inflation du Layout
     */
    public void init(Context context) {
        LayoutInflater.from(context).inflate(R.layout.view_fetch_status, this, true);
        progressBar = findViewById(R.id.progressBar_loading);
        error = findViewById(R.id.textView_error);
    }

    /**
     * Modifie la fonction appelée pour cacher le reste du contenu de la page
     * @param hideContentFunction la fonction permettant de cacher le reste du contenu de la page
     */
    public void setHideContentFunction(Runnable hideContentFunction) {
        this.hideContentFunction = hideContentFunction;
    }

    /**
     * Modifie la fonction appelée pour afficher le reste du contenu de la page
     * @param showContentFunction la fonction permettant d'afficher le reste du contenu de la page
     */
    public void setShowContentFunction(Runnable showContentFunction) {
        this.showContentFunction = showContentFunction;
    }

    /**
     * Affiche la barre de chargement
     */
    public void setLoading() {
        hideContentFunction.run();
        progressBar.setVisibility(VISIBLE);
        error.setVisibility(GONE);
    }

    /**
     * Affiche un message d'erreur
     * @param message Ressource du message d'erreur
     */
    public void setError(int message) {
        hideContentFunction.run();
        progressBar.setVisibility(GONE);
        error.setText(getContext().getString(message));
        error.setVisibility(VISIBLE);
    }

    /**
     * Cache la barre de chargement et le message d'erreur :
     * le composant est entièrement invisible, sans hauteur ni largeur
     */
    public void hide() {
        showContentFunction.run();
        progressBar.setVisibility(GONE);
        error.setVisibility(GONE);
    }
}
