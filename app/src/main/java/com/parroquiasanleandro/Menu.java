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
import com.parroquiasanleandro.activitys.ActivityInicarSesion;
import com.parroquiasanleandro.activitys.ActivityNavigation;
import com.parroquiasanleandro.fragments.FragmentAvisosParroquiales;
import com.parroquiasanleandro.fragments.FragmentCalendario;
import com.parroquiasanleandro.fragments.FragmentCategorias;
import com.parroquiasanleandro.fragments.FragmentInformacion;
import com.parroquiasanleandro.fragments.FragmentInicio;
import com.parroquiasanleandro.fragments.FragmentPerfil;

public class Menu {
    public static final int FRAGMENT_INICIO = R.id.nav_fragment_inicio;
    public static final int FRAGMENT_AVISOS = R.id.nav_fragment_avisos;
    public static final int FRAGMENT_INFORMACION = R.id.nav_fragment_informacion;
    public static final int FRAGMENT_PERFIL = R.id.nav_fragment_perfil;
    public static final int FRAGMENT_CALENDARIO = R.id.nav_fragment_calendario;
    public static final int FRAGMENT_CATEGORIAS = 2;
    //Añadirlo automaticamente
    public static final int CERRAR_SESION = 1;

    public static int selecionarItemMenu(MenuItem item, int idFragmentActual, FirebaseUser user, Activity activity,
                                         Context context, FragmentManager fragmentManager, ActionBar actionBar, NavigationView navView) {
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
                case FRAGMENT_CALENDARIO:
                    iniciarFragmentCalendario(fragmentManager,actionBar);
                    break;

            }
            asignarIconosMenu(navView,itemId);
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

    public static int iniciarFragmentCalendario(FragmentManager fragmentManager, ActionBar actionBar){
        fragmentManager.beginTransaction()
                .setReorderingAllowed(true)
                .replace(R.id.fragment_container, FragmentCalendario.class, null)
                .addToBackStack(null)
                .commit();

        actionBar.setTitle("Calendario");
        return FRAGMENT_CALENDARIO;
    }

    public static MenuItem addCerrarSesion(NavigationView navView){
        MenuItem menuItem = navView.getMenu().add(0, CERRAR_SESION, 0, "Cerrar Sesion");
        menuItem.setIcon(R.drawable.ic_logout);
        return menuItem;
    }

    public static void asignarIconosMenu(NavigationView navView,int itemId){
        ActivityNavigation.imgInicio.setImageResource(R.drawable.ic_home);
        ActivityNavigation.imgAvisos.setImageResource(R.drawable.ic_bell);
        ActivityNavigation.imgInformacion.setImageResource(R.drawable.ic_app);
        ActivityNavigation.imgPerfil.setImageResource(R.drawable.ic_user);

        int[][] items = {
                {FRAGMENT_INICIO, R.drawable.ic_home, R.drawable.ic_home_black},
                {FRAGMENT_AVISOS,R.drawable.ic_bell,R.drawable.ic_bell_black},
                {FRAGMENT_INFORMACION,R.drawable.ic_app,R.drawable.ic_app_black},
                {FRAGMENT_PERFIL,R.drawable.ic_user,R.drawable.ic_user_black},
                {FRAGMENT_CALENDARIO,R.drawable.ic_calendar,R.drawable.ic_calendar_black},
                {CERRAR_SESION,R.drawable.ic_logout,R.drawable.ic_login}
        };

        switch (itemId){
            case FRAGMENT_INICIO:
                ActivityNavigation.imgInicio.setImageResource(R.drawable.ic_home_black);
                break;
            case FRAGMENT_AVISOS:
                ActivityNavigation.imgAvisos.setImageResource(R.drawable.ic_bell_black);
                break;
            case FRAGMENT_INFORMACION:
                ActivityNavigation.imgInformacion.setImageResource(R.drawable.ic_app_black);
                break;
            case FRAGMENT_PERFIL:
                ActivityNavigation.imgPerfil.setImageResource(R.drawable.ic_user_black);
                break;
        }

        for (int[] item:items) {
            if(item[0] == itemId){
                navView.getMenu().findItem(item[0]).setIcon(item[2]);
            }else{
                navView.getMenu().findItem(item[0]).setIcon(item[1]);
            }
        }
    }
}
