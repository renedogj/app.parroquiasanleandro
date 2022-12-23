package es.parroquiasanleandro;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.navigation.NavigationView;

import org.jetbrains.annotations.NotNull;

import java.util.Map;

import es.parroquiasanleandro.activitys.ActivityInicarSesion;
import es.parroquiasanleandro.activitys.ActivityNavigation;
import es.parroquiasanleandro.fragments.FragmentAvisosParroquiales;
import es.parroquiasanleandro.fragments.FragmentCalendario;
import es.parroquiasanleandro.fragments.FragmentConfirmarPassword;
import es.parroquiasanleandro.fragments.FragmentGrupos;
import es.parroquiasanleandro.fragments.FragmentHorario;
import es.parroquiasanleandro.fragments.FragmentInicio;
import es.parroquiasanleandro.fragments.FragmentPerfil;
import es.parroquiasanleandro.mercadillo.FragmentMercadillo;

public class Menu {
	public static final String INICIO = "Parroquia San Leandro";
	public static final String AVISOS = "Avisos";
	public static final String HORARIO = "Horario";
	public static final String PERFIL = "Perfil";
	public static final String CALENDARIO = "Calendario";
	public static final String MERCADILLO = "Mercadillo";
	public static final String GRUPOS = "Mis grupos";

	public static final int FRAGMENT_INICIO = R.id.nav_fragment_inicio;
	public static final int FRAGMENT_AVISOS = R.id.nav_fragment_avisos;
	public static final int FRAGMENT_HORARIO = R.id.nav_fragment_horario;
	public static final int FRAGMENT_PERFIL = R.id.nav_fragment_perfil;
	public static final int FRAGMENT_CALENDARIO = R.id.nav_fragment_calendario;
	public static final int FRAGMENT_MERCADILLO = R.id.nav_fragment_mercadillo;
	public static final int FRAGMENT_GRUPOS = R.id.nav_fragment_grupos;
	public static final int CERRAR_SESION = 1;

	//public static Map<String, MenuOption> menuItemMap = new ArrayMap<String, MenuOption>();
	public static Map<Integer, MenuOption> menuOptionMap = MenuOption.obtenerMapMenuOptions();

	//Constructor privado para que no se pueda inicializar
	private Menu() {}

	public static int selecionarFragmentMenuItem(MenuItem item, int idFragmentActual, Usuario usuario, Activity activity,
												 Context context, FragmentManager fragmentManager, ActionBar actionBar, NavigationView navView) {
		int itemId = item.getItemId();
		seleccionarFragmentMenuId(itemId, idFragmentActual, usuario, activity, context, fragmentManager, actionBar, navView);
		return itemId;
	}

	public static void seleccionarFragmentMenuId(@NotNull int idFragment, @NotNull int idFragmentActual, Usuario usuario, Activity activity,
												Context context, @NotNull FragmentManager fragmentManager, @NotNull ActionBar actionBar, NavigationView navView) {
		MenuOption menuOption = menuOptionMap.get(idFragment);
		if (menuOption != null && idFragmentActual != menuOption.id) {
			if (menuOption.nombre.equals(Menu.PERFIL)) {
				iniciarFragmentPerfil(usuario, activity, context, fragmentManager, actionBar);
			} else if(idFragment == CERRAR_SESION){
				navView.getMenu().getItem(CERRAR_SESION).setVisible(false);
				Usuario.borrarUsuarioLocal(context);
				Toast.makeText(context, "Se ha cerrado sesión", Toast.LENGTH_SHORT).show();
				context.startActivity(new Intent(context, ActivityNavigation.class));
				activity.finish();
			}else {
				iniciarFragmentEstandar(menuOption, fragmentManager, actionBar);
			}
			asignarIconosMenu(navView, menuOption.id);
		}
	}

	public static void iniciarFragmentEstandar(MenuOption menuOption, FragmentManager fragmentManager, ActionBar actionBar){
		fragmentManager.beginTransaction()
				.setReorderingAllowed(true)
				.replace(R.id.fragment_container, menuOption.fragementClass, null)
				.addToBackStack(null)
				.commit();

		actionBar.setTitle(menuOption.nombre);
	}

	public static void iniciarFragmentInicio(FragmentManager fragmentManager, ActionBar actionBar) {
		fragmentManager.beginTransaction()
				.setReorderingAllowed(true)
				.replace(R.id.fragment_container, FragmentInicio.class, null)
				.addToBackStack(null)
				.commit();

		actionBar.setTitle(INICIO);
	}

