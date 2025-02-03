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
import fr.iutrodez.tourneecommercial.utils.api.ApiRequest;

public class MapFragment extends Fragment {

    public MainActivity parent;

    public static MapFragment newInstance() {
        return new MapFragment();
    }
    private ApiRequest API_REQUEST = ApiRequest.getInstance();

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        parent = (MainActivity) context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View frag = inflater.inflate(R.layout.test_fragment, container, false);
        TextView text = frag.findViewById(R.id.nom_frag);
        prepareItineraireMap(text);

        text.setText(R.string.bottom_bar_map);
        return frag;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private void prepareItineraireMap(TextView text){
        Bundle args = getArguments();
        if (args != null && args.containsKey("id")) {
            long id = args.getLong("id");
            API_REQUEST.itineraire.getOne(parent,id, response -> {
                text.setText(response.getNom());

            },error -> {});


            }
    }

}
