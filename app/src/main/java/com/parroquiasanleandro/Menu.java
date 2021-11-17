package com.parroquiasanleandro;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Menu {
    public static final int FRAGMENT_INICIO = R.id.nav_fragment_inicio;
    public static final int FRAGMENT_AVISOS = R.id.nav_fragment_avisos;
    public static final int FRAGMENT_INFORMACION = R.id.nav_fragment_informacion;
    public static final int FRAGMENT_PERFIL = R.id.nav_fragment_perfil;
    public static final int FRAGMENT_CATEGORIAS = 2;
    public static final int CERRAR_SESION = 1;

    public static int selecionarItemMenu(MenuItem item, int idFragmentActual, FirebaseUser user, Activity activity, Context context, FragmentManager fragmentManager, ActionBar actionBar) {
        int itemId = item.getItemId();
        if(itemId != idFragmentActual){
            switch (itemId) {
                case FRAGMENT_INICIO:
                    iniciarFragmentInicio(fragmentManager,actionBar);
                    break;
                case FRAGMENT_AVISOS:
                    iniciarFragmentAvisos(fragmentManager,actionBar);
                    Log.d("AVISOS","AAAAAAAAA");
                    break;
                case FRAGMENT_INFORMACION:
                    iniciarFragmentInformacion(fragmentManager,actionBar);
                    break;
                case FRAGMENT_PERFIL:
                    iniciarFragmentPerfil(user,activity,context,fragmentManager,actionBar);
                    break;
                case CERRAR_SESION:
                    FirebaseAuth.getInstance().signOut();
                    item.setVisible(false);
                    Usuario.borrarUsuarioLocal(context);
                    Toast.makeText(context, "Se ha cerrado sesión", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
        return itemId;
    }

    public static int iniciarFragmentInicio(FragmentManager fragmentManager, ActionBar actionBar){
        fragmentManager.beginTransaction()
                .setReorderingAllowed(true)
                .replace(R.id.fragment_container, FragmentInicio.class, null)
                .addToBackStack(null)
                .commit();

        actionBar.setTitle("Parroquia San Leandro");
        return FRAGMENT_INICIO;
    }

    public static int iniciarFragmentAvisos(FragmentManager fragmentManager, ActionBar actionBar){
        fragmentManager.beginTransaction()
                .setReorderingAllowed(true)
                .replace(R.id.fragment_container, FragmentAvisosParroquiales.class, null)
                .addToBackStack(null)
                .commit();

        actionBar.setTitle("Avisos");
        return FRAGMENT_AVISOS;
    }

    public static int iniciarFragmentInformacion(FragmentManager fragmentManager, ActionBar actionBar){
        fragmentManager.beginTransaction()
                .setReorderingAllowed(true)
                .replace(R.id.fragment_container, FragmentInformacion.class, null)
                .addToBackStack(null)
                .commit();

        actionBar.setTitle("Información");
        return FRAGMENT_INFORMACION;
    }

    public static int iniciarFragmentPerfil(FirebaseUser user, Activity activity, Context context, FragmentManager fragmentManager, ActionBar actionBar){
        if (user != null) {
            fragmentManager.beginTransaction()
                    .setReorderingAllowed(true)
                    .replace(R.id.fragment_container, FragmentPerfil.class, null)
                    .addToBackStack(null)
                    .commit();

            actionBar.setTitle("Perfil");
        } else {
            activity.startActivity(new Intent(context, ActivityInicarSesion.class));
            activity.finish();
        }
        return FRAGMENT_PERFIL;
    }

    public static int iniciarFragmentCategorias(FragmentManager fragmentManager){
        fragmentManager.beginTransaction()
                .setReorderingAllowed(true)
                .replace(R.id.fragment_container, FragmentCategorias.class, null)
                .addToBackStack(null)
                .commit();

        //actionBar.setTitle("Categorias");
        return FRAGMENT_CATEGORIAS;
    }

    public static MenuItem addCerrarSesion(NavigationView navView){
        return navView.getMenu().add(0, CERRAR_SESION, 0, "Cerrar Sesion");
    }
}
