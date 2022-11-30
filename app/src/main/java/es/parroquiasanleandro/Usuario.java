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
	public HashMap<String, String> hashMapCategorias;
	private Categoria[] categorias;
	public long fechaNacimiento;
	public String fotoPerfil;
	//public String numeroTelefono;
	//public boolean emailVerified;
	public boolean esAdministrador;
	public HashMap<String, String> hashMapCategoriasAdministradas;
	private Categoria[] categoriasAdministradas;

	/*Constructors*/
	public Usuario() {}

	public Usuario(String nombre, String email) {
		this.nombre = nombre;
		this.email = email;
		this.hashMapCategorias = new HashMap<>();
	}

	public Usuario(String nombre, String email, String numeroTelefono) {
		this.nombre = nombre;
		this.email = email;
		//this.numeroTelefono = numeroTelefono;
		this.hashMapCategorias = new HashMap<>();
	}

	public Usuario(JSONObject jsonUsuario) throws JSONException {
		this.id = jsonUsuario.getString("id");
		this.nombre = jsonUsuario.getString("nombre");
		this.email = jsonUsuario.getString("email");
		//this.suscripciones = jsonUsuario.getString("suscripciones");
		//this.categorias = jsonUsuario.getString("categorias");
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

	public Categoria[] getCategorias() {
		return categorias;
	}

	public Categoria[] getCategoriasAdministradas() {
		return categoriasAdministradas;
	}

	/*Setters*/
	public void setId(String id) {
		this.id = id;
	}

	public void setCategorias(Categoria[] categorias) {
		this.categorias = categorias;
	}

	public void setCategoriasAdministradas(Categoria[] categoriasAdministradas) {
		this.categoriasAdministradas = categoriasAdministradas;
	}

	/*Funciones*/
	public void addCategoria(Categoria categoria) {
		List<Categoria> list = Arrays.asList(categorias);
		List<Categoria> listCategorias = new ArrayList<>(list);
		listCategorias.add(categoria);
		categorias = listCategorias.toArray(new Categoria[0]);
	}

	public void removeCategoria(Categoria categoria) {
		List<Categoria> list = Arrays.asList(categorias);
		List<Categoria> listCategorias = new ArrayList<>(list);
		listCategorias.remove(categoria);
		categorias = listCategorias.toArray(new Categoria[0]);
	}

	public void guardarUsuarioLocal(Context context) {
		SharedPreferences sharedPreferences = context.getSharedPreferences(USUARIO, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sharedPreferences.edit();

		if (hashMapCategorias != null) {
			categorias = Categoria.convertirCategoria(hashMapCategorias.keySet().toArray(new String[0]), hashMapCategorias.values().toArray(new String[0]));
			Categoria.guardarCategoriasSuscritasLocal(context, categorias);
		} else {
			//Si no sigue a ninguna categoria se pone automaticamente la categoria Padre (Avisos Generales)
			Categoria categoria = new Categoria(Categoria.ID_PADRE, Categoria.NOMBRE_PADRE);
			categoria.guardarSuscripcionLocal(context);
		}
		if (hashMapCategoriasAdministradas != null) {
			categoriasAdministradas = Categoria.convertirCategoria(hashMapCategoriasAdministradas.keySet().toArray(new String[0]), hashMapCategoriasAdministradas.values().toArray(new String[0]));
			Categoria.guardarCategoriasAdministradasLocal(context, categoriasAdministradas);
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
		usuario.categorias = Categoria.recuperarCategoriasSuscritasLocal(context);
		//usuario.emailVerified = sharedPreferences.getBoolean(EMAIL_VERIFIED, false);
		usuario.esAdministrador = sharedPreferences.getBoolean(ES_ADMINISTRADOR, false);
		if (usuario.esAdministrador) {
			usuario.categoriasAdministradas = Categoria.recuperarCategoriasAdministradasLocal(context);
		}
		return usuario;
	}

	public static void borrarUsuarioLocal(Context context) {
		SharedPreferences sharedPreferences = context.getSharedPreferences(USUARIO, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sharedPreferences.edit();
		Categoria.vaciarTablasCategorias(context);
		editor.remove(ES_ADMINISTRADOR);
		editor.putString(ID, null);
		editor.putString(NOMBRE, null);
		editor.putString(EMAIL, null);
		//editor.putString(NUMERO_TELEFONO, null);
		editor.apply();
	}
}
