package com.parroquiasanleandro;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class ActivityMenu extends AppCompatActivity {

    Context context = ActivityMenu.this;

    private Button bttonActivityEnviarMensaje;
    private Button bttnActivityAvisos;
    private Button bttnActivityNuevoAviso;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        bttonActivityEnviarMensaje = findViewById(R.id.bttnActivityEnviarMensajes);
        bttnActivityAvisos = findViewById(R.id.bttnActivityAvisos);
        bttnActivityNuevoAviso = findViewById(R.id.bttnActivityNuevoAviso);

        bttonActivityEnviarMensaje.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //startActivity(new Intent(context, ActivityEnviarMensajes.class));
            }
        });

        bttnActivityAvisos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(context, ActivityAvisosParroquiales.class));
            }
        });

        bttnActivityNuevoAviso.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(context, ActivityNuevoAviso.class));
            }
        });
    }
}