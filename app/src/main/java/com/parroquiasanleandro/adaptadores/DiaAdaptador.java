package com.parroquiasanleandro.adaptadores;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.parroquiasanleandro.Aviso;
import com.parroquiasanleandro.R;
import com.parroquiasanleandro.fecha.Mes;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class DiaAdaptador extends RecyclerView.Adapter<DiaAdaptador.ViewHolder>{

    private Context context;
    private List<Integer> dias;
    private List<Aviso> avisos;
    private Mes mesActual;

    public DiaAdaptador(Context context,List<Integer> dias,List<Aviso> avisos){
        this.context = context;
        this.dias = dias;
        this.avisos = avisos;
    }

    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.dia_item, parent, false);
        return new DiaAdaptador.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ViewHolder holder, int position) {
        int dia = dias.get(position);
        holder.asignarValores(dia);
    }

    @Override
    public int getItemCount() {
        return dias.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private TextView tvNumDia;
        private RecyclerView rvAvisosDia;

        List<Aviso> avisosDia = new ArrayList<>();

        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            tvNumDia = itemView.findViewById(R.id.tvNumDia);
            rvAvisosDia = itemView.findViewById(R.id.rvAvisosDia);
        }

        public void asignarValores(int dia) {
            tvNumDia.setText(String.valueOf(dia));

            rvAvisosDia.setHasFixedSize(true);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
            rvAvisosDia.setLayoutManager(linearLayoutManager);

            for (Aviso aviso: avisos){
                Log.d("AVISO",aviso.titulo);
                if(aviso.fechaInicio.dia == dia){
                    avisosDia.add(aviso);
                }else {
                    Log.d("AVISO",aviso.titulo);
                }
            }

            AvisoTituloAdaptador avisoTituloAdaptador = new AvisoTituloAdaptador(context,avisos);
            rvAvisosDia.setAdapter(avisoTituloAdaptador);
        }
    }
}
