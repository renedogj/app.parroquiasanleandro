package com.parroquiasanleandro;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.util.ArraySet;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.parroquiasanleandro.fecha.Fecha;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class Usuario {
    public static final String USUARIO = "usuario";
    public static final String UID = "uid";
    public static final String NOMBRE = "nombre";
    public static final String EMAIL = "email";
    public static final String CATEGORIAS = "categorias";
    public static final String NUMERO_TELEFONO = "numeroTelefono";
    public static final String EMAIL_VERIFIED = "emailVerified";

    public String uid;
    public String nombre;
    public String email;
    public HashMap<String, String> suscripciones;
    public String[] categorias;
    public Fecha fechaNacimiento;
    public Uri fotoPerfil;
    public String numeroTelefono;
    public boolean emailVerified;

    public Usuario() {

    }

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

    public static void guardarUsuarioEnLocal(Context context, Activity activity, FirebaseUser user) {
        FirebaseDatabase.getInstance().getReference("Usuarios").child(user.getUid()).addValueEventListener(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Usuario usuario = dataSnapshot.getValue(Usuario.class);
                Set<String> categorias = new ArraySet<>();
                if (usuario != null && usuario.suscripciones != null) {
                    categorias.addAll(usuario.suscripciones.values());
                }

                SharedPreferences sharedPreferences = context.getSharedPreferences(USUARIO, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(UID, user.getUid());
                editor.putString(NOMBRE, user.getDisplayName());
                editor.putString(EMAIL, user.getEmail());
                //editor.putString("fotoPerfil",user.getPhotoUrl());
                editor.putString(NUMERO_TELEFONO, user.getPhoneNumber());
                editor.putBoolean(EMAIL_VERIFIED, user.isEmailVerified());
                editor.putStringSet(CATEGORIAS, categorias);
                editor.apply();


                context.startActivity(new Intent(context, ActivityNavigation.class));
                activity.finish();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("DatabaseError", "loadPost:onCancelled", databaseError.toException());
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public static Usuario recuperarUsuarioLocal(Context context) {
        Usuario usuario = new Usuario();
        SharedPreferences sharedPreferences = context.getSharedPreferences(Usuario.USUARIO, Context.MODE_PRIVATE);
        usuario.uid = sharedPreferences.getString(Usuario.UID, null);
        usuario.nombre = sharedPreferences.getString(Usuario.NOMBRE, null);
        usuario.email = sharedPreferences.getString(Usuario.EMAIL, null);
        usuario.numeroTelefono = sharedPreferences.getString(Usuario.NUMERO_TELEFONO, null);
        Set<String> categorias = new ArraySet<>();
        usuario.categorias = sharedPreferences.getStringSet(Usuario.CATEGORIAS, categorias).toArray(new String[0]);
        usuario.emailVerified = sharedPreferences.getBoolean(Usuario.EMAIL_VERIFIED, false);

        return usuario;
    }

    public void getCategoriasReales() {
        List<String> categoriasAvisos = new ArrayList<>();
        for (String categoria : categorias) {
            for (int i = 1; i <= categoria.length(); i++) {
                if (!categoriasAvisos.contains(categoria.substring(0, i))) {
                    categoriasAvisos.add(categoria.substring(0, i));
                }
            }
        }
        if (categoriasAvisos.size() == 0) {
            categoriasAvisos.add("0");
        }
        categorias = categoriasAvisos.toArray(new String[0]);
    }
}
