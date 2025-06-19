package es.parroquiasanleandro.activitys;

import static es.parroquiasanleandro.NotificacionSL.NOTIFICATION_STATUS;
import static es.parroquiasanleandro.NotificacionSL.NOTI_RECORDATORIOS;
import static es.parroquiasanleandro.NotificacionSL.NOTI_RECORDATORIOS_VIBR;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.switchmaterial.SwitchMaterial;

import es.parroquiasanleandro.NotificacionSL;
import es.parroquiasanleandro.R;
import es.parroquiasanleandro.Url;
import es.parroquiasanleandro.Usuario;

public class ActivityConfiguracion extends AppCompatActivity{
    Context context = ActivityConfiguracion.this;

    private LinearLayout lnlytMenuConfiguaracion;
    private LinearLayout lnlytIrConfiguaracion;
    private LinearLayout lnlytIrPrivacidad;
    private LinearLayout lnlytPoliticaPrivacidad;
    private LinearLayout lnlytIrNotificaciones;
    private LinearLayout lnlytConfiguaracion;
    private LinearLayout lnlytCambiarContraseña;
    private LinearLayout lnlytCerrarSesion;
    private LinearLayout lnlytEliminarUsuario;
    private LinearLayout lnlytPrivacidad;

    private LinearLayout lnlytNotificaciones;
    private LinearLayout lnlytNotificacionesActivas;

    private TextView tvStatusNotificaciones;
    private String statusNotificaciones;

    private TextView tvRecordatorios;
    private SwitchMaterial switchRecordatorios;
    private TextView tvVibrRecordatorios;
    private SwitchMaterial switchVibrRecordatorios;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuracion);

        lnlytMenuConfiguaracion = findViewById(R.id.lnlytMenuConfiguaracion);
        lnlytIrConfiguaracion = findViewById(R.id.lnlytIrConfiguaracion);
//        lnlytIrPrivacidad = findViewById(R.id.lnlytIrPrivacidad);
        lnlytIrNotificaciones = findViewById(R.id.lnlytIrNotificaciones);
        lnlytPoliticaPrivacidad = findViewById(R.id.lnlytPoliticaPrivacidad);
//        lnlytPrivacidad = findViewById(R.id.lnlytPrivacidad);
        lnlytConfiguaracion = findViewById(R.id.lnlytConfiguracion);
        lnlytCambiarContraseña = findViewById(R.id.lnlytCambiarContraseña);
        lnlytCerrarSesion = findViewById(R.id.lnlytCerrarSesion);
        lnlytEliminarUsuario = findViewById(R.id.lnlytEliminarUsuario);
        lnlytNotificaciones = findViewById(R.id.lnlytNotificaciones);
        lnlytNotificacionesActivas = findViewById(R.id.lnlytNotificacionesActivas);
        tvStatusNotificaciones = findViewById(R.id.tvStatusNotificaciones);
        tvRecordatorios = findViewById(R.id.tvRecordatorios);
        switchRecordatorios = findViewById(R.id.switchRecordatorios);
        tvVibrRecordatorios = findViewById(R.id.tvVibrRecordatorios);
        switchVibrRecordatorios = findViewById(R.id.switchVibrRecordatorios);


        lnlytIrConfiguaracion.setOnClickListener(v -> {
            lnlytMenuConfiguaracion.setVisibility(View.GONE);
            lnlytConfiguaracion.setVisibility(View.VISIBLE);
            lnlytPrivacidad.setVisibility(View.GONE);
        });

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

        lnlytIrNotificaciones.setOnClickListener(v -> {
            lnlytMenuConfiguaracion.setVisibility(View.GONE);
            lnlytNotificaciones.setVisibility(View.VISIBLE);

            statusNotificaciones = NotificacionSL.getStringInfoNotification(context, NOTIFICATION_STATUS);
            if(statusNotificaciones.equals("")){
                statusNotificaciones = NotificacionSL.changeAllNotificationsStatus(context);
            }
            tvStatusNotificaciones.setText(statusNotificaciones);

            lnlytNotificacionesActivas.setOnClickListener(view -> {
                if(statusNotificaciones.equals(NotificacionSL.STATUS_ACTIVADAS)){
                    MaterialAlertDialogBuilder materialAlertDialogBuilder = new MaterialAlertDialogBuilder(context);
                    materialAlertDialogBuilder.setTitle("¿Seguro quieres desactivar las notificaciones?");

                    materialAlertDialogBuilder.setPositiveButton("Desactivar", (dialog, which) -> {
                        statusNotificaciones = NotificacionSL.changeAllNotificationsStatus(context);
                        tvStatusNotificaciones.setText(statusNotificaciones);
                    });

                    materialAlertDialogBuilder.setNegativeButton("Cancelar", (dialog, which) -> {
                        dialog.cancel();
                    });
                    materialAlertDialogBuilder.show();
                }else if(statusNotificaciones.equals(NotificacionSL.STATUS_DESACTIVADAS)){
                    statusNotificaciones = NotificacionSL.changeAllNotificationsStatus(context);
                    tvStatusNotificaciones.setText(statusNotificaciones);
                }
            });
            
            switchRecordatorios.setChecked(NotificacionSL.getBooleanInfoNotification(context, NOTI_RECORDATORIOS));
            switchRecordatorios.setOnCheckedChangeListener((buttonView, isChecked) -> {
                NotificacionSL.changeInfoNotification(context, NOTI_RECORDATORIOS, isChecked);
                if(isChecked){
                    tvRecordatorios.setText("Recordatorios activados");
                }else {
                    tvRecordatorios.setText("Recordatorios desactivados");
                }
            });

            switchVibrRecordatorios.setChecked(NotificacionSL.getBooleanInfoNotification(context, NOTI_RECORDATORIOS_VIBR));
            switchVibrRecordatorios.setOnCheckedChangeListener((buttonView, isChecked) -> {
                NotificacionSL.changeInfoNotification(context, NOTI_RECORDATORIOS_VIBR, isChecked);
                if(isChecked){
                    tvVibrRecordatorios.setText("Vibración activada");
                }else {
                    tvVibrRecordatorios.setText("Vibración desactivada");
                }
            });
        });
    }

    @Override
    public void onBackPressed() {
        if(lnlytMenuConfiguaracion.getVisibility() == View.VISIBLE){
            super.onBackPressed();
        }else{
            lnlytMenuConfiguaracion.setVisibility(View.VISIBLE);
            lnlytConfiguaracion.setVisibility(View.GONE);
            lnlytPrivacidad.setVisibility(View.GONE);
            lnlytNotificaciones.setVisibility(View.GONE);
        }
    }
}