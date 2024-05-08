package es.parroquiasanleandro.utils;

import android.content.Context;
import android.util.Log;
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

	public static boolean checkVersion(String versionActual, String versionNueva){
		Log.d("VERSION ACTUAL", versionActual);
		Log.d("VERSION NUEVA", versionNueva);
		boolean result = false;
		int[] versiones = new int[6];
		int i = 0, anterior = 0, orden = 0;
		if(versionActual != null && versionNueva != null){
			try{
				for(i = 0; i < 6; i++){
					versiones[i] = 0;
				}
				i = 0;
				do{
					i = versionActual.indexOf('.', anterior);
					if(i > 0){
						versiones[orden] = Integer.parseInt(versionActual.substring(anterior, i));
					}else{
						versiones[orden] = Integer.parseInt(versionActual.substring(anterior));
					}
					anterior = i + 1;
					orden++;
				}while(i != -1);
				anterior = 0;
				orden = 3;
				i = 0;
				do{
					i = versionNueva.indexOf('.', anterior);
					if(i > 0){
						versiones[orden] = Integer.parseInt(versionNueva.substring(anterior, i));
					}else{
						versiones[orden] = Integer.parseInt(versionNueva.substring(anterior));
					}
					anterior = i + 1;
					orden++;
				}while(i != -1 && orden < 6);
				if(versiones[0] < versiones[3]){
					result = true;
				}else if(versiones[1] < versiones[4] && versiones[0] == versiones[3]){
					result = true;
				}else if(versiones[2] < versiones[5] && versiones[0] == versiones[3] && versiones[1] == versiones[4]){
					result = true;
				}
			}catch (NumberFormatException e){
				Log.e("updateApp", "NFE " + e.getMessage() + " parsing versionAct " + versionActual + " and versionNew " + versionNueva);
				e.printStackTrace();
			}catch (Exception e){
				Log.e("updateApp", "Ex " + e.getMessage() + " parsing versionAct " + versionActual + " and versionNew " + versionNueva);
				e.printStackTrace();
			}
		}
		return result;
	}
}
