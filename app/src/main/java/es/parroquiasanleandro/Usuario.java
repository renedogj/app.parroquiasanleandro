package es.parroquiasanleandro;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import es.parroquiasanleandro.activitys.ActivityWebView;
import es.parroquiasanleandro.utils.VolleyCallBack;
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
    public static final String ID_POLITICA_PRIVACIDAD = "idPoliticaPrivacidad";

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

    public Usuario(JSONObject jsonUsuario) throws JSONException {
        this.id = jsonUsuario.getString("id");
        this.nombre = jsonUsuario.getString("nombre");
        this.email = jsonUsuario.getString("email");
        this.gruposSeguidos = Grupo.convertirGrupos(jsonUsuario.getJSONArray("grupos"));
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

    public static Usuario actualizarUsuarioDeServidorToLocal(Context context, Activity activity){
        return actualizarUsuarioDeServidorToLocal(context, activity, (isSuccess) -> {});
    }

    public static Usuario actualizarUsuarioDeServidorToLocal(Context context, Activity activity, final VolleyCallBack callBack) {
        AtomicReference<Usuario> atRefUsuario = new AtomicReference<>(Usuario.recuperarUsuarioLocal(context));
        if (atRefUsuario.get().id != null) {
            RequestQueue requestQueue = Volley.newRequestQueue(context);
            requestQueue.add(new StringRequest(Request.Method.POST, Url.actualizarUsuario, result -> {
                try {
                    JSONObject jsonResult = new JSONObject(result);
                    if (!jsonResult.getBoolean("error")) {
                        JSONObject jsonObject = jsonResult.getJSONObject("usuario");
                        Usuario usuario = new Usuario(jsonObject);
                        usuario.comprobarPoliticaDePrivacidad(context,activity);
                        atRefUsuario.set(usuario);
                        atRefUsuario.get().guardarUsuarioEnLocal(context);
                        callBack.onSuccess(true);
                    } else {
                        Toast.makeText(context, "Se ha producido un error al actualizar la informacion del usuario", Toast.LENGTH_SHORT).show();
                        Usuario.borrarUsuarioLocal(context);
                        context.startActivity(new Intent(context, ActivityNavigation.class));
                        callBack.onSuccess(false);
                        activity.finish();
                    }
                } catch (JSONException e) {
                    Toast.makeText(context, "Se ha producido un error al recuperar la información del Usuario", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                    callBack.onSuccess(false);
                }
            }, error -> {
                Toast.makeText(context, "Se ha producido un error al conectar con el servidor", Toast.LENGTH_SHORT).show();
            }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> parametros = new HashMap<>();
                    parametros.put("idUsuario", atRefUsuario.get().id);
                    return parametros;
                }
            });
        }
        return atRefUsuario.get();
    }

    public void comprobarPoliticaDePrivacidad(Context context, Activity activity) {
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(new JsonObjectRequest(Request.Method.GET, Url.informacionJson, null, (Response.Listener<JSONObject>) result -> {
            try {
                long idPoliticaPrivacidad = result.getLong("politicaPrivacidad");
                if (this.getId() != null) {
                    if (idPoliticaPrivacidad > this.idPoliticaPrivacidad) {
                        Intent intent = new Intent(context, ActivityWebView.class);
                        intent.putExtra("url", Url.urlPoliticaPrivacidad);
                        intent.putExtra("soloVisualizarPoliticas", false);
                        context.startActivity(intent);
                        activity.finish();
                    }
                }
            } catch (JSONException e) {
                Toast.makeText(context, "Se ha producido un error al recuperar la información del Usuario", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        }, error -> {
            Toast.makeText(context, "Se ha producido un error al conectar con el servidor", Toast.LENGTH_SHORT).show();
        }));
    }

    public void aceptarPoliticaPrivacidad(Context context, Activity activity){
        Volley.newRequestQueue(context).add(new StringRequest(Request.Method.POST, Url.aceptarPoliticaPrivacidad, result -> {
            try {
                JSONObject jsonResult = new JSONObject(result);
                if (!jsonResult.getBoolean("error")) {
                    context.startActivity(new Intent(context, ActivityNavigation.class));
                    activity.finish();
                }else{
                    Toast.makeText(context, "Se ha producido un error al aceptar la politica de privacidad 1", Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                Toast.makeText(context, "Se ha producido un error al aceptar la politica de privacidad 2", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        }, error -> Toast.makeText(context, "Se ha producido un error al conectar con el servidor", Toast.LENGTH_SHORT).show()) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> parametros = new HashMap<>();
                parametros.put("idUsuario", id);
                return parametros;
            }
        });
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
        }else{
            editor.putString(FECHA_NACIMIENTO, null);
        }
        //editor.putString("fotoPerfil",user.getPhotoUrl());
        //editor.putString(NUMERO_TELEFONO, numeroTelefono);
        editor.putBoolean(EMAIL_VERIFIED, emailVerificado);
        editor.putBoolean(ES_ADMINISTRADOR, esAdministrador);
        editor.putLong(ID_POLITICA_PRIVACIDAD, idPoliticaPrivacidad);
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
        usuario.idPoliticaPrivacidad = sharedPreferences.getLong(ID_POLITICA_PRIVACIDAD, 0);
        return usuario;
    }

    public static void borrarUsuarioLocal(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(USUARIO, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(ES_ADMINISTRADOR);
        editor.putString(ID, null);
        editor.putString(NOMBRE, null);
        editor.putString(EMAIL, null);
        editor.putString(FECHA_NACIMIENTO, null);
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

    public static void cerrarSesion(Context context){
        ActivityNavigation.navView.getMenu().getItem(Menu.CERRAR_SESION).setVisible(false);
        Usuario.borrarUsuarioLocal(context);
        Toast.makeText(context, "Se ha cerrado sesión", Toast.LENGTH_SHORT).show();
        context.startActivity(new Intent(context, ActivityNavigation.class));
    }
}
