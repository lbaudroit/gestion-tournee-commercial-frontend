package fr.iutrodez.tourneecommercial;

import android.app.AlertDialog;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import com.google.android.material.navigation.NavigationBarView;
import fr.iutrodez.tourneecommercial.fragments.*;

import java.util.*;

/**
 * Classe principale de l'activité qui gère la navigation entre différents fragments.
 */
public class MainActivity extends AppCompatActivity
        implements NavigationBarView.OnItemSelectedListener {
    private FragmentManager fragmentManager;
    private NavigationBarView navigationBar;
    public final static int CLIENT_FRAGMENT = 0;
    public final static int MAP_FRAGMENT = 1;
    public final static int HISTORY_FRAGMENT = 2;
    public final static int ITINERARY_FRAGMENT = 3;
    public final static int SETTING_FRAGMENT = 4;
    public final static int CLIENT_CREATION_FRAGMENT = 5;
    public final static int ITINERARY_CREATION_FRAGMENT = 6;
    public final static int PASSWORD_MODIFICATION_FRAGMENT = 7;

    public final static int COURSE_VIEW_FRAGMENT = 8;

    List<Class<? extends Fragment>> fragments = new ArrayList<>(5);

    {
        fragments.add(ClientFragment.class);
        fragments.add(MapFragment.class);
        fragments.add(HistoryFragment.class);
        fragments.add(ItineraryFragment.class);
        fragments.add(SettingFragment.class);
        fragments.add(ClientCreationFragment.class);
        fragments.add(ItineraryCreationFragment.class);
        fragments.add(PasswordModificationFragment.class);
        fragments.add(CourseFragment.class);
    }

    HashMap<Integer, Fragment> cache = new HashMap<>();

    HashMap<Integer, Integer> menuId = new HashMap<>();

    Stack<Integer> commits = new Stack<>();

    {
        menuId.put(R.id.bottom_bar_client, CLIENT_FRAGMENT);
        menuId.put(R.id.bottom_bar_map, MAP_FRAGMENT);
        menuId.put(R.id.bottom_bar_history, HISTORY_FRAGMENT);
        menuId.put(R.id.bottom_bar_itinerary, ITINERARY_FRAGMENT);
        menuId.put(R.id.bottom_bar_setting, SETTING_FRAGMENT);
    }

    /**
     * Appelé lors de la création de l'activité.
     *
     * @param savedInstanceState Si l'activité est recréée après avoir été précédemment arrêtée, ce Bundle contient les données les plus récentes fournies dans onSaveInstanceState(Bundle).
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        navigationBar = findViewById(R.id.bottom_bar);
        navigationBar.setOnItemSelectedListener(this);

        fragmentManager = getSupportFragmentManager();
        navigateToFragment(CLIENT_FRAGMENT, false);

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                // Handle the back button press

                if (fragmentManager.getBackStackEntryCount() > 1) {
                    int id = Integer.parseInt(Objects.requireNonNull(fragmentManager.getBackStackEntryAt(fragmentManager.getBackStackEntryCount() - 1).getName()));

                    // Désactive le listener pour éviter d'ajouter une nouvelle d'activité à la pile
                    navigationBar.setOnItemSelectedListener(item -> true);
                    navigationBar.setSelectedItemId(id);
                    navigationBar.setOnItemSelectedListener(MainActivity.this);

                    Integer toRemove = commits.pop();
                    fragmentManager.popBackStack(toRemove, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                } else {
                    new AlertDialog.Builder(MainActivity.this)
                            .setTitle(R.string.quit_app)
                            .setMessage(R.string.confirm_quit_app)
                            .setPositiveButton(R.string.yes, (dialog, which) -> finish())
                            .setNegativeButton(R.string.no, null)
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
        if (id == MAP_FRAGMENT) {
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
        navigationBar.setSelectedItemId(getNavbarItemId(id));
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
        navigationBar.setSelectedItemId(getNavbarItemId(id));
    }

    /**
     * Navigue vers un fragment.
     *
     * @param id     L'ID du fragment.
     * @param cached Indique s'il faut utiliser un fragment mis en cache.
     */
    public void navigateToFragment(int id, boolean cached) {
        Fragment fragment = cached ? getCachedFragment(id) : getNotCachedFragment(id);
        int commit = fragmentManager.beginTransaction()
                .replace(R.id.replaceable, fragment)
                .addToBackStack(String.valueOf(navigationBar.getSelectedItemId()))
                .commit();
        commits.push(commit);
    }

    /**
     * Efface le cache d'un fragment.
     *
     * @param id
     */
    public void clearCache(int id) {
        cache.remove(id);
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
        int commit = fragmentManager.beginTransaction()
                .replace(R.id.replaceable, fragment)
                .addToBackStack(String.valueOf(getNavbarItemId(id)))
                .commit();
        commits.push(commit);
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
        int idFound = -1;
        for (Map.Entry<Integer, Integer> set : menuId.entrySet()) {
            if (set.getValue() == id) {
                idFound = set.getKey();
                break;
            }
        }
        return idFound;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d("MainActivity", "Permission GPS accordée !");
            } else {
                Log.e("MainActivity", "Permission GPS refusée !");
            }
        }
    }

}