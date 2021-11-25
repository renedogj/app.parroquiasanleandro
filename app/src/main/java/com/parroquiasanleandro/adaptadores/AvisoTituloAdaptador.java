package com.parroquiasanleandro.adaptadores;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.parroquiasanleandro.Aviso;
import com.parroquiasanleandro.R;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class AvisoTituloAdaptador extends RecyclerView.Adapter<AvisoTituloAdaptador.ViewHolder>{

    private Context context;
    private List<Aviso> avisos;

    public AvisoTituloAdaptador(Context context,List<Aviso> avisos){
        this.context = context;
        this.avisos = avisos;
    }

    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.aviso_titulo_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ViewHolder holder, int position) {
        Aviso aviso = avisos.get(position);
        holder.asignarValores(aviso);
    }

    @Override
    public int getItemCount() {
        return avisos.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private TextView tvTituloAviso;

        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            tvTituloAviso = itemView.findViewById(R.id.tvTituloAviso);
        }

        public void asignarValores(Aviso aviso) {
            tvTituloAviso.setText(aviso.titulo);
        }
    }
}
