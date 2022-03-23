package com.parroquiasanleandro;

import android.content.Context;
import android.graphics.Color;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.google.firebase.storage.FirebaseStorage;
import com.parroquiasanleandro.fecha.Fecha;

public class Aviso {
	public static final String AVISOS = "Avisos";

	public String key;
	public String titulo;
	public String descripcion;
	public String categoria;
	public long longInicio;
	public long longFin;
	private Fecha fechaInicio;
	private Fecha fechaFin;
	public boolean todoElDia;
	public String imagen;
	public String uidCreador;

	public Aviso() {
		setFechaInicio(Fecha.toFecha(longInicio));
		setFechaFin(Fecha.toFecha(longFin));
	}

	public Aviso(String titulo, String descripcion, String categoria, Fecha fechaInicio, Fecha fechaFin, boolean todoElDia, String imagen, String uidCreador) {
		this.titulo = titulo;
		this.descripcion = descripcion;
		this.categoria = categoria;
		this.longInicio = Fecha.toMillis(fechaInicio);
		this.longFin = Fecha.toMillis(fechaFin);
		this.todoElDia = todoElDia;
		this.imagen = imagen;
		this.uidCreador = uidCreador;
	}

	public Aviso(String titulo, String descripcion, String categoria, Fecha fechaInicio, boolean todoElDia, String imagen, String uidCreador) {
		this.titulo = titulo;
		this.descripcion = descripcion;
		this.categoria = categoria;
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
		this.uidCreador = uidCreador;
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
		linearLayout.setBackgroundColor(Color.parseColor(Categoria.obtenerColorCategoria(context, categoria)));
	}

	//Funcion para asignar la imagen del aviso obteniendolo de la bbdd al imageView
	public void asignarImagen(Context context, ImageView imageView) {
		if (imagen.equals("imagenPredeterminada")) {
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
		}
	}
}
