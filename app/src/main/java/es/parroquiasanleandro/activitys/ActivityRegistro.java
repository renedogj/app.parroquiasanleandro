package es.parroquiasanleandro.activitys;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

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

public class ActivityRegistro extends AppCompatActivity {
    private final Context context = ActivityRegistro.this;

    private EditText etNombre;
    private EditText etCorreoElectronico;
    private EditText etPassword;
    private ImageButton imgBtnShowPassword;
    private EditText etComprobarPassword;
    private Button bttnRegistrarse;
    private LinearLayout linearLayoutIniciarSesion;

    private ActionBar actionBar;

    private String nombre;
    private String email;
    private String password;
    private String comprobarPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        etNombre = findViewById(R.id.etNombre);
        etCorreoElectronico = findViewById(R.id.etCorreoElectronico);
        imgBtnShowPassword = findViewById(R.id.imgBtnShowPassword);
        etPassword = findViewById(R.id.etContraseña);
        etComprobarPassword = findViewById(R.id.etComprobarContraseña);
        bttnRegistrarse = findViewById(R.id.btnRegistrarse);
        linearLayoutIniciarSesion = findViewById(R.id.linearLayoutIniciarSesion);

        actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(false); //True-> mostrar flecha ir atras
            actionBar.setTitle("Registrarse");
        }

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
                registrarUsuario();
            }
        });

        linearLayoutIniciarSesion.setOnClickListener(v -> {
            startActivity(new Intent(getApplicationContext(), ActivityInicarSesion.class));
            finish();
        });
    }

    private void registrarUsuario() {
        Map<String, String> parametros = new HashMap<>();
        parametros.put("nombre", nombre);
        parametros.put("email", email);
        parametros.put("password", password);
        Url.a(context, parametros);

        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(new StringRequest(Request.Method.POST, Url.registrarse, result -> {
            try {
                JSONObject jsonResult = new JSONObject(result);
                if (!jsonResult.getBoolean("error")) {
                    JSONObject jsonObject = jsonResult.getJSONObject("usuario");
                    Usuario usuario = new Usuario(jsonObject);
                    usuario.guardarUsuarioEnLocal(context);
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
                return parametros;
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(context, ActivityInicarSesion.class));
    }
}