	public static void iniciarFragmentAvisos(FragmentManager fragmentManager, ActionBar actionBar) {
		fragmentManager.beginTransaction()
				.setReorderingAllowed(true)
				.replace(R.id.fragment_container, FragmentAvisosParroquiales.class, null)
				.addToBackStack(null)
				.commit();

		actionBar.setTitle(AVISOS);
	}

	public static void iniciarFragmentHorario(FragmentManager fragmentManager, ActionBar actionBar) {
		fragmentManager.beginTransaction()
				.setReorderingAllowed(true)
				.replace(R.id.fragment_container, FragmentHorario.class, null)
				.addToBackStack(null)
				.commit();

		actionBar.setTitle(HORARIO);
	}

	public static void iniciarFragmentPerfil(Usuario usuario, Activity activity, Context context, FragmentManager fragmentManager, ActionBar actionBar) {
		if (usuario.getId() != null) {
			fragmentManager.beginTransaction()
					.setReorderingAllowed(true)
					.replace(R.id.fragment_container, FragmentPerfil.class, null)
					.addToBackStack(null)
					.commit();

			actionBar.setTitle(PERFIL);
		} else {
			activity.startActivity(new Intent(context, ActivityInicarSesion.class));
			activity.finish();
		}
	}

	public static void iniciarFragmentCalendario(FragmentManager fragmentManager, ActionBar actionBar) {
		fragmentManager.beginTransaction()
				.setReorderingAllowed(true)
				.replace(R.id.fragment_container, FragmentCalendario.class, null)
				.addToBackStack(null)
				.commit();

		actionBar.setTitle(CALENDARIO);
	}

	public static void iniciarFragmentMercadillo(FragmentManager fragmentManager, ActionBar actionBar) {
		fragmentManager.beginTransaction()
				.setReorderingAllowed(true)
				.replace(R.id.fragment_container, FragmentMercadillo.class, null)
				.addToBackStack(null)
				.commit();

		actionBar.setTitle(MERCADILLO);
	}

	public static void iniciarFragmentGrupos(FragmentManager fragmentManager, ActionBar actionBar) {
		fragmentManager.beginTransaction()
				.setReorderingAllowed(true)
				.replace(R.id.fragment_container, FragmentGrupos.class, null)
				.addToBackStack(null)
				.commit();
		actionBar.setTitle(GRUPOS);
	}

	public static void inicarFragmentConfirmarPassword(FragmentManager fragmentManager) {
		fragmentManager.beginTransaction()
				.setReorderingAllowed(true)
				.replace(R.id.fragment_container, FragmentConfirmarPassword.class, null)
				.addToBackStack(null)
				.commit();
	}

	public static MenuItem addCerrarSesion(NavigationView navView) {
		MenuItem menuItem = navView.getMenu().add(0, CERRAR_SESION, 0, "Cerrar Sesion");
		menuItem.setIcon(R.drawable.ic_logout);
		return menuItem;
	}

	public static void asignarIconosMenu(NavigationView navView, int itemId) {
		ActivityNavigation.imgInicio.setImageResource(R.drawable.ic_home);
		ActivityNavigation.imgAvisos.setImageResource(R.drawable.ic_bell);
		ActivityNavigation.imgHorario.setImageResource(R.drawable.ic_app);
		ActivityNavigation.imgPerfil.setImageResource(R.drawable.ic_user);

		int[][] items = {
				{FRAGMENT_INICIO, R.drawable.ic_home, R.drawable.ic_home_black},
				{FRAGMENT_AVISOS, R.drawable.ic_bell, R.drawable.ic_bell_black},
				{FRAGMENT_HORARIO, R.drawable.ic_app, R.drawable.ic_app_black},
				{FRAGMENT_PERFIL, R.drawable.ic_user, R.drawable.ic_user_black},
				{FRAGMENT_CALENDARIO, R.drawable.ic_calendar, R.drawable.ic_calendar_black}
		};

		switch (itemId) {
			case FRAGMENT_INICIO:
				ActivityNavigation.imgInicio.setImageResource(R.drawable.ic_home_black);
				break;
			case FRAGMENT_AVISOS:
				ActivityNavigation.imgAvisos.setImageResource(R.drawable.ic_bell_black);
				break;
			case FRAGMENT_HORARIO:
				ActivityNavigation.imgHorario.setImageResource(R.drawable.ic_app_black);
				break;
			case FRAGMENT_PERFIL:
				ActivityNavigation.imgPerfil.setImageResource(R.drawable.ic_user_black);
				break;
		}

		//Modificamos los iconos del menu lateral
		for (int[] item : items) {
			if (item[0] == itemId) {
				navView.getMenu().findItem(item[0]).setIcon(item[2]);
			} else {
				navView.getMenu().findItem(item[0]).setIcon(item[1]);
			}
		}
	}
}
