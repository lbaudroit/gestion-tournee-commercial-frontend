package fr.iutrodez.tourneecommercial;

import android.os.Bundle;
import android.view.View;
import android.widget.Switch;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

public class CreateClientActivity extends AppCompatActivity{
        private Switch aswitch;
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_create_clients);
            ActionBar actionBar = getSupportActionBar();
            aswitch = findViewById(R.id.statut);
            if (actionBar != null) {
                actionBar.setTitle("Création nouveau client");
            }
        }

        /**
         * Change le statut True client False Prospect
         * @param view
         */
        public void changeStatut(View view) {
            if(aswitch.isChecked()){
                aswitch.setText("Client");
            }else{
                aswitch.setText("Prospect");
            }
        }

        /**
         * enregistrement du client
         * @param view
         * TODO
         */
        public void enregistrer(View view){
            System.out.println("enregistrement");
        }
    }

