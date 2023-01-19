package es.parroquiasanleandro.activitys;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
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

public class ActivityCambiarCorreo extends AppCompatActivity {
    private final Context context = ActivityCambiarCorreo.this;

    private EditText etnuevoCorreoElectronico;
    private Button bttnGuardarNuevoCorreo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cambiar_correo);

        etnuevoCorreoElectronico = findViewById(R.id.etnuevoCorreoElectronico);
        bttnGuardarNuevoCorreo = findViewById(R.id.bttnGuardarNuevoCorreo);

        Usuario usuario = Usuario.recuperarUsuarioLocal(context);

        bttnGuardarNuevoCorreo.setOnClickListener(view1 -> {
            String nuevoEmail = etnuevoCorreoElectronico.getText().toString().trim();
            if (Comprobaciones.comprobarCorreo(context, nuevoEmail)) {
                Volley.newRequestQueue(context).add(new StringRequest(Request.Method.POST, Url.cambiarCorreo, result -> {
                	Log.e("RESULT",result);
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
                                Log.e("ERROR AL REGISTRARSE", jsonResult.toString());
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
            } else {
                Toast.makeText(context, "Introduce un correo valido", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(context, ActivityNavigation.class));
    }
}