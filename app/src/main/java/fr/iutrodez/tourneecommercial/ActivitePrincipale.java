package fr.iutrodez.tourneecommercial;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.navigation.NavigationBarView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import fr.iutrodez.tourneecommercial.fragments.FragmentCarte;
import fr.iutrodez.tourneecommercial.fragments.FragmentClients;
import fr.iutrodez.tourneecommercial.fragments.FragmentCreationClient;
import fr.iutrodez.tourneecommercial.fragments.FragmentCreationItineraire;
import fr.iutrodez.tourneecommercial.fragments.FragmentHistorique;
import fr.iutrodez.tourneecommercial.fragments.FragmentItineraires;
import fr.iutrodez.tourneecommercial.fragments.FragmentParametres;

/**
 * Classe principale de l'activité qui gère la navigation entre différents fragments.
 */
public class ActivitePrincipale extends AppCompatActivity
        implements NavigationBarView.OnItemSelectedListener {
    private FragmentManager fm;
    private NavigationBarView navbar;
    public final static int FRAGMENT_CLIENTS = 0;
    public final static int FRAGMENT_CARTE = 1;
    public final static int FRAGMENT_HISTORIQUE = 2;
    public final static int FRAGMENT_ITINERAIRES = 3;
    public final static int FRAGMENT_PARAMETRES = 4;
    public final static int FRAGMENT_CREATION_CLIENT = 5;
    public final static int FRAGMENT_CREATION_ITINERAIRE = 6;

    List<Class<? extends Fragment>> fragments = new ArrayList<>(5);

    {
        fragments.add(FragmentClients.class);
        fragments.add(FragmentCarte.class);
        fragments.add(FragmentHistorique.class);
        fragments.add(FragmentItineraires.class);
        fragments.add(FragmentParametres.class);
        fragments.add(FragmentCreationClient.class);
        fragments.add(FragmentCreationItineraire.class);
    }

    HashMap<Integer, Fragment> cache = new HashMap<>();

    HashMap<Integer, Integer> menuId = new HashMap<>();

    {
        menuId.put(R.id.bottom_bar_clients, FRAGMENT_CLIENTS);
        menuId.put(R.id.bottom_bar_carte, FRAGMENT_CARTE);
        menuId.put(R.id.bottom_bar_historique, FRAGMENT_HISTORIQUE);
        menuId.put(R.id.bottom_bar_itineraires, FRAGMENT_ITINERAIRES);
        menuId.put(R.id.bottom_bar_parametres, FRAGMENT_PARAMETRES);
    }

    /**
     * Appelé lors de la création de l'activité.
     *
     * @param savedInstanceState Si l'activité est recréée après avoir été précédemment arrêtée, ce Bundle contient les données les plus récentes fournies dans onSaveInstanceState(Bundle).
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activite_principale);
        navbar = findViewById(R.id.bottom_bar);
        navbar.setOnItemSelectedListener(this);
        fm = getSupportFragmentManager();
        navigateToFragment(FRAGMENT_CLIENTS, false);
        System.out.println(fm.getBackStackEntryCount());
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                // Handle the back button press

                if (fm.getBackStackEntryCount() > 1) {
                    int id = Integer.parseInt(Objects.requireNonNull(fm.getBackStackEntryAt(fm.getBackStackEntryCount() - 1).getName()));
                    System.out.println(id);
                    navbar.setSelectedItemId(id);
                    fm.popBackStack();
                } else {
                    new AlertDialog.Builder(ActivitePrincipale.this)
                            .setTitle("Quitter l'application")
                            .setMessage("Voulez-vous vraiment quitter l'application ?")
                            .setPositiveButton("Oui", (dialog, which) -> finish())
                            .setNegativeButton("Non", null)
                            .show();

                }
            }
        });
    }

    /**
     * Appelé lorsqu'un élément de la barre de navigation est sélectionné.
     *
     * @param item L'élément sélectionné.
     * @return true pour afficher l'élément comme sélectionné, false pour ne rien faire.
     */
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        boolean cache = false;
        Integer id = menuId.get(item.getItemId());
        if (id == null) {
            return false;
        }
        if (id == FRAGMENT_CARTE) {
            cache = true;
        }
        navigateToFragment(id, cache);
        return true;
    }

    /**
     * Navigue vers un fragment basé sur l'ID d'un fragment.
     * Cette version met à jour le tab de navigation séléctionner.
     *
     * @param id     L'ID du fragment.
     * @param cached Indique s'il faut utiliser un fragment mis en cache.
     */
    public void navigateToNavbarItem(int id, boolean cached) {
        navigateToFragment(id, cached);
        navbar.setSelectedItemId(getNavbarItemId(id));
    }

    /**
     * Navigue vers un fragment basé sur l'ID d'un fragment avec des paramètres supplémentaires.
     * Cette version met à jour le tab de navigation séléctionner.
     *
     * @param id     L'ID du fragment.
     * @param cached Indique s'il faut utiliser un fragment mis en cache.
     * @param bundle Paramètres supplémentaires à passer au fragment.
     */
    public void navigateToNavbarItem(int id, boolean cached, Bundle bundle) {
        navigateToFragment(id, cached, bundle);
        navbar.setSelectedItemId(getNavbarItemId(id));
    }

    /**
     * Navigue vers un fragment.
     *
     * @param id     L'ID du fragment.
     * @param cached Indique s'il faut utiliser un fragment mis en cache.
     */
    public void navigateToFragment(int id, boolean cached) {
        Fragment fragment = cached ? getCachedFragment(id) : getNotCachedFragment(id);
        fm.beginTransaction()
                .replace(R.id.replaceable, fragment)
                .addToBackStack(String.valueOf(navbar.getSelectedItemId()))
                .commit();
    }

    /**
     * Navigue vers un fragment avec des paramètres supplémentaires.
     *
     * @param id     L'ID du fragment.
     * @param cached Indique s'il faut utiliser un fragment mis en cache.
     * @param bundle Paramètres supplémentaires à passer au fragment.
     */
    public void navigateToFragment(int id, boolean cached, Bundle bundle) {
        Fragment fragment = cached ? getCachedFragment(id) : getNotCachedFragment(id);
        fragment.setArguments(bundle);
        fm.beginTransaction()
                .replace(R.id.replaceable, fragment)
                .addToBackStack(String.valueOf(getNavbarItemId(id)))
                .commit();
    }

    /**
     * Obtient un fragment déjà dans le cache.
     * Si le fragment n'est pas en cache, une nouvelle instance est créée.
     *
     * @param id L'ID du fragment.
     * @return Le fragment mis en cache.
     */
    private Fragment getCachedFragment(int id) {
        Fragment fragment = null;
        if (cache.containsKey(id)) {
            fragment = cache.get(id);
        } else {
            fragment = getNotCachedFragment(id);
        }
        return fragment;
    }

    /**
     * Obtient une nouvelle instance d'un fragment et le met en cache.
     * Même si le fragment est déjà en cache, une nouvelle instance est créée.
     *
     * @param id L'ID du fragment.
     * @return La nouvelle instance du fragment.
     */
    private Fragment getNotCachedFragment(int id) {
        Fragment fragment = null;
        try {
            fragment = fragments.get(id).newInstance();
            cache.put(id, fragment);
        } catch (IllegalAccessException | InstantiationException e) {
            throw new RuntimeException(e);
        }
        return fragment;
    }

    /**
     * Obtient l'ID de l'élément de la barre de navigation pour un ID de fragment donné.
     *
     * @param id L'ID du fragment.
     * @return L'ID de l'élément de la barre de navigation.
     */
    private int getNavbarItemId(int id) {
        int id_found = -1;
        for (Map.Entry<Integer, Integer> set : menuId.entrySet()) {
            if (set.getValue() == id) {
                id_found = set.getKey();
                break;
            }
        }
        return id_found;
    }
}