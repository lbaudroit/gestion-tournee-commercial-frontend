package fr.iutrodez.tourneecommercial;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class ActiviteInscription extends AppCompatActivity {

    private EditText email;
    private EditText password;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activite_inscription);
        getSupportActionBar().setTitle(R.string.inscription);

        email = findViewById(R.id.field_email);
        password = findViewById(R.id.field_password);

        findViewById(R.id.btn_inscription).setOnClickListener(this::onClickEnvoyer);
        findViewById(R.id.btn_connexion).setOnClickListener(this::onClickGoToConnexion);
    }

    private void onClickEnvoyer(View view) {
        // TODO vérifier les champs et changer de vue si besoin
        startActivity(new Intent(this, ActivitePrincipale.class));

        Toast.makeText(this, R.string.todo, Toast.LENGTH_SHORT).show();
    }

    private void onClickGoToConnexion(View view) {
        startActivity(new Intent(this, ActiviteConnexion.class));
        Toast.makeText(this, R.string.todo, Toast.LENGTH_SHORT).show();
    }
}
