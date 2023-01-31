package es.parroquiasanleandro;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;

import java.util.Map;

import es.parroquiasanleandro.activitys.ActivityInicarSesion;
import es.parroquiasanleandro.activitys.ActivityNavigation;
import es.parroquiasanleandro.fragments.FragmentAvisosParroquiales;
import es.parroquiasanleandro.fragments.FragmentCalendario;
import es.parroquiasanleandro.fragments.FragmentConfirmarPassword;
import es.parroquiasanleandro.fragments.FragmentGrupos;
import es.parroquiasanleandro.fragments.FragmentHorario;
import es.parroquiasanleandro.fragments.FragmentInfoGrupo;
import es.parroquiasanleandro.fragments.FragmentInicio;
import es.parroquiasanleandro.fragments.FragmentMercadillo;
import es.parroquiasanleandro.fragments.FragmentPerfil;

public class Menu {
    public static final String INICIO = "Parroquia San Leandro";
    public static final String AVISOS = "Avisos";
    public static final String HORARIO = "Horario";
    public static final String INFORMACION = "Informacion";
    public static final String PERFIL = "Perfil";
    public static final String CALENDARIO = "Calendario";
    public static final String MERCADILLO = "Mercadillo";
    public static final String GRUPOS = "Mis grupos";

    public static final int CERRAR_SESION = 1;
    public static final int FRAGMENT_INICIO = R.id.nav_fragment_inicio;
    public static final int FRAGMENT_AVISOS = R.id.nav_fragment_avisos;
    public static final int FRAGMENT_HORARIO = R.id.nav_fragment_horario;
    public static final int FRAGMENT_INFORMACION = R.id.nav_fragment_informacion;
    public static final int FRAGMENT_PERFIL = R.id.nav_fragment_perfil;
    public static final int FRAGMENT_CALENDARIO = R.id.nav_fragment_calendario;
    public static final int FRAGMENT_MERCADILLO = R.id.nav_fragment_mercadillo;
    public static final int FRAGMENT_GRUPOS = R.id.nav_fragment_grupos;
    public static final int FRAGMENT_INFO_GRUPO = 2;

    public static Map<Integer, MenuOption> menuOptionMap = MenuOption.obtenerMapMenuOptions();

    //Constructor privado para que no se pueda inicializar
    private Menu() {
    }

    public static int selecionarFragmentMenuItem(MenuItem item, int idFragmentActual, Usuario usuario, Activity activity, Context context) {
        int itemId = item.getItemId();
        seleccionarFragmentMenuId(itemId, idFragmentActual, usuario, activity, context);
        return itemId;
    }

    public static void seleccionarFragmentMenuId(int idFragment, int idFragmentActual, Usuario usuario, Activity activity, Context context) {
        MenuOption menuOption = menuOptionMap.get(idFragment);
        if (menuOption != null && idFragmentActual != menuOption.id) {
            if (menuOption.nombre.equals(Menu.PERFIL)) {
                iniciarFragmentPerfil(usuario, activity, context);
            } else if (menuOption.nombre.equals(Menu.GRUPOS)) {
                iniciarFragmentGrupos(usuario, activity, context);
            } else {
                iniciarFragmentEstandar(menuOption);
            }
            asignarIconosMenu(menuOption.id);
        } else if (idFragment == CERRAR_SESION) {
            ActivityNavigation.navView.getMenu().getItem(CERRAR_SESION).setVisible(false);
            Usuario.borrarUsuarioLocal(context);
            Toast.makeText(context, "Se ha cerrado sesión", Toast.LENGTH_SHORT).show();
            context.startActivity(new Intent(context, ActivityNavigation.class));
            activity.finish();
        }
    }

    public static void iniciarFragmentEstandar(MenuOption menuOption) {
        ActivityNavigation.fragmentManager.beginTransaction()
                .setReorderingAllowed(true)
                .replace(R.id.fragment_container, menuOption.fragementClass, null)
                .addToBackStack(null)
                .commit();

        ActivityNavigation.actionBar.setTitle(menuOption.nombre);
    }

    public static void iniciarFragmentInicio() {
        ActivityNavigation.fragmentManager.beginTransaction()
                .setReorderingAllowed(true)
                .replace(R.id.fragment_container, FragmentInicio.class, null)
                .addToBackStack(null)
                .commit();

        ActivityNavigation.actionBar.setTitle(INICIO);
    }

    public static void iniciarFragmentAvisos() {
        ActivityNavigation.fragmentManager.beginTransaction()
                .setReorderingAllowed(true)
                .replace(R.id.fragment_container, FragmentAvisosParroquiales.class, null)
                .addToBackStack(null)
                .commit();

        ActivityNavigation.actionBar.setTitle(AVISOS);
    }

    public static void iniciarFragmentHorario() {
        ActivityNavigation.fragmentManager.beginTransaction()
                .setReorderingAllowed(true)
                .replace(R.id.fragment_container, FragmentHorario.class, null)
                .addToBackStack(null)
                .commit();

        ActivityNavigation.actionBar.setTitle(HORARIO);
    }

