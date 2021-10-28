package com.parroquiasanleandro;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.parroquiasanleandro.fecha.Fecha;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public class Usuario {
    public static final String USUARIOS = "Usuarios";
    public static final String USUARIO = "usuario";
    public static final String UID = "uid";
    public static final String NOMBRE = "nombre";
    public static final String EMAIL = "email";
    public static final String NUMERO_TELEFONO = "numeroTelefono";
    public static final String EMAIL_VERIFIED = "emailVerified";
    public static final String ES_ADMINISTRADOR = "esAdministrador";

    public String uid;
    public String nombre;
    public String email;
    public HashMap<String, String> suscripciones;
    Categoria[] categorias;
    public Fecha fechaNacimiento;
    public Uri fotoPerfil;
    public String numeroTelefono;
    //public boolean emailVerified;
    public boolean esAdministrador;
    public HashMap<String, String> administraciones ;
    Categoria[] categoriasAdministradas;

    public Usuario() {}

    public Usuario(String uid, String nombre, String email, Fecha fechaNacimiento, Uri fotoPerfil, String numeroTelefono) {
        this.uid = uid;
        this.nombre = nombre;
        this.email = email;
        this.fechaNacimiento = fechaNacimiento;
        this.fotoPerfil = fotoPerfil;
        this.numeroTelefono = numeroTelefono;
    }

    public Usuario(String nombre, String email) {
        this.nombre = nombre;
        this.email = email;
    }

    public Usuario(String nombre, String email, String numeroTelefono) {
        this.nombre = nombre;
        this.email = email;
        this.numeroTelefono = numeroTelefono;
    }

    public static void actualizarUsuarioLocal(Context context, FirebaseUser user) {
        FirebaseDatabase.getInstance().getReference(USUARIOS).child(user.getUid()).addValueEventListener(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onDataChange(@NotNull DataSnapshot dataSnapshot) {
                Usuario usuario = dataSnapshot.getValue(Usuario.class);
                SharedPreferences sharedPreferences = context.getSharedPreferences(USUARIO, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();

                if (usuario != null) {
                    if(usuario.suscripciones != null){
                        usuario.categorias = Categoria.convertirCategoria(usuario.suscripciones.keySet().toArray(new String[0]),usuario.suscripciones.values().toArray(new String[0]));
                        Categoria.guardarCategoriasLocal(context,usuario.categorias);
                    }
                    if(usuario.categoriasAdministradas != null){
                        usuario.categoriasAdministradas = Categoria.convertirCategoria(usuario.administraciones.keySet().toArray(new String[0]),usuario.administraciones.values().toArray(new String[0]));
                        Categoria.guardarCategoriasAdministradasLocal(context,usuario.categoriasAdministradas);
                    }
                    editor.putBoolean(ES_ADMINISTRADOR, usuario.esAdministrador);
                }

                editor.putString(UID, user.getUid());
                editor.putString(NOMBRE, user.getDisplayName());
                editor.putString(EMAIL, user.getEmail());
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
        usuario.numeroTelefono = sharedPreferences.getString(NUMERO_TELEFONO, null);
        usuario.categorias = Categoria.recuperarCategoriasLocal(context);
        //usuario.emailVerified = sharedPreferences.getBoolean(EMAIL_VERIFIED, false);
        usuario.esAdministrador = sharedPreferences.getBoolean(ES_ADMINISTRADOR, false);
        if(usuario.esAdministrador) {
            usuario.categoriasAdministradas = Categoria.recuperarCategoriasAdministradasLocal(context);
        }
        return usuario;
    }
}
