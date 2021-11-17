package com.parroquiasanleandro.adaptadores;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.parroquiasanleandro.Aviso;
import com.parroquiasanleandro.R;
import com.parroquiasanleandro.activitys.ActivityAviso;
import com.parroquiasanleandro.fecha.Fecha;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class AvisoAdaptador extends RecyclerView.Adapter<AvisoAdaptador.ViewHolder> {

    private Context context;
    private List<Aviso> avisos;

    public AvisoAdaptador(Context context, List<Aviso> avisos) {
        this.context = context;
        this.avisos = avisos;
    }

    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.aviso_item, parent, false);
        return new AvisoAdaptador.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ViewHolder holder, int position) {
        Aviso aviso = avisos.get(position);
        holder.asignarValoresAviso(aviso);
    }

    @Override
    public int getItemCount() {
        return avisos.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private CardView cardAviso;
        private TextView tvTitulo;
        private TextView tvFecha;
        //private TextView tvDescripcion;
        private ImageView ivAviso;
        //private CardView cvInfoAvisos;
        //private FrameLayout flInfoAvisos;

        public ViewHolder(View itemView) {
            super(itemView);

            cardAviso = itemView.findViewById(R.id.cardAviso);
            tvTitulo = itemView.findViewById(R.id.tvTitulo);
            tvFecha = itemView.findViewById(R.id.tvFecha);
            //tvDescripcion = itemView.findViewById(R.id.tvDescripcion);
            ivAviso = itemView.findViewById(R.id.ivAviso);
            //cvInfoAvisos = itemView.findViewById(R.id.cvInfoAvisos);
            //flInfoAvisos = itemView.findViewById(R.id.flInfoAvisos);
        }

        public void asignarValoresAviso(Aviso aviso) {
            tvTitulo.setText(aviso.titulo);
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
            });
            //tvDescripcion.setText(aviso.descripcion);
            //flInfoAvisos.setPadding(0, 50, 0, 0);
            /*if (aviso.imagen.equals("imagenPredeterminada")) {
                flInfoAvisos.setPadding(0, 0,0, 0);
            } else {
                flInfoAvisos.setPadding(0, 50, 0, 0);
            }*/
        }

    }
}
