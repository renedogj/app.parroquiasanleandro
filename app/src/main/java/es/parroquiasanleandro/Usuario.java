package es.parroquiasanleandro;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.util.Log;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class Usuario {
	public static final String USUARIOS = "Usuarios";
	public static final String USUARIO = "usuario";
	public static final String UID = "uid";
	public static final String NOMBRE = "nombre";
	public static final String EMAIL = "email";
	public static final String MILLIS_FECHA_NACIMIENTO = "millisfechaNacimiento";
	public static final String NUMERO_TELEFONO = "numeroTelefono";
	public static final String EMAIL_VERIFIED = "emailVerified";
	public static final String ES_ADMINISTRADOR = "esAdministrador";

	public String uid;
	public String nombre;
	public String email;
	public HashMap<String, String> suscripciones;
	private Categoria[] categorias;
	public long fechaNacimiento;
	public Uri fotoPerfil;
	public String numeroTelefono;
	//public boolean emailVerified;
	public boolean esAdministrador;
	public HashMap<String, String> administraciones;
	private Categoria[] categoriasAdministradas;

	public Usuario() {

	}

	public Usuario(String nombre, String email) {
		this.nombre = nombre;
		this.email = email;
		this.suscripciones = new HashMap<>();
	}

	public Usuario(String nombre, String email, String numeroTelefono) {
		this.nombre = nombre;
		this.email = email;
		this.numeroTelefono = numeroTelefono;
		this.suscripciones = new HashMap<>();
	}

	public Categoria[] getCategorias() {
		return categorias;
	}

	public Categoria[] getCategoriasAdministradas() {
		return categoriasAdministradas;
	}

	public void setCategorias(Categoria[] categorias) {
		this.categorias = categorias;
	}

	public void setCategoriasAdministradas(Categoria[] categoriasAdministradas) {
		this.categoriasAdministradas = categoriasAdministradas;
	}

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

	public static void actualizarUsuarioLocal(Context context, FirebaseUser user) {
		FirebaseDatabase.getInstance().getReference(USUARIOS).child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
			@Override
			public void onDataChange(@NotNull DataSnapshot dataSnapshot) {
				Usuario usuario = dataSnapshot.getValue(Usuario.class);
				SharedPreferences sharedPreferences = context.getSharedPreferences(USUARIO, Context.MODE_PRIVATE);
				SharedPreferences.Editor editor = sharedPreferences.edit();

				if (usuario != null) {
					if (usuario.suscripciones != null) {
						usuario.categorias = Categoria.convertirCategoria(usuario.suscripciones.keySet().toArray(new String[0]), usuario.suscripciones.values().toArray(new String[0]));
						Categoria.guardarCategoriasSuscritasLocal(context, usuario.categorias);
					} else {
						Categoria categoria = new Categoria(Categoria.ID_PADRE, Categoria.NOMBRE_PADRE);
						FirebaseDatabase.getInstance().getReference(USUARIOS).child(user.getUid()).child(Categoria.SUSCRIPCIONES).child(Categoria.ID_PADRE).setValue(Categoria.NOMBRE_PADRE);
						categoria.guardarSuscripcionLocal(context);
					}
					if (usuario.administraciones != null) {
						usuario.categoriasAdministradas = Categoria.convertirCategoria(usuario.administraciones.keySet().toArray(new String[0]), usuario.administraciones.values().toArray(new String[0]));
						Categoria.guardarCategoriasAdministradasLocal(context, usuario.categoriasAdministradas);
					}
					editor.putBoolean(ES_ADMINISTRADOR, usuario.esAdministrador);
				}

				editor.putString(UID, user.getUid());
				editor.putString(NOMBRE, user.getDisplayName());
				editor.putString(EMAIL, user.getEmail());
				assert usuario != null;
				editor.putLong(MILLIS_FECHA_NACIMIENTO, usuario.fechaNacimiento);
				//editor.putString("fotoPerfil",user.getPhotoUrl());
				editor.putString(NUMERO_TELEFONO, user.getPhoneNumber());
				//editor.putBoolean(EMAIL_VERIFIED, user.isEmailVerified());
				editor.apply();
			}

			@Override
			public void onCancelled(@NotNull DatabaseError databaseError) {
				Log.w("DatabaseError", "loadPost:onCancelled", databaseError.toException());
			}
		});
	}

	public static Usuario recuperarUsuarioLocal(Context context) {
		Usuario usuario = new Usuario();
		SharedPreferences sharedPreferences = context.getSharedPreferences(USUARIO, Context.MODE_PRIVATE);
		usuario.uid = sharedPreferences.getString(UID, null);
		usuario.nombre = sharedPreferences.getString(NOMBRE, null);
		usuario.email = sharedPreferences.getString(EMAIL, null);
		usuario.fechaNacimiento = sharedPreferences.getLong(MILLIS_FECHA_NACIMIENTO, 0);
		usuario.numeroTelefono = sharedPreferences.getString(NUMERO_TELEFONO, null);
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
		editor.putString(UID, null);
		editor.putString(NOMBRE, null);
		editor.putString(EMAIL, null);
		editor.putString(NUMERO_TELEFONO, null);
		editor.apply();
	}
}
