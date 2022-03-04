package com.parroquiasanleandro;

import android.content.Context;
import android.graphics.Color;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.google.firebase.storage.FirebaseStorage;
import com.parroquiasanleandro.fecha.Fecha;

import java.util.HashMap;
import java.util.Map;

public class Aviso {
    public static final String AVISOS = "Avisos";

    public String key;
    public String titulo;
    public String descripcion;
    public String categoria;
    public Fecha fechaInicio;
    public Fecha fechaFin;
    public boolean todoElDia;
    public String imagen;
    public String uidCreador;

    public Aviso() {
    }

    public Aviso(String titulo, String descripcion, String categoria, Fecha fechaInicio, Fecha fechaFin, boolean todoElDia, String imagen, String uidCreador) {
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.categoria = categoria;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.todoElDia = todoElDia;
        this.imagen = imagen;
        this.uidCreador = uidCreador;
    }

    public Aviso(String titulo, String descripcion, String categoria, Fecha fechaInicio, boolean todoElDia, String imagen, String uidCreador) {
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.categoria = categoria;
        this.fechaInicio = fechaInicio;
        this.todoElDia = todoElDia;
        this.imagen = imagen;
        this.uidCreador = uidCreador;
    }


    public void asignarColor(Context context, LinearLayout linearLayout){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Url.obtenerColorCategoria, response -> {
            linearLayout.setBackgroundColor(Color.parseColor(response));
        }, error -> {
            Toast.makeText(context, "Se ha producido un error al cargar los colores", Toast.LENGTH_SHORT).show();
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> parametros = new HashMap<>();
                parametros.put("id",categoria);
                return parametros;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(stringRequest);
    }

    //Funcion para asignar la imagen del aviso obteniendolo de la bbdd al imageView
    public void asignarImagen(Context context, ImageView imageView) {
        if(categoria.equals("A")){
            //Glide.with(context).load(R.drawable.fondo_parroquia).into(imageView);
            Glide.with(context).load(R.drawable.fondo_parroquia_dark).into(imageView);
        }else{
            if (imagen.equals("imagenPredeterminada")) {
                FirebaseStorage.getInstance().getReference().child("ImagenesAvisos").child(categoria).child("imagenPredeterminada.jpg").getDownloadUrl()
                        .addOnSuccessListener(uri -> {
                            Glide.with(context).load(uri).into(imageView);
                        });
            } else {
                FirebaseStorage.getInstance().getReference().child("ImagenesAvisos").child(categoria).child(imagen).getDownloadUrl()
                        .addOnSuccessListener(uri -> {
                            Glide.with(context).load(uri).into(imageView);
                        });
            }
        }
    }
}
