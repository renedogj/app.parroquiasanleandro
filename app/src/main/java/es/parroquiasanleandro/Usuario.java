package es.parroquiasanleandro;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import es.parroquiasanleandro.activitys.ActivityNavigation;
import es.renedogj.fecha.Fecha;

public class Usuario {
    public static final String USUARIOS = "Usuarios";
    public static final String USUARIO = "usuario";
    public static final String ID = "uid";
    public static final String NOMBRE = "nombre";
    public static final String EMAIL = "email";
    public static final String FECHA_NACIMIENTO = "fechaNacimiento";
    public static final String NUMERO_TELEFONO = "numeroTelefono";
    public static final String EMAIL_VERIFIED = "emailVerified";
    public static final String ES_ADMINISTRADOR = "esAdministrador";

    private String id;
    public String nombre;
    public String email;
    //public String numeroTelefono;
    public Fecha fechaNacimiento;
    public String fotoPerfil;
    public boolean emailVerificado;
    private Grupo[] gruposSeguidos;
    public boolean esAdministrador;
    private Grupo[] gruposAdministrados;
    public long idPoliticaPrivacidad;

    /*Constructors*/
    public Usuario() {
    }

    public Usuario(String nombre, String email) {
        this.nombre = nombre;
        this.email = email;
    }

    /*public Usuario(String nombre, String email, String numeroTelefono) {
        this.nombre = nombre;
        this.email = email;
        //this.numeroTelefono = numeroTelefono;
    }*/

    public Usuario(JSONObject jsonUsuario) throws JSONException {
        this.id = jsonUsuario.getString("id");
        this.nombre = jsonUsuario.getString("nombre");
        this.email = jsonUsuario.getString("email");
        this.gruposSeguidos = Grupo.convertirGrupos(jsonUsuario.getJSONArray("grupos"));
        this.fechaNacimiento = Fecha.stringToFecha(jsonUsuario.getString("fecha_nacimiento"), Fecha.FormatosFecha.aaaa_MM_dd);
        String stringFecha = jsonUsuario.getString("fecha_nacimiento");
        if (!stringFecha.equals("null") && !stringFecha.equals("0000-00-00")) {
            this.fechaNacimiento = Fecha.stringToFecha(stringFecha, Fecha.FormatosFecha.aaaa_MM_dd);
        } else {
            this.fechaNacimiento = null;
        }
        this.fotoPerfil = jsonUsuario.getString("foto_perfil");
        //this.numeroTelefono = jsonUsuario.getString("telefono");
        this.emailVerificado = (jsonUsuario.getInt("email_verificado") == 1);
        this.esAdministrador = (jsonUsuario.getInt("esAdministrador") == 1);
        if (esAdministrador) {
            this.gruposAdministrados = Grupo.convertirGrupos(jsonUsuario.getJSONArray("gruposAdministrados"));
        }
        this.idPoliticaPrivacidad = jsonUsuario.getLong("politica_privacidad");
    }

    /*Getters*/
    public String getId() {
        return id;
    }

    public Grupo[] getGruposSeguidos() {
        return gruposSeguidos;
    }

    public Grupo[] getGruposAdministrados() {
        return gruposAdministrados;
    }

    /*Setters*/
    public void setId(String id) {
        this.id = id;
    }

    public void setGruposSeguidos(Grupo[] gruposSeguidos) {
        this.gruposSeguidos = gruposSeguidos;
    }

    public void setGruposAdministrados(Grupo[] categoriasAdministradas) {
        this.gruposAdministrados = categoriasAdministradas;
    }

    /*Funciones*/
    public void addGrupo(Grupo grupo) {
        List<Grupo> list = Arrays.asList(gruposSeguidos);
        List<Grupo> listGrupos = new ArrayList<>(list);
        listGrupos.add(grupo);
        gruposSeguidos = listGrupos.toArray(new Grupo[0]);
    }

    public void removeGrupo(Grupo grupo) {
        List<Grupo> list = Arrays.asList(gruposSeguidos);
        List<Grupo> listGrupos = new ArrayList<>(list);
        listGrupos.remove(grupo);
        gruposSeguidos = listGrupos.toArray(new Grupo[0]);
    }

