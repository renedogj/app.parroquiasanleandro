package es.parroquiasanleandro.activitys;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
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
import es.renedogj.fecha.Fecha;
import es.renedogj.fecha.Mes;

public class ActivityCambiarInfoUsuario extends AppCompatActivity {
    public static final int CAMBIAR_EMAIL = 1;
    public static final int CAMBIAR_PASSWORD = 2;
    public static final int CAMBIAR_NOMBRE = 3;
    public static final int CAMBIAR_FECHA = 4;
    public static final int ELIMINAR_USUARIO = 5;

    private final Context context = ActivityCambiarInfoUsuario.this;

    private Usuario usuario;

    private int tipoCambio;

    private Fecha auxfecha;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        tipoCambio = getIntent().getIntExtra("tipoCambio", 0);

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
            case ELIMINAR_USUARIO:
                eliminarUsuario();
                break;
            default:
                finish();
        }
    }

    public void cambiarEmail() {
        EditText etnuevoCorreoElectronico;
        Button btnGuardarNuevoCorreo;

        setContentView(R.layout.view_cambiar_correo);

        etnuevoCorreoElectronico = findViewById(R.id.etnuevoCorreoElectronico);
        btnGuardarNuevoCorreo = findViewById(R.id.btnGuardarNuevoCorreo);

        etnuevoCorreoElectronico.setText(usuario.email);

        btnGuardarNuevoCorreo.setOnClickListener(view1 -> {
            String nuevoEmail = etnuevoCorreoElectronico.getText().toString().trim();
            if (Comprobaciones.comprobarCorreo(context, nuevoEmail)) {
                ProgressDialog progressDialog = new ProgressDialog(context);
                progressDialog.setMessage("Guardando nuevo email...");
                progressDialog.show();

                Volley.newRequestQueue(context).add(new StringRequest(Request.Method.POST, Url.cambiarCorreo, result -> {
                    try {
                        JSONObject jsonResult = new JSONObject(result);
                        if (!jsonResult.getBoolean("error")) {
                            Toast.makeText(context, "Se ha enviado un correo de verificación a tu nuevo correo electronico", Toast.LENGTH_SHORT).show();
                            Usuario.actualizarUsuarioDeServidorToLocal(context, this, (isSuccess) -> {
                                if(isSuccess) {
                                    Toast.makeText(context, "Correo actualizado con exito", Toast.LENGTH_SHORT).show();
                                }
                                progressDialog.dismiss();
                                onBackPressed();
                            });
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
                            onBackPressed();
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

        etnuevoNombre.setText(usuario.nombre);

        btnGuardarNuevoNombre.setOnClickListener(view1 -> {
            String nuevoNombre = etnuevoNombre.getText().toString().trim();
            if (Comprobaciones.comprobarNombre(context, nuevoNombre)) {
                ProgressDialog progressDialog = new ProgressDialog(context);
                progressDialog.setMessage("Guardando nuevo nombre...");
                progressDialog.show();

                Volley.newRequestQueue(context).add(new StringRequest(Request.Method.POST, Url.cambiarNombre, result -> {
                    Log.d("Result", result);
                    try {
                        JSONObject jsonResult = new JSONObject(result);
                        if (!jsonResult.getBoolean("error")) {
                            Usuario.actualizarUsuarioDeServidorToLocal(context, this, (isSuccess) -> {
                                if(isSuccess){
                                    Toast.makeText(context, "Nombre actualizado con exito", Toast.LENGTH_SHORT).show();
                                }
                                progressDialog.dismiss();
                                onBackPressed();
                            });
                        } else {
                            Toast.makeText(context, "Se ha producido un error al actualizar el nombre", Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                            onBackPressed();
                        }
                    } catch (JSONException e) {
                        Toast.makeText(context, "Se ha producido un error en el servidor al actualizar el nombre", Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                        progressDialog.dismiss();
                        onBackPressed();
                    }
                }, error -> {
                    Toast.makeText(context, "Se ha producido un error al conectar con el servidor", Toast.LENGTH_SHORT).show();
                    error.printStackTrace();
                    progressDialog.dismiss();
                    onBackPressed();
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
        TextView tvNuevaFecha;
        Button btnGuardarNuevaFecha;
        LinearLayout lnlytAutorizacionPaterna;
        CheckBox checkboxAutorizacionPaterna;

        setContentView(R.layout.view_cambiar_fecha_nacimiento);

        tvNuevaFecha = findViewById(R.id.tvNuevaFecha);
        btnGuardarNuevaFecha = findViewById(R.id.btnGuardarNuevaFecha);
        lnlytAutorizacionPaterna = findViewById(R.id.lnlytAutorizacionPaterna);
        checkboxAutorizacionPaterna = findViewById(R.id.checkboxAutorizacionPaterna);

        if(usuario.fechaNacimiento != null){
            auxfecha = usuario.fechaNacimiento.clone();
        }else{
            auxfecha = Fecha.FechaActual();
        }

        tvNuevaFecha.setText(auxfecha.toString(Fecha.FormatosFecha.d_MMMM_aaaa));

        tvNuevaFecha.setOnClickListener(v -> {
            DatePickerDialog datePickerDialog = new DatePickerDialog(context, (view, year, month, dayOfMonth) -> {
                usuario.fechaNacimiento = new Fecha(dayOfMonth, Mes.values()[month], year);
                auxfecha = usuario.fechaNacimiento.clone();
                tvNuevaFecha.setText(auxfecha.toString(Fecha.FormatosFecha.d_MMMM_aaaa));
                if(!Fecha.diferenciaFechaMayorQueAnnos(auxfecha, Fecha.FechaActual(), 16)){
                    lnlytAutorizacionPaterna.setVisibility(View.VISIBLE);
                } else {
                    lnlytAutorizacionPaterna.setVisibility(View.GONE);
                }
            }, auxfecha.año, auxfecha.mes.getNumeroMes() - 1, auxfecha.dia);
            datePickerDialog.show();
        });

        btnGuardarNuevaFecha.setOnClickListener(v -> {
            if (Fecha.diferenciaFechaMayorQueAnnos(auxfecha, Fecha.FechaActual(), 16)) {
                guardarFechaNacimiento();
            } else {
                lnlytAutorizacionPaterna.setVisibility(View.VISIBLE);
                if(checkboxAutorizacionPaterna.isChecked()){
                    guardarFechaNacimiento();
                }else{
                    Toast.makeText(context, "Si tienes menos de 16 años necesitas autorización paterna para utilizar esta aplicación", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void eliminarUsuario(){
        Button btnEliminarUsuario;

        setContentView(R.layout.view_eliminar_usuario);

        btnEliminarUsuario = findViewById(R.id.btnEliminarUsuario);

        btnEliminarUsuario.setOnClickListener(v -> {
            Volley.newRequestQueue(context).add(new StringRequest(Request.Method.POST, Url.eliminarUsuario, result -> {
                try {
                    JSONObject jsonResult = new JSONObject(result);
                    if (!jsonResult.getBoolean("error")) {
                        Usuario.borrarUsuarioLocal(context);
                        Toast.makeText(context, "Usuario eliminado con exito", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(context, ActivityNavigation.class));
                        finish();
                    } else {
                        Toast.makeText(context, "Se ha producido al eliminar el usuario", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    Toast.makeText(context, "Se ha producido un error en el servidor al eliminar el usuario", Toast.LENGTH_SHORT).show();
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
                    return parametros;
                }
            });
        });
    }

    public void guardarFechaNacimiento(){
        ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Guardando fecha de nacimiento...");
        progressDialog.show();

        Volley.newRequestQueue(context).add(new StringRequest(Request.Method.POST, Url.cambiarFechaNacimiento, result -> {
            Log.d("Result", result);
            try {
                JSONObject jsonResult = new JSONObject(result);
                if (!jsonResult.getBoolean("error")) {
                    Usuario.actualizarUsuarioDeServidorToLocal(context, this, (isSuccess) -> {
                        if(isSuccess){
                            Toast.makeText(context, "Fecha de nacimiento actualizado con exito", Toast.LENGTH_SHORT).show();
                        }
                        progressDialog.dismiss();
                        onBackPressed();
                    });
                } else {
                    Toast.makeText(context, "Se ha producido un error al actualizar la fecha de nacimiento", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                    onBackPressed();
                }
            } catch (JSONException e) {
                Toast.makeText(context, "Se ha producido un error en el servidor al actualizar la fecha de nacimiento", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
                progressDialog.dismiss();
                onBackPressed();
            }
        }, error -> {
            Toast.makeText(context, "Se ha producido un error al conectar con el servidor", Toast.LENGTH_SHORT).show();
            error.printStackTrace();
            progressDialog.dismiss();
            onBackPressed();
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> parametros = new HashMap<>();
                parametros.put("idUsuario", usuario.getId());
                parametros.put("fechaNacimiento", usuario.fechaNacimiento.toString(Fecha.FormatosFecha.aaaa_MM_dd));
                return parametros;
            }
        });
    }
}