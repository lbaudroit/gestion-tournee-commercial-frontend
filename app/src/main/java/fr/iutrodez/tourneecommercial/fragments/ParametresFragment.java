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

import fr.iutrodez.tourneecommercial.ActivitePrincipale;
import fr.iutrodez.tourneecommercial.R;

public class ParametresFragment extends Fragment {

    public static ParametresFragment newInstance() {
        return new ParametresFragment();
    }


    public ActivitePrincipale parent;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        parent = (ActivitePrincipale) context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View frag = inflater.inflate(R.layout.fragment_test, container, false);
        TextView text = frag.findViewById(R.id.nom_frag);
        text.setText("Paramètres");
        return frag;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

}
