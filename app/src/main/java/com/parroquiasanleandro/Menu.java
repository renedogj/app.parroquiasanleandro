package com.parroquiasanleandro;

import android.content.Context;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.fragment.app.FragmentManager;

import com.google.firebase.auth.FirebaseAuth;

public class Menu {
    public static final int FRAGMENT_INICIO = R.id.nav_fragment_inicio;
    public static final int FRAGMENT_AVISOS = R.id.nav_fragment_avisos;
    public static final int FRAGMENT_INFORMACION = R.id.nav_fragment_informacion;
    public static final int FRAGMENT_PERFIL = R.id.nav_fragment_perfil;
    public static final int CERRAR_SESION = 1;

    public static void selecionarItemMenu(MenuItem item, Context context, FragmentManager fragmentManager) {
        switch (item.getItemId()) {
            case FRAGMENT_INICIO:
                fragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, new FragmentInicio())
                        .addToBackStack(null)
                        .commit();
                break;
            case FRAGMENT_AVISOS:
                fragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, new FragmentAvisosParroquiales())
                        .addToBackStack(null)
                        .commit();
                break;
            case FRAGMENT_INFORMACION:
                fragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, new FragmentInformacion())
                        .addToBackStack(null)
                        .commit();
                break;
            case FRAGMENT_PERFIL:
                fragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, new FragmentPerfil())
                        .addToBackStack(null)
                        .commit();
                break;
            case CERRAR_SESION:
                FirebaseAuth.getInstance().signOut();
                item.setVisible(false);
                Usuario.borrarUsuarioLocal(context);
                Toast.makeText(context, "Se ha cerrado sesión", Toast.LENGTH_SHORT).show();
                break;
        }
    }
}
