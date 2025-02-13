package fr.iutrodez.tourneecommercial.fragments;

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

public class PasswordModificationFragment extends Fragment {
    private final static ApiRequest API_REQUEST = ApiRequest.getInstance();
    private final static String PASSWORD_PATTERN = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@#$%^&+=_]).+$";
    private EditText newPassword;
    private EditText confirmPassword;

    public PasswordModificationFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.modify_password_fragment, container, false);
        newPassword = view.findViewById(R.id.editText_password);
        confirmPassword = view.findViewById(R.id.editText_passwordConfirmation);
        view.findViewById(R.id.button_modify).setOnClickListener(this::modifier);
        return view;
    }

    private void modifier(View view) {
        if (checkPassword(newPassword.getText().toString())
            && checkPasswordConfirmation(
                    newPassword.getText().toString(),
                confirmPassword.getText().toString())) {
            API_REQUEST.utilisateur.updatePassword(requireContext(), newPassword.getText().toString(),
                    response -> Toast.makeText(requireContext(), response, Toast.LENGTH_LONG).show(),
                    error -> Toast.makeText(requireContext(), R.string.modify_password_error, Toast.LENGTH_LONG).show());
        }
    }

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
