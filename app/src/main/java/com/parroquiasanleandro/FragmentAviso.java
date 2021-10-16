package com.parroquiasanleandro;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.parroquiasanleandro.fecha.Fecha;

import org.jetbrains.annotations.NotNull;

public class FragmentAviso extends Fragment {

    private Aviso aviso;

    private TextView tvTituloAviso;
    private TextView tvFechaInicio;
    private TextView tvFechaFinal;
    private TextView tvDescripcion;

    public FragmentAviso() {}

    public FragmentAviso(Aviso aviso){
        this.aviso = aviso;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_aviso, container, false);
        tvTituloAviso = view.findViewById(R.id.tvTituloAviso);
        tvFechaInicio = view.findViewById(R.id.tvFechaInicio);
        tvFechaFinal = view.findViewById(R.id.tvFechaFinal);
        tvDescripcion = view.findViewById(R.id.tvDescripcion);

        tvTituloAviso.setText(aviso.titulo);
        tvFechaInicio.setText(aviso.fechaInicio.toString(Fecha.FormatosFecha.EE_d_MMM_aaaa) + "  " + aviso.fechaInicio.toString(Fecha.FormatosFecha.HH_mm));
        if (aviso.fechaFin != null){
            tvFechaFinal.setText(aviso.fechaFin.toString(Fecha.FormatosFecha.EE_d_MMM_aaaa) + "  " + aviso.fechaFin.toString(Fecha.FormatosFecha.HH_mm));
        }
        tvDescripcion.setText(aviso.descripcion);
        return view;
    }
}