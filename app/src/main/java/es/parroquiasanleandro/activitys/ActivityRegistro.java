package es.parroquiasanleandro.activitys;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import es.parroquiasanleandro.R;
import es.parroquiasanleandro.Url;
import es.parroquiasanleandro.Usuario;
import es.parroquiasanleandro.utils.Comprobaciones;
import es.renedogj.fecha.Fecha;
import es.renedogj.fecha.Mes;

public class ActivityRegistro extends AppCompatActivity {
    private final Context context = ActivityRegistro.this;

    private EditText etNombre;
    private EditText etCorreoElectronico;
    private EditText etPassword;
    private ImageButton imgBtnShowPassword;
    private EditText etComprobarPassword;
    private LinearLayout lnlytFechaNacimiento;
    private TextView tvFechaNacimiento;
    private CheckBox checkboxPoliticaPrivacidad;
    private TextView tvPoliticaPrivacidad;
    private LinearLayout lnlytAutorizacionPaterna;
    private CheckBox checkboxAutorizacionPaterna;
    private Button bttnRegistrarse;
    private LinearLayout linearLayoutIniciarSesion;

    private ActionBar actionBar;

    private String nombre;
    private String email;
    private String password;
    private String comprobarPassword;

    private Fecha fechaNacimiento;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        etNombre = findViewById(R.id.etNombre);
        etCorreoElectronico = findViewById(R.id.etCorreoElectronico);
        imgBtnShowPassword = findViewById(R.id.imgBtnShowPassword);
        etPassword = findViewById(R.id.etContraseña);
        etComprobarPassword = findViewById(R.id.etComprobarContraseña);
        lnlytFechaNacimiento = findViewById(R.id.lnlytFechaNacimiento);
        tvFechaNacimiento = findViewById(R.id.tvFechaNacimiento);
        checkboxPoliticaPrivacidad = findViewById(R.id.checkboxPoliticaPrivacidad);
        tvPoliticaPrivacidad = findViewById(R.id.tvPoliticaPrivacidad);
        lnlytAutorizacionPaterna = findViewById(R.id.lnlytAutorizacionPaterna);
        checkboxAutorizacionPaterna = findViewById(R.id.checkboxAutorizacionPaterna);
        bttnRegistrarse = findViewById(R.id.btnRegistrarse);
        linearLayoutIniciarSesion = findViewById(R.id.linearLayoutIniciarSesion);

        actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("Registrarse");
        }

        fechaNacimiento = Fecha.FechaActual();
        tvFechaNacimiento.setText(fechaNacimiento.toString(Fecha.FormatosFecha.dd_MM_aaaa));

        imgBtnShowPassword.setOnClickListener(view1 -> {
            ActivityInicarSesion.changeShowPassword(etPassword, imgBtnShowPassword);
        });

        bttnRegistrarse.setOnClickListener(v -> {
            nombre = etNombre.getText().toString().trim();
            email = etCorreoElectronico.getText().toString().trim();
            password = etPassword.getText().toString().trim();
            comprobarPassword = etComprobarPassword.getText().toString().trim();

            if (Comprobaciones.comprobarNombre(context, nombre) &&
                    Comprobaciones.comprobarCorreo(context, email) &&
                    Comprobaciones.comprobarPassword(context, password, comprobarPassword)) {

                if (checkboxPoliticaPrivacidad.isChecked()) {
                    if (!necesitaAutoriazacionPaterna()) {
                        registrarUsuario();
                    } else {
                        lnlytAutorizacionPaterna.setVisibility(View.VISIBLE);
                        if(checkboxAutorizacionPaterna.isChecked()){
                            registrarUsuario();
                        }else{
                            Toast.makeText(context, "Necesitas autorización paterna", Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    Toast.makeText(context, "Es necesario aceptar las politicas de privacidad", Toast.LENGTH_SHORT).show();
                }
            }
        });

        lnlytFechaNacimiento.setOnClickListener(v -> {
            DatePickerDialog datePickerDialog = new DatePickerDialog(context, (view, year, month, dayOfMonth) -> {
                fechaNacimiento.dia = dayOfMonth;
                fechaNacimiento.mes = Mes.values()[month];
                fechaNacimiento.año = year;
                fechaNacimiento.actualizarDiaSemana();
                tvFechaNacimiento.setText(fechaNacimiento.toString(Fecha.FormatosFecha.dd_MM_aaaa));
                comprobarFechaNacimiento();
            }, fechaNacimiento.año, fechaNacimiento.mes.getNumeroMes() - 1, fechaNacimiento.dia);
            datePickerDialog.show();
        });


        tvPoliticaPrivacidad.setOnClickListener(v -> {
            Intent intent = new Intent(context, ActivityWebView.class);
            intent.putExtra("url", Url.urlPoliticaPrivacidad);
            startActivity(intent);
            finish();
        });

        linearLayoutIniciarSesion.setOnClickListener(v -> {
            startActivity(new Intent(getApplicationContext(), ActivityInicarSesion.class));
            finish();
        });
    }

    private void registrarUsuario() {
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(new StringRequest(Request.Method.POST, Url.registrarse, result -> {
            result = result.replace("true{\"", "{\"");
            Log.e("RESULT",result);
            try {
                JSONObject jsonResult = new JSONObject(result);
                if (!jsonResult.getBoolean("error")) {
                    JSONObject jsonObject = jsonResult.getJSONObject("usuario");
                    Usuario usuario = new Usuario(jsonObject);
                    usuario.guardarUsuarioEnLocal(context);
                    Toast.makeText(context, "Se ha enviado un correo de verificación a tu correo electronico", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(context, ActivityNavigation.class));
                    finish();
                } else {
                    JSONObject jsonErrorInfo = jsonResult.getJSONObject("errorInfo");
                    if (jsonErrorInfo.getInt("errorCode") == 23000 && jsonErrorInfo.getInt("code") == 1062) {
                        if (jsonErrorInfo.getString("key").equals("email_UK")) {
                            Toast.makeText(context, "Ya existe una cuenta con ese correo electronico", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Log.e("ERROR AL REGISTRARSE", jsonResult.toString());
                        Toast.makeText(context, "Se ha producido un error al registrarse", Toast.LENGTH_SHORT).show();
                    }
                }
            } catch (JSONException e) {
                Toast.makeText(context, "Se ha producido un error", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        }, error -> {
            Toast.makeText(context, "Se ha producido un error al conectar con el servidor", Toast.LENGTH_SHORT).show();
            error.printStackTrace();
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> parametros = new HashMap<>();
                parametros.put("nombre", nombre);
                parametros.put("email", email);
                parametros.put("password", password);
                parametros.put("fechaNacimiento", fechaNacimiento.toString(Fecha.FormatosFecha.aaaa_MM_dd));
                return parametros;
            }
        });
    }

    public boolean necesitaAutoriazacionPaterna() {
        return !Fecha.diferenciaFechaMayorQueAnnos(fechaNacimiento, Fecha.FechaActual(), 16);
    }

    public void comprobarFechaNacimiento() {
        if (necesitaAutoriazacionPaterna()) {
            lnlytAutorizacionPaterna.setVisibility(View.VISIBLE);
        } else {
            lnlytAutorizacionPaterna.setVisibility(View.GONE);
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        onBackPressed();
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(context, ActivityInicarSesion.class));
    }
}