    public static void iniciarFragmentPerfil(Usuario usuario, Activity activity, Context context) {
        if (usuario.getId() != null) {
            ActivityNavigation.fragmentManager.beginTransaction()
                    .setReorderingAllowed(true)
                    .replace(R.id.fragment_container, FragmentPerfil.class, null)
                    .addToBackStack(null)
                    .commit();

            ActivityNavigation.actionBar.setTitle(PERFIL);
        } else {
            activity.startActivity(new Intent(context, ActivityInicarSesion.class));
            activity.finish();
        }
    }

    public static void iniciarFragmentCalendario() {
        ActivityNavigation.fragmentManager.beginTransaction()
                .setReorderingAllowed(true)
                .replace(R.id.fragment_container, FragmentCalendario.class, null)
                .addToBackStack(null)
                .commit();

        ActivityNavigation.actionBar.setTitle(CALENDARIO);
    }

    public static void iniciarFragmentMercadillo() {
        ActivityNavigation.fragmentManager.beginTransaction()
                .setReorderingAllowed(true)
                .replace(R.id.fragment_container, FragmentMercadillo.class, null)
                .addToBackStack(null)
                .commit();

        ActivityNavigation.actionBar.setTitle(MERCADILLO);
    }

    public static void iniciarFragmentGrupos(Usuario usuario, Activity activity, Context context) {
        if (usuario.getId() != null) {
            ActivityNavigation.fragmentManager.beginTransaction()
                    .setReorderingAllowed(true)
                    .replace(R.id.fragment_container, FragmentGrupos.class, null)
                    .addToBackStack(null)
                    .commit();

            ActivityNavigation.actionBar.setTitle(GRUPOS);
        } else {
            activity.startActivity(new Intent(context, ActivityInicarSesion.class));
            activity.finish();
        }
    }

    public static void iniciarFragmentGrupos() {
        ActivityNavigation.fragmentManager.beginTransaction()
                .setReorderingAllowed(true)
                .replace(R.id.fragment_container, FragmentGrupos.class, null)
                .addToBackStack(null)
                .commit();

        ActivityNavigation.actionBar.setTitle(GRUPOS);
    }

    public static void iniciarFragmentInfoGrupo() {
        ActivityNavigation.fragmentManager.beginTransaction()
                .setReorderingAllowed(true)
                .replace(R.id.fragment_container, FragmentInfoGrupo.class, null)
                .addToBackStack(null)
                .commit();

        ActivityNavigation.actionBar.setTitle(GRUPOS);
    }

    public static void inicarFragmentConfirmarPassword() {
        ActivityNavigation.fragmentManager.beginTransaction()
                .setReorderingAllowed(true)
                .replace(R.id.fragment_container, FragmentConfirmarPassword.class, null)
                .addToBackStack(null)
                .commit();
    }

    public static void addCerrarSesion(NavigationView navView) {
        MenuItem menuItem = navView.getMenu().add(0, CERRAR_SESION, 0, "Cerrar Sesion");
        menuItem.setIcon(R.drawable.ic_logout);
    }

    public static void asignarIconosMenu(int itemId) {
        ActivityNavigation.imgInicio.setImageResource(R.drawable.ic_home);
        ActivityNavigation.imgAvisos.setImageResource(R.drawable.ic_bell);
        ActivityNavigation.imgHorario.setImageResource(R.drawable.ic_reloj);
        ActivityNavigation.imgPerfil.setImageResource(R.drawable.ic_user);

        int[][] items = {
                {FRAGMENT_INICIO, R.drawable.ic_home, R.drawable.ic_home_black},
                {FRAGMENT_AVISOS, R.drawable.ic_bell, R.drawable.ic_bell_black},
                {FRAGMENT_HORARIO, R.drawable.ic_reloj, R.drawable.ic_reloj_black},
                {FRAGMENT_CALENDARIO, R.drawable.ic_calendar, R.drawable.ic_calendar_black},
                {FRAGMENT_MERCADILLO, R.drawable.ic_mercadillo, R.drawable.ic_mercadillo_black},
                {FRAGMENT_INFORMACION, R.drawable.ic_informacion, R.drawable.ic_informacion_black},
                {FRAGMENT_GRUPOS, R.drawable.ic_grupos, R.drawable.ic_grupos_black},
                {FRAGMENT_PERFIL, R.drawable.ic_user, R.drawable.ic_user_black},
        };

        switch (itemId) {
            case FRAGMENT_INICIO:
                ActivityNavigation.imgInicio.setImageResource(R.drawable.ic_home_black);
                break;
            case FRAGMENT_AVISOS:
                ActivityNavigation.imgAvisos.setImageResource(R.drawable.ic_bell_black);
                break;
            case FRAGMENT_HORARIO:
                ActivityNavigation.imgHorario.setImageResource(R.drawable.ic_reloj_black);
                break;
            case FRAGMENT_PERFIL:
                ActivityNavigation.imgPerfil.setImageResource(R.drawable.ic_user_black);
                break;
        }

        //Modificamos los iconos del menu lateral
        for (int[] item : items) {
            if (item[0] == itemId) {
                ActivityNavigation.navView.getMenu().findItem(item[0]).setIcon(item[2]);
            } else {
                ActivityNavigation.navView.getMenu().findItem(item[0]).setIcon(item[1]);
            }
        }
    }
}
