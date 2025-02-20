package fr.iutrodez.tourneecommercial.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import fr.iutrodez.tourneecommercial.R;
import fr.iutrodez.tourneecommercial.utils.api.ApiRequest;

import static android.content.Context.MODE_PRIVATE;

/**
 * Fragment permettant de modifier le mot de passe de l'utilisateur.
 */
public class PasswordModificationFragment extends Fragment {
    private final static ApiRequest API_REQUEST = ApiRequest.getInstance();
    private final static String PASSWORD_PATTERN = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@#$%^&+=_]).+$";
    private EditText newPassword;
    private EditText confirmPassword;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.modify_password_fragment, container, false);

        newPassword = view.findViewById(R.id.editText_password);
        confirmPassword = view.findViewById(R.id.editText_passwordConfirmation);
        view.findViewById(R.id.button_modify).setOnClickListener(this::modifier);
        return view;
    }

    /**
     * Si les champs sont valides, met à jour le mot de passe de l'utilisateur.
     * Le met également à jour dans les SharedPreferences et rafraîchit le token.
     * En cas d'échec de la requête API, affiche un message d'erreur.
     *
     * @param view l'élément cliqué
     */
    private void modifier(View view) {
        Context context = requireContext();
        SharedPreferences pref = context.getSharedPreferences("user", MODE_PRIVATE);
        String passwordValue = newPassword.getText().toString();

        if (checkPassword(passwordValue)
                && checkPasswordConfirmation(passwordValue, confirmPassword.getText().toString())) {

            API_REQUEST.utilisateur.updatePassword(requireContext(), newPassword.getText().toString(),
                    response -> {
                        Toast.makeText(context, response, Toast.LENGTH_LONG).show();

                        // On met à jour le mdp dans les SharedPreferences et on rafraîchit le token
                        pref.edit().putString("password", passwordValue).apply();
                        String email = pref.getString("email", "");
                        new Thread(() -> API_REQUEST.auth.refreshToken(requireContext(), email, passwordValue)).start();
                    },
                    error -> Toast.makeText(context, R.string.modify_password_error, Toast.LENGTH_LONG).show());
        }
    }

    /**
     * Vérifie que le mot de passe est non-vide, a une longueur minimale de 8 caractères et respecte
     * le pattern défini.
     *
     * @param password le mot de passe
     * @return true si le mot de passe est valide, false sinon
     */
    private boolean checkPassword(String password) {
        if (password.trim().isEmpty()) {
            this.newPassword.setError(getString(R.string.empty_field_error));
            return false;
        }
        if (password.length() < 8) {
            this.newPassword.setError(getString(R.string.password_length_error));
            return false;
        }
        if (!password.matches(PASSWORD_PATTERN)) {
            this.newPassword.setError(getString(R.string.password_pattern_error));
            return false;
        }
        return true;
    }

    /**
     * Vérifie que la confirmation du mot de passe est non-vide et identique au mot de passe.
     *
     * @param password             le mot de passe
     * @param passwordConfirmation la confirmation du mot de passe
     * @return true si la confirmation est valide, false sinon
     */
    private boolean checkPasswordConfirmation(String password, String passwordConfirmation) {
        if (passwordConfirmation.trim().isEmpty()) {
            this.confirmPassword.setError(getString(R.string.empty_field_error));
            return false;
        }
        if (!password.equals(passwordConfirmation)) {
            this.confirmPassword.setError(getString(R.string.password_confirmation_error));
            return false;
        }
        return true;
    }
}
