package fr.iutrodez.tourneecommercial;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class ActiviteConnexion extends AppCompatActivity {

    private EditText email;
    private EditText password;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activite_connexion);
        getSupportActionBar().setTitle(R.string.connexion);

        email = findViewById(R.id.field_email);
        password = findViewById(R.id.field_password);

        findViewById(R.id.btn_connexion).setOnClickListener(this::onClickEnvoyer);
        findViewById(R.id.btn_inscription).setOnClickListener(this::onClickGoToInscription);
    }

    private void onClickEnvoyer(View view) {
        // TODO vérifier les champs et changer de vue si besoin
        startActivity(new Intent(this, ActivitePrincipale.class));

        Toast.makeText(this, R.string.todo, Toast.LENGTH_SHORT).show();
    }

    private void onClickGoToInscription(View view) {
        startActivity(new Intent(this, ActiviteInscription.class));
        Toast.makeText(this, R.string.todo, Toast.LENGTH_SHORT).show();
    }
}