    public static Usuario actualizarUsuarioDeServidorToLocal(Context context, Activity activity) {
        AtomicReference<Usuario> usuario = new AtomicReference<>(Usuario.recuperarUsuarioLocal(context));
        if (usuario.get().id != null) {
            RequestQueue requestQueue = Volley.newRequestQueue(context);
            requestQueue.add(new StringRequest(Request.Method.POST, Url.actualizarUsuario, result -> {
                try {
                    JSONObject jsonResult = new JSONObject(result);
                    if (!jsonResult.getBoolean("error")) {
                        JSONObject jsonObject = jsonResult.getJSONObject("usuario");
                        usuario.set(new Usuario(jsonObject));
                        usuario.get().guardarUsuarioEnLocal(context);
                    } else {
                        Usuario.borrarUsuarioLocal(context);
                        context.startActivity(new Intent(context, ActivityNavigation.class));
                        activity.finish();
                    }
                } catch (JSONException e) {
                    Toast.makeText(context, "Se ha producido un error al recuperar la información del Usuario", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }, error -> {
                Toast.makeText(context, "Se ha producido un error al conectar con el servidor", Toast.LENGTH_SHORT).show();
            }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> parametros = new HashMap<>();
                    parametros.put("idUsuario", usuario.get().id);
                    return parametros;
                }
            });
        }
        return usuario.get();
    }

    public void guardarUsuarioEnLocal(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(USUARIO, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        if (gruposSeguidos != null && gruposSeguidos.length != 0) {
            Grupo.guardarGruposSeguidosEnLocal(context, gruposSeguidos);
        } else {
            //Si no sigue a ninguna grupo se pone automaticamente la grupo Padre (Avisos Generales)
            Grupo grupo = new Grupo(Grupo.ID_PADRE, Grupo.NOMBRE_PADRE);
            grupo.guardarGrupoSeguidoEnLocal(context);
        }
        if (gruposAdministrados != null && gruposAdministrados.length != 0) {
            Grupo.guardarGruposAdministradosEnLocal(context, gruposAdministrados);
        } else {
            Grupo.guardarGruposAdministradosEnLocal(context, new Grupo[0]);
        }
        editor.putString(ID, id);
        editor.putString(NOMBRE, nombre);
        editor.putString(EMAIL, email);
        if (fechaNacimiento != null) {
            editor.putString(FECHA_NACIMIENTO, fechaNacimiento.toString(Fecha.FormatosFecha.aaaa_MM_dd));
        }
        //editor.putString("fotoPerfil",user.getPhotoUrl());
        //editor.putString(NUMERO_TELEFONO, numeroTelefono);
        editor.putBoolean(EMAIL_VERIFIED, emailVerificado);
        editor.putBoolean(ES_ADMINISTRADOR, esAdministrador);
        editor.apply();
    }

    public static Usuario recuperarUsuarioLocal(Context context) {
        Usuario usuario = new Usuario();
        SharedPreferences sharedPreferences = context.getSharedPreferences(USUARIO, Context.MODE_PRIVATE);
        usuario.id = sharedPreferences.getString(ID, null);
        usuario.nombre = sharedPreferences.getString(NOMBRE, null);
        usuario.email = sharedPreferences.getString(EMAIL, null);

        String stringFecha = sharedPreferences.getString(FECHA_NACIMIENTO, null);
        if (stringFecha != null) {
            usuario.fechaNacimiento = Fecha.stringToFecha(stringFecha, Fecha.FormatosFecha.aaaa_MM_dd);
        } else {
            usuario.fechaNacimiento = null;
        }
        //usuario.numeroTelefono = sharedPreferences.getString(NUMERO_TELEFONO, null);
        usuario.gruposSeguidos = Grupo.recuperarGruposSeguidosDeLocal(context);
        usuario.emailVerificado = sharedPreferences.getBoolean(EMAIL_VERIFIED, false);
        usuario.esAdministrador = sharedPreferences.getBoolean(ES_ADMINISTRADOR, false);
        if (usuario.esAdministrador) {
            usuario.gruposAdministrados = Grupo.recuperarGruposAdministradosDeLocal(context);
        }
        return usuario;
    }

    public static void borrarUsuarioLocal(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(USUARIO, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(ES_ADMINISTRADOR);
        editor.putString(ID, null);
        editor.putString(NOMBRE, null);
        editor.putString(EMAIL, null);
        //editor.putString(NUMERO_TELEFONO, null);
        editor.apply();
        Grupo.vaciarTablasGruposSeguidosYAdministrados(context);
    }

    public boolean esAdminGrupo(String idGrupo) {
        if (esAdministrador) {
            for (Grupo grupo : gruposAdministrados) {
                if (idGrupo.equals(grupo.id)) {
                    return true;
                }
            }
        }
        return false;
    }

    public static long getIdPoliticaPrivacidadActual(Context context) {
        AtomicReference<Long> idPoliticaPrivacidad = new AtomicReference<>();
        if (idPoliticaPrivacidad.get() != null) {
            RequestQueue requestQueue = Volley.newRequestQueue(context);
            requestQueue.add(new JsonObjectRequest(Request.Method.GET, Url.informacionJson, null, (Response.Listener<JSONObject>) result -> {
                try {
                    idPoliticaPrivacidad.set(result.getLong("politicaPrivacidad"));
                } catch (JSONException e) {
                    Toast.makeText(context, "Se ha producido un error al recuperar la información del Usuario", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }, (Response.ErrorListener) error -> {
                Toast.makeText(context, "Se ha producido un error al conectar con el servidor", Toast.LENGTH_SHORT).show();
            }));
        }
        return idPoliticaPrivacidad.get();
    }

    public void aceptarPoliticaPrivacidad(Context context){
        Volley.newRequestQueue(context).add(new StringRequest(Request.Method.POST, Url.actualizarUsuario, result -> {
            Log.d("RESULT",result);
        }, error -> {
            Toast.makeText(context, "Se ha producido un error al conectar con el servidor", Toast.LENGTH_SHORT).show();
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> parametros = new HashMap<>();
                parametros.put("idUsuario", id);
                return parametros;
            }
        });
    }
}
