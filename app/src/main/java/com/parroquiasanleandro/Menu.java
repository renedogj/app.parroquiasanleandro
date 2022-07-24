package com.parroquiasanleandro;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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
import com.parroquiasanleandro.fragments.FragmentCambiarCorreo;
import com.parroquiasanleandro.fragments.FragmentCategorias;
import com.parroquiasanleandro.fragments.FragmentHorario;
import com.parroquiasanleandro.fragments.FragmentInicio;
import com.parroquiasanleandro.fragments.FragmentPerfil;

public class Menu {
	public static final int FRAGMENT_INICIO = R.id.nav_fragment_inicio;
	public static final int FRAGMENT_AVISOS = R.id.nav_fragment_avisos;
	public static final int FRAGMENT_HORARIO = R.id.nav_fragment_horario;
	public static final int FRAGMENT_PERFIL = R.id.nav_fragment_perfil;
	public static final int FRAGMENT_CALENDARIO = R.id.nav_fragment_calendario;
	public static final int FRAGMENT_CATEGORIAS = 2;
	public static final int CERRAR_SESION = 1;

	//Constructor privado para que no se pueda inicializar
	private Menu() {
	}

	public static int selecionarItemMenu(MenuItem item, int idFragmentActual, FirebaseUser user, Activity activity,
										 Context context, FragmentManager fragmentManager, ActionBar actionBar, NavigationView navView) {
		int itemId = item.getItemId();
		if (itemId != idFragmentActual) {
			switch (itemId) {
				case FRAGMENT_INICIO:
					iniciarFragmentInicio(fragmentManager, actionBar);
					break;
				case FRAGMENT_AVISOS:
					iniciarFragmentAvisos(fragmentManager, actionBar);
					break;
				case FRAGMENT_HORARIO:
					iniciarFragmentHorario(fragmentManager, actionBar);
					break;
				case FRAGMENT_PERFIL:
					iniciarFragmentPerfil(user, activity, context, fragmentManager, actionBar);
					break;
				case CERRAR_SESION:
					FirebaseAuth.getInstance().signOut();
					item.setVisible(false);
					Usuario.borrarUsuarioLocal(context);
					Toast.makeText(context, "Se ha cerrado sesión", Toast.LENGTH_SHORT).show();
					context.startActivity(new Intent(context, ActivityNavigation.class));
					activity.finish();
					break;
				case FRAGMENT_CALENDARIO:
					iniciarFragmentCalendario(fragmentManager, actionBar);
					break;

			}
			asignarIconosMenu(navView, itemId);
		}
		return itemId;
	}

	public static void iniciarFragmentInicio(FragmentManager fragmentManager, ActionBar actionBar) {
		fragmentManager.beginTransaction()
				.setReorderingAllowed(true)
				.replace(R.id.fragment_container, FragmentInicio.class, null)
				.addToBackStack(null)
				.commit();

		actionBar.setTitle("Parroquia San Leandro");
	}

	public static void iniciarFragmentAvisos(FragmentManager fragmentManager, ActionBar actionBar) {
		fragmentManager.beginTransaction()
				.setReorderingAllowed(true)
				.replace(R.id.fragment_container, FragmentAvisosParroquiales.class, null)
				.addToBackStack(null)
				.commit();

		actionBar.setTitle("Avisos");
	}

	public static void iniciarFragmentHorario(FragmentManager fragmentManager, ActionBar actionBar) {
		fragmentManager.beginTransaction()
				.setReorderingAllowed(true)
				.replace(R.id.fragment_container, FragmentHorario.class, null)
				.addToBackStack(null)
				.commit();

		actionBar.setTitle("Información");
	}

	public static void iniciarFragmentPerfil(FirebaseUser user, Activity activity, Context context, FragmentManager fragmentManager, ActionBar actionBar) {
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
	}

	public static void iniciarFragmentCategorias(FragmentManager fragmentManager) {
		fragmentManager.beginTransaction()
				.setReorderingAllowed(true)
				.replace(R.id.fragment_container, FragmentCategorias.class, null)
				.addToBackStack(null)
				.commit();
	}

	public static void iniciarFragmentCalendario(FragmentManager fragmentManager, ActionBar actionBar) {
		fragmentManager.beginTransaction()
				.setReorderingAllowed(true)
				.replace(R.id.fragment_container, FragmentCalendario.class, null)
				.addToBackStack(null)
				.commit();

		actionBar.setTitle("Calendario");
	}

	public static void inicarFragmentCambiarCorreo(FragmentManager fragmentManager) {
		fragmentManager.beginTransaction()
				.setReorderingAllowed(true)
				.replace(R.id.fragment_container, FragmentCambiarCorreo.class, null)
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
