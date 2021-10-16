package com.parroquiasanleandro;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.parroquiasanleandro.fecha.Fecha;

public class Aviso {
    public String key;
    public String titulo;
    public String descripcion;
    public String categoria;
    public Fecha fechaInicio;
    public Fecha fechaFin;
    public boolean todoElDia;
    public String imagen;
    public String uidCreador;

    public Aviso() {}

    public Aviso(String titulo, String descripcion, Fecha fechaInicio, Fecha fechaFin, boolean todoElDia, String imagen, String uidCreador) {
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.todoElDia = todoElDia;
        this.imagen = imagen;
        this.uidCreador = uidCreador;
    }

    public Aviso(String titulo, String descripcion, Fecha fechaInicio, boolean todoElDia, String imagen, String uidCreador) {
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.fechaInicio = fechaInicio;
        this.todoElDia = todoElDia;
        this.imagen = imagen;
        this.uidCreador = uidCreador;
    }

    public void asignarImagen(Context context, ImageView imageView){
        if (imagen.equals("imagenPredeterminada")) {
            FirebaseDatabase.getInstance().getReference().child("infoGeneral").child(imagen).get()
                    .addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DataSnapshot> task) {
                            if (!task.isSuccessful()) {
                                Log.e("firebase", "Error getting data", task.getException());
                            } else {
                                Log.d("firebase", String.valueOf(task.getResult().getValue()));
                                FirebaseStorage.getInstance().getReference().child("ImagenesAvisos").child(String.valueOf(task.getResult().getValue())).getDownloadUrl()
                                        .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                            @Override
                                            public void onSuccess(Uri uri) {
                                                Glide.with(context).load(uri).into(imageView);
                                            }
                                        });
                            }
                        }
                    });
        } else {
            FirebaseStorage.getInstance().getReference().child("ImagenesAvisos").child(imagen).getDownloadUrl()
                    .addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Glide.with(context).load(uri).into(imageView);
                        }
                    });
        }
    }
}
