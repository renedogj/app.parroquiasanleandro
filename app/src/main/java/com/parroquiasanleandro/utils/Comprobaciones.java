package com.parroquiasanleandro.utils;

import android.content.Context;
import android.widget.Toast;

public class Comprobaciones {
	//Constructor privado para que no se pueda inicializar
	private Comprobaciones() {
	}

	public static boolean comprobarNombre(Context context, String nombre) {
		if (!nombre.trim().equals("")) {
			return true;
		}
		Toast.makeText(context, "El nombre no puede estar vacio", Toast.LENGTH_SHORT).show();
		return false;
	}

	public static boolean comprobarCorreo(Context context, String correo) {
		correo = correo.trim();
		if (!correo.equals("")) {
			if (correo.length() >= 5) {
				if (correo.contains("@") && correo.contains(".")) {
					if (correo.indexOf("@") == correo.lastIndexOf("@")
							&& correo.indexOf("@") < correo.lastIndexOf(".")
							&& correo.lastIndexOf(".") < correo.length()) {
						return true;
					}
					Toast.makeText(context, "Es necesario introducir un correo electronico valido", Toast.LENGTH_SHORT).show();
					return false;
				}
				Toast.makeText(context, "Es necesario introducir un correo electronico valido", Toast.LENGTH_SHORT).show();
				return false;
			}
			Toast.makeText(context, "El correo electronico debe contener al menos 5 caracteres", Toast.LENGTH_SHORT).show();
			return false;
		}
		Toast.makeText(context, "Es necesario introducir un correo electronico", Toast.LENGTH_SHORT).show();
		return false;
	}

	public static boolean comprobarPassword(Context context, String password, String comprobacion) {
		if (!password.trim().equals("") && password.trim().equals(comprobacion)) {
			return true;
		} else {
			Toast.makeText(context, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show();
			return false;
		}
	}
}
