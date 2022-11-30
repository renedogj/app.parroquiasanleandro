package es.parroquiasanleandro;

import android.content.Context;
import android.graphics.Color;
import android.widget.ImageView;
import android.widget.LinearLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import es.parroquiasanleandro.fecha.Fecha;

public class Aviso {
	public static final String AVISOS = "Avisos";

	public String id;
	public String idCreador;
	public String idCategoria;
	public String titulo;
	public String descripcion;
	public long longInicio;
	public long longFin;
	private Fecha fechaInicio;
	private Fecha fechaFin;
	public boolean todoElDia;
	public String imagen;

	public Aviso() {
		//setFechaInicio(Fecha.toFecha(longInicio));
		//setFechaFin(Fecha.toFecha(longFin));
	}

	public Aviso(String titulo, String descripcion, String idCategoria, Fecha fechaInicio, Fecha fechaFin, boolean todoElDia, String imagen, String uidCreador) {
		this.titulo = titulo;
		this.descripcion = descripcion;
		this.idCategoria = idCategoria;
		this.longInicio = Fecha.toMillis(fechaInicio);
		this.longFin = Fecha.toMillis(fechaFin);
		this.todoElDia = todoElDia;
		this.imagen = imagen;
		this.idCreador = uidCreador;
	}

	public Aviso(String titulo, String descripcion, String idCategoria, Fecha fechaInicio, boolean todoElDia, String imagen, String uidCreador) {
		this.titulo = titulo;
		this.descripcion = descripcion;
		this.idCategoria = idCategoria;
		this.longInicio = Fecha.toMillis(fechaInicio);
		if(todoElDia){
			fechaInicio.sumDias(1);
			fechaInicio.hora = 0;
			fechaInicio.minuto = 0;
			fechaInicio.segundo = 0;
			this.longFin = Fecha.toMillis(fechaInicio);
		}else{
			fechaInicio.sumHoras(1);
			this.longFin = Fecha.toMillis(fechaInicio);
		}
		this.todoElDia = todoElDia;
		this.imagen = imagen;
		this.idCreador = uidCreador;
	}

	public Fecha getFechaInicio() {
		return fechaInicio;
	}

	public void setFechaInicio(Fecha fechaInicio) {
		this.fechaInicio = fechaInicio;
	}

	public Fecha getFechaFin() {
		return fechaFin;
	}

	public void setFechaFin(Fecha fechaFin) {
		this.fechaFin = fechaFin;
	}

	public void asignarColor(Context context, LinearLayout linearLayout) {
		linearLayout.setBackgroundColor(Color.parseColor(Categoria.obtenerColorCategoria(context, idCategoria)));
	}

	public static List<Aviso> JSONArrayToAviso(JSONArray jsonArrayAvisos){
		List<Aviso>  avisos = new ArrayList<>();
		for (int i = 0; i < jsonArrayAvisos.length(); i++) {
			Aviso avisoAux = new Aviso();
			try {
				JSONObject jsonAviso = jsonArrayAvisos.getJSONObject(i);
				avisoAux.id = jsonAviso.getString("id");
				avisoAux.idCreador = jsonAviso.getString("id_creador");
				avisoAux.idCategoria = jsonAviso.getString("id_categoria");
				avisoAux.descripcion = jsonAviso.getString("descripcion");
				avisoAux.imagen = jsonAviso.getString("imagen");
				avisoAux.longInicio = jsonAviso.getLong("fecha_inicio");
				avisoAux.longFin = jsonAviso.getLong("fecha_fin");
				avisoAux.fechaInicio = Fecha.toFecha(jsonAviso.getLong("fecha_inicio"));
				avisoAux.fechaFin = Fecha.toFecha(jsonAviso.getLong("fecha_fin"));
			} catch (JSONException e) {
				e.printStackTrace();
			}
			avisos.add(avisoAux);
		}
		return avisos;
	}

	//Funcion para asignar la imagen del aviso obteniendolo de la bbdd al imageView
	public void asignarImagen(Context context, ImageView imageView) {
		/*if (imagen.equals("imagenPredeterminada")) {
			if (categoria.equals("A")) {
				Glide.with(context).load(R.drawable.fondo_parroquia_dark).into(imageView);
			} else {
				FirebaseStorage.getInstance().getReference().child("ImagenesAvisos").child(categoria).child("imagenPredeterminada.jpg").getDownloadUrl()
						.addOnSuccessListener(uri -> {
							Glide.with(context).load(uri).into(imageView);
						});
			}
		} else {
			FirebaseStorage.getInstance().getReference().child("ImagenesAvisos").child(categoria).child(imagen).getDownloadUrl()
					.addOnSuccessListener(uri -> {
						Glide.with(context).load(uri).into(imageView);
					});
		}*/
	}
}
