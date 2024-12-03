package fr.iutrodez.tourneecommercial;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.navigation.NavigationBarView;

import fr.iutrodez.tourneecommercial.fragments.CarteFragment;
import fr.iutrodez.tourneecommercial.fragments.ClientsFragment;
import fr.iutrodez.tourneecommercial.fragments.HistoriqueFragment;
import fr.iutrodez.tourneecommercial.fragments.ItinerairesFragment;
import fr.iutrodez.tourneecommercial.fragments.ParametresFragment;

public class MainActivity extends AppCompatActivity
        implements NavigationBarView.OnItemSelectedListener {

    NavigationBarView navbar;

    Fragment[] fragments = {
            ClientsFragment.newInstance(),
            CarteFragment.newInstance(),
            HistoriqueFragment.newInstance(),
            ItinerairesFragment.newInstance(),
            ParametresFragment.newInstance()
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        navbar = findViewById(R.id.bottom_bar);
        navbar.setOnItemSelectedListener(this);
        replaceMainFragment(ClientsFragment.newInstance());
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        try {
            Fragment fragmentByButton = getFragmentByNavbarButton(item);
            replaceMainFragment(fragmentByButton);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    /**
     * Remplace le fragment de la fenêtre principale (hors navbar)
     *
     * @param fragment le fragment à utiliser comme remplacement
     */
    public void replaceMainFragment(Fragment fragment) {
        FragmentManager fm = getSupportFragmentManager();
        fm.beginTransaction()
                .replace(R.id.replaceable, fragment)
                .commit();
        System.out.println("navigated to new main fragment");
    }

    /**
     * @param item l'item du menu qui a été sélectionné
     * @return le fragment correspondant à chaque bouton de la navbar
     * @throws IllegalArgumentException si aucun fragment n'est associé à l'item
     */
    private Fragment getFragmentByNavbarButton(@NonNull MenuItem item)
            throws IllegalArgumentException {
        int id = item.getItemId();
        if (id == R.id.bottom_bar_clients) {
            return fragments[0];
        } else if (id == R.id.bottom_bar_carte) {
            return fragments[1];
        } else if (id == R.id.bottom_bar_historique) {
            return fragments[2];
        } else if (id == R.id.bottom_bar_itineraires) {
            return fragments[3];
        } else if (id == R.id.bottom_bar_parametres) {
            return fragments[4];
        }
        throw new IllegalArgumentException("No such fragment");
    }
}