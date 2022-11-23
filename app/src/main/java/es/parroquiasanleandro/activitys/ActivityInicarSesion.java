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

public class ActivityInicarSesion extends AppCompatActivity {
    private final Context context = ActivityInicarSesion.this;
    private static final int INPUTTYPE_TEXT = 0x00000091;
    private static final int INPUTTYPE_PWD = 0x00000081;

    private Button bttnIniciarSesionGoogle;
    private EditText etCorreoElectronico;
    private EditText etContraseña;
    private ImageButton imgButtonShowPassword;
    private Button bttnIniciarSesion;
    private LinearLayout linearLayoutRegistrarse;

    private ActionBar actionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_iniciar_sesion);

        bttnIniciarSesionGoogle = findViewById(R.id.bttnIniciarSesionGoogle);
        etCorreoElectronico = findViewById(R.id.etCorreoElectronico);
        etContraseña = findViewById(R.id.etContraseña);
        imgButtonShowPassword = findViewById(R.id.imgButtonShowPassword);
        bttnIniciarSesion = findViewById(R.id.bttnIniciarSesion);
        linearLayoutRegistrarse = findViewById(R.id.linearLayoutRegistrarse);

        actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("Iniciar sesión");
        }

        imgButtonShowPassword.setOnClickListener(view -> {
            changeShowPassword(etContraseña,imgButtonShowPassword);
        });

        bttnIniciarSesionGoogle.setOnClickListener(v -> {
            Toast.makeText(context,"Inicio de sesion con google desactivado",Toast.LENGTH_SHORT).show();
        });

        bttnIniciarSesion.setOnClickListener(v -> iniciarSesion());

        linearLayoutRegistrarse.setOnClickListener(v -> startActivity(new Intent(context, ActivityRegistro.class)));
    }

    private void iniciarSesion() {
        String email = etCorreoElectronico.getText().toString().trim();
        String password = etContraseña.getText().toString().trim();
        if (!email.equals("") && !password.equals("")) {
            RequestQueue requestQueue = Volley.newRequestQueue(context);
            requestQueue.add(new StringRequest(Request.Method.POST, Url.iniciarSesion, result -> {
                //Log.d("Resultado",result);
                try {
                    JSONObject jsonResult = new JSONObject(result);
                    if(!jsonResult.getBoolean("error")){
                        Log.d("Resultado",jsonResult.getJSONArray("usuario").getJSONObject(0).toString());
                        Log.d("Resultado",jsonResult.getJSONArray("usuario").getJSONObject(0).toString());
                        JSONObject jsonObject = jsonResult.getJSONArray("usuario").getJSONObject(0);
                        Usuario usuario = new Usuario(jsonObject);
                        usuario.guardarUsuarioLocal(context);
                        startActivity(new Intent(context, ActivityNavigation.class));
                        finish();
                    }else{
                        Toast.makeText(context, "Correo o contraseña incorrecta", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    Toast.makeText(context, "Se ha producido un error en el servidor al iniciar sesion", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }, error -> {
                Toast.makeText(context, "Se ha producido un error al conectar con el servidor", Toast.LENGTH_SHORT).show();
            }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String,String> parametros = new HashMap<>();
                    parametros.put("email",email);
                    parametros.put("password",password);
                    return parametros;
                }
            });
        } else {
            Toast.makeText(context, "Es necesario completar todos los campos", Toast.LENGTH_SHORT).show();
        }
    }

    public static void changeShowPassword(@NonNull EditText etContraseña, ImageButton imgButtonShowPassword){
        if(etContraseña.getInputType() == INPUTTYPE_PWD){
            etContraseña.setInputType(INPUTTYPE_TEXT);
            imgButtonShowPassword.setImageResource(R.drawable.eye_24);
        }else{
            etContraseña.setInputType(INPUTTYPE_PWD);
            imgButtonShowPassword.setImageResource(R.drawable.eye_crossed_24);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(context, ActivityNavigation.class));
    }
}