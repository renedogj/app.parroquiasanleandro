package es.parroquiasanleandro;

import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

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
	public Usuario() {}

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
		//this.esAdministrador = jsonUsuario.getString("esAdministrador");
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

	public void guardarUsuarioLocal(Context context) {
		SharedPreferences sharedPreferences = context.getSharedPreferences(USUARIO, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sharedPreferences.edit();

		if (hashMapGrupos != null) {
			grupos = Grupo.convertirGrupo(hashMapGrupos.keySet().toArray(new String[0]), hashMapGrupos.values().toArray(new String[0]));
			Grupo.guardarGruposSuscritosLocal(context, grupos);
		} else {
			//Si no sigue a ninguna grupo se pone automaticamente la grupo Padre (Avisos Generales)
			Grupo grupo = new Grupo(Grupo.ID_PADRE, Grupo.NOMBRE_PADRE);
			grupo.guardarSuscripcionLocal(context);
		}
		if (hashMapGruposAdministrados != null) {
			gruposAdministrados = Grupo.convertirGrupo(hashMapGruposAdministrados.keySet().toArray(new String[0]), hashMapGruposAdministrados.values().toArray(new String[0]));
			Grupo.guardarGruposAdministradosLocal(context, gruposAdministrados);
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
		usuario.grupos = Grupo.recuperarGruposSuscritosLocal(context);
		//usuario.emailVerified = sharedPreferences.getBoolean(EMAIL_VERIFIED, false);
		usuario.esAdministrador = sharedPreferences.getBoolean(ES_ADMINISTRADOR, false);
		if (usuario.esAdministrador) {
			usuario.gruposAdministrados = Grupo.recuperarGruposAdministradosLocal(context);
		}
		return usuario;
	}

	public static void borrarUsuarioLocal(Context context) {
		SharedPreferences sharedPreferences = context.getSharedPreferences(USUARIO, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sharedPreferences.edit();
		Grupo.vaciarTablasGrupos(context);
		editor.remove(ES_ADMINISTRADOR);
		editor.putString(ID, null);
		editor.putString(NOMBRE, null);
		editor.putString(EMAIL, null);
		//editor.putString(NUMERO_TELEFONO, null);
		editor.apply();
	}
}
