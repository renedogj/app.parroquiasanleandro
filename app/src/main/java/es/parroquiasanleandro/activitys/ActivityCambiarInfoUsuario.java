package es.parroquiasanleandro.activitys;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
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

public class ActivityCambiarInfoUsuario extends AppCompatActivity {
    public static final int CAMBIAR_EMAIL = 1;
    public static final int CAMBIAR_PASSWORD = 2;
    public static final int CAMBIAR_NOMBRE = 3;
    public static final int CAMBIAR_FECHA = 4;

    private final Context context = ActivityCambiarInfoUsuario.this;

    private Usuario usuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        int tipoCambio = getIntent().getIntExtra("tipoCambio", 0);

        usuario = Usuario.recuperarUsuarioLocal(context);

        switch (tipoCambio) {
            case CAMBIAR_EMAIL:
                cambiarEmail();
                break;
            case CAMBIAR_PASSWORD:
                cambiarPassword();
                break;
            case CAMBIAR_NOMBRE:
                cambiarNombre();
                break;
            case CAMBIAR_FECHA:
                cambiarFecha();
                break;
            default:
                finish();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(context, ActivityNavigation.class));
    }

    public void cambiarEmail() {
        EditText etnuevoCorreoElectronico;
        Button btnGuardarNuevoCorreo;

        setContentView(R.layout.view_cambiar_correo);

        etnuevoCorreoElectronico = findViewById(R.id.etnuevoCorreoElectronico);
        btnGuardarNuevoCorreo = findViewById(R.id.btnGuardarNuevoCorreo);

        btnGuardarNuevoCorreo.setOnClickListener(view1 -> {
            String nuevoEmail = etnuevoCorreoElectronico.getText().toString().trim();
            if (Comprobaciones.comprobarCorreo(context, nuevoEmail)) {
                Volley.newRequestQueue(context).add(new StringRequest(Request.Method.POST, Url.cambiarCorreo, result -> {
                    try {
                        JSONObject jsonResult = new JSONObject(result);
                        if (!jsonResult.getBoolean("error")) {
                            Usuario.actualizarUsuarioDeServidorToLocal(context, this);
                            Toast.makeText(context, "Correo actualizado con exito", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(context, ActivityNavigation.class));
                            finish();
                        } else {
                            JSONObject jsonErrorInfo = jsonResult.getJSONObject("errorInfo");
                            if (jsonErrorInfo.getInt("errorCode") == 23000 && jsonErrorInfo.getInt("code") == 1062) {
                                if (jsonErrorInfo.getString("key").equals("email_UK")) {
                                    Toast.makeText(context, "Ya existe una cuenta con ese correo electronico", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(context, "Se ha producido al actualizar el correo electronico", Toast.LENGTH_SHORT).show();
                            }
                        }
                    } catch (JSONException e) {
                        Toast.makeText(context, "Se ha producido un error en el servidor al actualizar el correo electronico", Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                }, error -> {
                    Toast.makeText(context, "Se ha producido un error al conectar con el servidor", Toast.LENGTH_SHORT).show();
                    error.printStackTrace();
                }) {
                    @Override
                    protected Map<String, String> getParams() {
                        Map<String, String> parametros = new HashMap<>();
                        parametros.put("idUsuario", usuario.getId());
                        parametros.put("email", usuario.email);
                        parametros.put("nuevoEmail", nuevoEmail);
                        return parametros;
                    }
                });
            }
        });
    }

    public void cambiarPassword() {
        EditText etPassword;
        EditText etComprobarPassword;
        Button bttnGuardarPassword;
        ImageButton imgBtnShowPassword;

        setContentView(R.layout.view_cambiar_password);

        etPassword = findViewById(R.id.etContraseña);
        etComprobarPassword = findViewById(R.id.etComprobarContraseña);
        bttnGuardarPassword = findViewById(R.id.bttnGuardarPassword);
        imgBtnShowPassword = findViewById(R.id.imgBtnShowPassword);

        imgBtnShowPassword.setOnClickListener(view1 -> {
            ActivityInicarSesion.changeShowPassword(etPassword, imgBtnShowPassword);
        });

        bttnGuardarPassword.setOnClickListener(v -> {
            String password = etPassword.getText().toString().trim();
            String comprobarPassword = etComprobarPassword.getText().toString().trim();

            if (Comprobaciones.comprobarPassword(context, password, comprobarPassword)) {
                Volley.newRequestQueue(context).add(new StringRequest(Request.Method.POST, Url.cambiarPassord, result -> {
                    try {
                        JSONObject jsonResult = new JSONObject(result);
                        if (!jsonResult.getBoolean("error")) {
                            Toast.makeText(context, "Contraseña actualizada con exito", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(context, ActivityNavigation.class));
                            finish();
                        } else {
                            Toast.makeText(context, "Se ha producido un error al actualizar la contraseña", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        Toast.makeText(context, "Se ha producido un error en el servidor al actualizar la contraseña", Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                }, error -> {
                    Toast.makeText(context, "Se ha producido un error al conectar con el servidor", Toast.LENGTH_SHORT).show();
                    error.printStackTrace();
                }) {
                    @Override
                    protected Map<String, String> getParams() {
                        Map<String, String> parametros = new HashMap<>();
                        parametros.put("idUsuario", usuario.getId());
                        parametros.put("password", password);
                        return parametros;
                    }
                });
            }
        });
    }

    public void cambiarNombre() {
        EditText etnuevoNombre;
        Button btnGuardarNuevoNombre;

        setContentView(R.layout.view_cambiar_nombre);

        etnuevoNombre = findViewById(R.id.etnuevoNombre);
        btnGuardarNuevoNombre = findViewById(R.id.btnGuardarNuevoNombre);

        btnGuardarNuevoNombre.setOnClickListener(view1 -> {
            String nuevoNombre = etnuevoNombre.getText().toString().trim();
            if (Comprobaciones.comprobarNombre(context, nuevoNombre)) {
                Volley.newRequestQueue(context).add(new StringRequest(Request.Method.POST, Url.cambiarNombre, result -> {
                    Log.d("Result", result);
                    try {
                        JSONObject jsonResult = new JSONObject(result);
                        if (!jsonResult.getBoolean("error")) {
                            Usuario.actualizarUsuarioDeServidorToLocal(context, this);
                            Toast.makeText(context, "Nombre actualizado con exito", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(context, ActivityNavigation.class));
                            finish();
                        } else {
                            Toast.makeText(context, "Se ha producido al actualizar el nombre", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        Toast.makeText(context, "Se ha producido un error en el servidor al actualizar el nombre", Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                }, error -> {
                    Toast.makeText(context, "Se ha producido un error al conectar con el servidor", Toast.LENGTH_SHORT).show();
                    error.printStackTrace();
                }) {
                    @Override
                    protected Map<String, String> getParams() {
                        Map<String, String> parametros = new HashMap<>();
                        parametros.put("idUsuario", usuario.getId());
                        parametros.put("nombre", nuevoNombre);
                        return parametros;
                    }
                });
            }
        });
    }

    public void cambiarFecha() {

    }
}