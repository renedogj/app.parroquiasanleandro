package com.parroquiasanleandro.adaptadores;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.parroquiasanleandro.R;
import com.parroquiasanleandro.fecha.Mes;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class DiaAdaptador extends RecyclerView.Adapter<DiaAdaptador.ViewHolder>{

    private Context context;
    private List<Integer> dias;
    private Mes mesActual;

    public DiaAdaptador(Context context,List<Integer> dias){
        this.context = context;
        this.dias = dias;
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
        holder.asignarValoresAviso(dia);
    }

    @Override
    public int getItemCount() {
        return dias.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private TextView tvNumDia;

        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            tvNumDia = itemView.findViewById(R.id.tvNumDia);
        }

        public void asignarValoresAviso(int dia) {
            tvNumDia.setText(dia+"");
            /*tvTitulo.setText(aviso.titulo);
            tvFecha.setText(aviso.fechaInicio.toString(Fecha.FormatosFecha.EE_d_MMM_aaaa));
            aviso.asignarImagen(context,ivAviso);

            cardAviso.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, ActivityAviso.class);
                    intent.putExtra("avisoKey",aviso.key);
                    intent.putExtra("avisoCategoria",aviso.categoria);
                    context.startActivity(intent);
                }
            });*/
        }
    }
}
