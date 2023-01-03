package es.parroquiasanleandro;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
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

public class Usuario {
    public static final String USUARIOS = "Usuarios";
    public static final String USUARIO = "usuario";
    public static final String ID = "uid";
    public static final String NOMBRE = "nombre";
    public static final String EMAIL = "email";
    public static final String MILLIS_FECHA_NACIMIENTO = "millisfechaNacimiento";
    public static final String NUMERO_TELEFONO = "numeroTelefono";
    public static final String EMAIL_VERIFIED = "emailVerified";
    public static final String ES_ADMINISTRADOR = "esAdministrador";

    private String id;
    public String nombre;
    public String email;
    public HashMap<String, String> hashMapGrupos;
    private Grupo[] grupos;
    public long fechaNacimiento;
    public String fotoPerfil;
    //public String numeroTelefono;
    //public boolean emailVerified;
    public boolean esAdministrador;
    public HashMap<String, String> hashMapGruposAdministrados;
    private Grupo[] gruposAdministrados;

    /*Constructors*/
    public Usuario() {
    }

    public Usuario(String nombre, String email) {
        this.nombre = nombre;
        this.email = email;
        this.hashMapGrupos = new HashMap<>();
    }

    public Usuario(String nombre, String email, String numeroTelefono) {
        this.nombre = nombre;
        this.email = email;
        //this.numeroTelefono = numeroTelefono;
        this.hashMapGrupos = new HashMap<>();
    }

    public Usuario(JSONObject jsonUsuario) throws JSONException {
        this.id = jsonUsuario.getString("id");
        this.nombre = jsonUsuario.getString("nombre");
        this.email = jsonUsuario.getString("email");
        //this.suscripciones = jsonUsuario.getString("suscripciones");
        //this.grupos = jsonUsuario.getString("grupos");
        this.fechaNacimiento = jsonUsuario.getLong("fecha_nacimiento");
        this.fotoPerfil = jsonUsuario.getString("foto_perfil");
        //this.numeroTelefono = jsonUsuario.getString("telefono");
        this.esAdministrador = jsonUsuario.getBoolean("esAdministrador");
        //this.administraciones = jsonUsuario.getString("administraciones");
        //this.categoriasAdministradas = jsonUsuario.getString("categoriasAdministradas");
    }

    /*Getters*/
    public String getId() {
        return id;
    }

    public Grupo[] getGrupos() {
        return grupos;
    }

    public Grupo[] getGruposAdministrados() {
        return gruposAdministrados;
    }

    /*Setters*/
    public void setId(String id) {
        this.id = id;
    }

    public void setGrupos(Grupo[] grupos) {
        this.grupos = grupos;
    }

    public void setGruposAdministrados(Grupo[] categoriasAdministradas) {
        this.gruposAdministrados = categoriasAdministradas;
    }

    /*Funciones*/
    public void addGrupo(Grupo grupo) {
        List<Grupo> list = Arrays.asList(grupos);
        List<Grupo> listGrupos = new ArrayList<>(list);
        listGrupos.add(grupo);
        grupos = listGrupos.toArray(new Grupo[0]);
    }

    public void removeGrupos(Grupo grupo) {
        List<Grupo> list = Arrays.asList(grupos);
        List<Grupo> listGrupos = new ArrayList<>(list);
        listGrupos.remove(grupo);
        grupos = listGrupos.toArray(new Grupo[0]);
    }

    public static Usuario actualizarUsuarioDeServidorToLocal(Context context) {
        AtomicReference<Usuario> usuario = new AtomicReference<>(Usuario.recuperarUsuarioLocal(context));
        if (usuario.get().id != null) {
            RequestQueue requestQueue = Volley.newRequestQueue(context);
            requestQueue.add(new StringRequest(Request.Method.POST, Url.actualizarUsuario, result -> {
                try {
                    JSONObject jsonResult = new JSONObject(result);
                    if (!jsonResult.getBoolean("error")) {
                        JSONObject jsonObject = jsonResult.getJSONArray("usuario").getJSONObject(0);
                        usuario.set(new Usuario(jsonObject));
                        usuario.get().guardarUsuarioLocal(context);
                    }
                } catch (JSONException e) {
                    Toast.makeText(context, "Se ha producido un error al recuperar la información del servidor", Toast.LENGTH_SHORT).show();
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

    public void guardarUsuarioLocal(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(USUARIO, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        if (hashMapGrupos != null) {
            grupos = Grupo.convertirGrupo(hashMapGrupos.keySet().toArray(new String[0]), hashMapGrupos.values().toArray(new String[0]));
            Grupo.guardarGruposSeguidosEnLocal(context, grupos);
        } else {
            //Si no sigue a ninguna grupo se pone automaticamente la grupo Padre (Avisos Generales)
            Grupo grupo = new Grupo(Grupo.ID_PADRE, Grupo.NOMBRE_PADRE);
            grupo.guardarGrupoSeguidoEnLocal(context);
        }
        if (hashMapGruposAdministrados != null) {
            gruposAdministrados = Grupo.convertirGrupo(hashMapGruposAdministrados.keySet().toArray(new String[0]), hashMapGruposAdministrados.values().toArray(new String[0]));
            Grupo.guardarGruposAdministradosEnLocal(context, gruposAdministrados);
        }
        editor.putBoolean(ES_ADMINISTRADOR, esAdministrador);

        editor.putString(ID, id);
        editor.putString(NOMBRE, nombre);
        editor.putString(EMAIL, email);
        editor.putLong(MILLIS_FECHA_NACIMIENTO, fechaNacimiento);
        //editor.putString("fotoPerfil",user.getPhotoUrl());
        //editor.putString(NUMERO_TELEFONO, numeroTelefono);
        //editor.putBoolean(EMAIL_VERIFIED, user.isEmailVerified());
        editor.apply();
    }

    public static Usuario recuperarUsuarioLocal(Context context) {
        Usuario usuario = new Usuario();
        SharedPreferences sharedPreferences = context.getSharedPreferences(USUARIO, Context.MODE_PRIVATE);
        usuario.id = sharedPreferences.getString(ID, null);
        usuario.nombre = sharedPreferences.getString(NOMBRE, null);
        usuario.email = sharedPreferences.getString(EMAIL, null);
        usuario.fechaNacimiento = sharedPreferences.getLong(MILLIS_FECHA_NACIMIENTO, 0);
        //usuario.numeroTelefono = sharedPreferences.getString(NUMERO_TELEFONO, null);
        usuario.grupos = Grupo.recuperarGruposSeguidosDeLocal(context);
        //usuario.emailVerified = sharedPreferences.getBoolean(EMAIL_VERIFIED, false);
        usuario.esAdministrador = sharedPreferences.getBoolean(ES_ADMINISTRADOR, false);
        if (usuario.esAdministrador) {
            usuario.gruposAdministrados = Grupo.recuperarGruposAdministradosDeLocal(context);
        }
        return usuario;
    }

    public static void borrarUsuarioLocal(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(USUARIO, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Grupo.vaciarTablasGruposSeguidosYAdministrados(context);
        editor.remove(ES_ADMINISTRADOR);
        editor.putString(ID, null);
        editor.putString(NOMBRE, null);
        editor.putString(EMAIL, null);
        //editor.putString(NUMERO_TELEFONO, null);
        editor.apply();
    }
}
