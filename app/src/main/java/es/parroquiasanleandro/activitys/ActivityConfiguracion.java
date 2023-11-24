package es.parroquiasanleandro.activitys;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import es.parroquiasanleandro.R;
import es.parroquiasanleandro.Url;
import es.parroquiasanleandro.Usuario;

public class ActivityConfiguracion extends AppCompatActivity {
    Context context = ActivityConfiguracion.this;

    private LinearLayout lnlytConfiguaracionYPrivacidad;
    private LinearLayout lnlytIrConfiguaracion;
    private LinearLayout lnlytIrPrivacidad;
    private LinearLayout lnlytPoliticaPrivacidad;
//    private LinearLayout lnlytIrNotificaciones;
    private LinearLayout lnlytConfiguaracion;
    private LinearLayout lnlytCambiarContraseña;
    private LinearLayout lnlytCerrarSesion;
    private LinearLayout lnlytEliminarUsuario;
    private LinearLayout lnlytPrivacidad;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuracion);

        lnlytConfiguaracionYPrivacidad = findViewById(R.id.lnlytConfiguaracionYPrivacidad);
        lnlytIrConfiguaracion = findViewById(R.id.lnlytIrConfiguaracion);
        lnlytConfiguaracion = findViewById(R.id.lnlytConfiguaracion);
        lnlytIrPrivacidad = findViewById(R.id.lnlytIrPrivacidad);
        lnlytPrivacidad = findViewById(R.id.lnlytPrivacidad);
        lnlytPoliticaPrivacidad = findViewById(R.id.lnlytPoliticaPrivacidad);
//        lnlytIrNotificaciones = findViewById(R.id.lnlytIrNotificaciones);
        lnlytCambiarContraseña = findViewById(R.id.lnlytCambiarContraseña);
        lnlytCerrarSesion = findViewById(R.id.lnlytCerrarSesion);
        lnlytEliminarUsuario = findViewById(R.id.lnlytEliminarUsuario);

        lnlytIrConfiguaracion.setOnClickListener(v -> {
            lnlytConfiguaracionYPrivacidad.setVisibility(View.GONE);
            lnlytConfiguaracion.setVisibility(View.VISIBLE);
            lnlytPrivacidad.setVisibility(View.GONE);
        });

        lnlytIrPrivacidad.setOnClickListener(v -> {
            lnlytConfiguaracionYPrivacidad.setVisibility(View.GONE);
            lnlytConfiguaracion.setVisibility(View.GONE);
            lnlytPrivacidad.setVisibility(View.VISIBLE);
        });

        /*lnlytIrNotificaciones.setOnClickListener(view -> {

        });*/

        lnlytCambiarContraseña.setOnClickListener(v -> {
            Intent intent = new Intent(context, ActivityCambiarInfoUsuario.class);
            intent.putExtra("tipoCambio", ActivityCambiarInfoUsuario.CAMBIAR_PASSWORD);
            startActivity(intent);
        });

        lnlytCerrarSesion.setOnClickListener(v -> {
            Usuario.cerrarSesion(context);
            finish();
        });

        lnlytEliminarUsuario.setOnClickListener(v -> {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
            alertDialog.setTitle("Eliminar Usuario");
            alertDialog.setMessage("¿Estás seguro de que quieres eliminar tu usuario?");
            alertDialog.setIcon(android.R.drawable.ic_dialog_alert);
            alertDialog.setPositiveButton("Eliminar", (dialog, whichButton) -> {
                Intent intent = new Intent(context, ActivityCambiarInfoUsuario.class);
                intent.putExtra("tipoCambio", ActivityCambiarInfoUsuario.ELIMINAR_USUARIO);
                startActivity(intent);
                finish();
            });
            alertDialog.setNegativeButton(android.R.string.no, null).show();
        });

        lnlytPoliticaPrivacidad.setOnClickListener(v -> {
            Intent intent = new Intent(context, ActivityWebView.class);
            intent.putExtra("url", Url.urlPoliticaPrivacidad);
            startActivity(intent);
        });
    }

    @Override
    public void onBackPressed() {
        if(lnlytConfiguaracionYPrivacidad.getVisibility() == View.VISIBLE){
            super.onBackPressed();
        }else{
            lnlytConfiguaracionYPrivacidad.setVisibility(View.VISIBLE);
            lnlytConfiguaracion.setVisibility(View.GONE);
            lnlytPrivacidad.setVisibility(View.GONE);
        }
    }
}