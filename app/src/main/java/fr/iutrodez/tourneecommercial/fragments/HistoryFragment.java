package fr.iutrodez.tourneecommercial.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import fr.iutrodez.tourneecommercial.MainActivity;
import fr.iutrodez.tourneecommercial.R;
import org.jetbrains.annotations.NotNull;

public class HistoryFragment extends Fragment {

    public MainActivity parent;

    @Override
    public void onAttach(@NotNull Context context) {
        super.onAttach(context);
        parent = (MainActivity) context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View frag = inflater.inflate(R.layout.test_fragment, container, false);
        TextView text = frag.findViewById(R.id.nom_frag);
        //FIX: CECI EST JUSTE UN TEST, CA DOIT PAS RESTER
        // Get le tocken depuis les SharedPreferences
        String token = parent.getSharedPreferences("user", Context.MODE_PRIVATE).getString("token", "");
        text.setText("Historique\n" + token); // Méthodes pour afficher le token
        return frag;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        Bundle bundle = getArguments();
        String param1 = ":)";
        if (bundle != null) {
            param1 = bundle.getString("param1");
            String param2 = bundle.getString("param2");
            // Utiliser les paramètres comme nécessaire
        }

        // Initialiser les composants
        TextView text = view.findViewById(R.id.nom_frag);
        text.setText(param1);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

}
