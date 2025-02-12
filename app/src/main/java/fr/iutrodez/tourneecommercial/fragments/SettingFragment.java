package fr.iutrodez.tourneecommercial.fragments;

import static fr.iutrodez.tourneecommercial.utils.helper.ViewHelper.disableView;
import static fr.iutrodez.tourneecommercial.utils.helper.ViewHelper.setVisibilityFor;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import fr.iutrodez.tourneecommercial.R;
import fr.iutrodez.tourneecommercial.utils.FullsizeFetchStatusDisplay;
import fr.iutrodez.tourneecommercial.utils.api.ApiRequest;

import java.util.Objects;

public class SettingFragment extends Fragment {

    /**
     * Expression régulière pour vérifier la validité d'une adresse email.
     */
    private static final String EMAIL_PATTERN = "^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,6}$";

    /**
     * Instance de la classe ApiRequest pour effectuer des requêtes API.
     */
    private static final ApiRequest API_REQUEST = ApiRequest.getInstance();

    /**
     * EditText pour le nom, prénom et email de l'utilisateur.
     */
    private EditText name, firstname, email;

    /**
     * Affichage pour mettre un Progress ou un message d'erreur
     * pendant la récupération des données.
     */
    private FullsizeFetchStatusDisplay status;

    /**
     * Le bouton "modifier"
     */
    private Button modify;

    /**
     * Appelé lors de la création du fragment.
     *
     * @param savedInstanceState Si non-null, ce fragment est reconstruit à partir de cet état précédent.
     */
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /**
     * Appelé lorsque le fragment est attaché à son contexte.
     *
     * @param context Le contexte auquel le fragment est attaché.
     */
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }

    /**
     * Crée et renvoie la vue hiérarchique associée au fragment.
     *
     * @param inflater           L'objet LayoutInflater qui peut être utilisé pour gonfler les vues dans le fragment.
     * @param container          Si non-null, c'est le parent auquel la vue du fragment est attachée.
     * @param savedInstanceState Si non-null, ce fragment est reconstruit à partir de cet état précédent.
     * @return La vue pour l'interface utilisateur du fragment.
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.setting_fragment, container, false);
    }

    /**
     * Appelé après que la vue du fragment a été créée.
     *
     * @param view               La vue renvoyée par onCreateView.
     * @param savedInstanceState Si non-null, ce fragment est reconstruit à partir de cet état précédent.
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        name = view.findViewById(R.id.editText_name);
        firstname = view.findViewById(R.id.editText_firstname);
        email = view.findViewById(R.id.editText_email);

        status = view.findViewById(R.id.fetchStatus_status);
        status.setShowContentFunction(() -> setContentVisibility(View.VISIBLE));
        status.setHideContentFunction(() -> setContentVisibility(View.GONE));

        modify = view.findViewById(R.id.button_modify);
        modify.setOnClickListener(this::modifier);

        status.setLoading();
        API_REQUEST.utilisateur.getSelf(getContext(), response -> {
            status.hide();

            name.setText(response.getNom());
            firstname.setText(response.getPrenom());
            email.setText(response.getEmail());
        }, error -> {
            status.setError(R.string.fetching_params_error);

            disableView(name);
            disableView(firstname);
            disableView(email);
            disableView(modify);
        });
    }

    /**
     * Méthode appelée lors du clic sur le bouton de modification.
     *
     * @param view La vue qui a été cliquée.
     */
    public void modifier(View view) {
        if (checkFields()) {
            API_REQUEST.utilisateur.updateSelf(getContext(), name.getText().toString(), firstname.getText().toString(), email.getText().toString(), response -> {
                Toast.makeText(getContext(), response, Toast.LENGTH_LONG).show();
                requireContext().getSharedPreferences("user", Context.MODE_PRIVATE).edit().putString("email", email.getText().toString()).apply();
            }, error -> Toast.makeText(getContext(), R.string.save_params_error, Toast.LENGTH_LONG).show());
        }
    }

    /**
     * Vérifie la validité des champs de saisie.
     *
     * @return true si tous les champs sont valides, false sinon.
     */
    private boolean checkFields() {
        return checkName(name.getText().toString()) &
                checkFirstname(firstname.getText().toString()) &
                checkEmail(email.getText().toString());
    }

    /**
     * Vérifie la validité du nom.
     *
     * @param name Le nom à vérifier.
     * @return true si le nom est valide, false sinon.
     */
    private boolean checkName(String name) {
        if (name.trim().isEmpty()) {
            this.name.setError(getString(R.string.empty_field_error));
            return false;
        }
        return true;
    }

    /**
     * Vérifie la validité du prénom.
     *
     * @param firstname Le prénom à vérifier.
     * @return true si le prénom est valide, false sinon.
     */
    private boolean checkFirstname(String firstname) {
        if (firstname.trim().isEmpty()) {
            this.firstname.setError(getString(R.string.empty_field_error));
            return false;
        }
        return true;
    }

    /**
     * Vérifie la validité de l'adresse email.
     *
     * @param email L'adresse email à vérifier.
     * @return true si l'adresse email est valide, false sinon.
     */
    private boolean checkEmail(String email) {
        if (email.trim().isEmpty()) {
            this.email.setError(getString(R.string.empty_field_error));
            return false;
        }
        if (!email.matches(EMAIL_PATTERN)) {
            this.email.setError(getString(R.string.invalid_email_error));
            return false;
        }
        return true;
    }


    /**
     * Met à jour la visibilité de l'ensemble des éléments de contenu du fragment
     *
     * @param visibility un entier parmi {@code View.GONE}, {@code View.VISIBLE}, ou {@code View.INVISIBLE}
     */
    public void setContentVisibility(int visibility) {
        ViewGroup rootLayout = Objects.requireNonNull((ViewGroup) this.getView());
        for (int i = 0 ; i < rootLayout.getChildCount() ; i++) {
            View child = rootLayout.getChildAt(i);
            if (!(child instanceof FullsizeFetchStatusDisplay)) {
                setVisibilityFor(visibility, child);
            }
        }
    }
}