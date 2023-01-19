package es.parroquiasanleandro.adaptadores;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import es.parroquiasanleandro.Aviso;
import es.parroquiasanleandro.R;
import es.parroquiasanleandro.activitys.ActivityAviso;
import es.renedogj.fecha.Fecha;

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
        View view = LayoutInflater.from(context).inflate(R.layout.item_aviso, parent, false);
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

        private LinearLayout linearLayoutContenedorAviso;
        private CardView cardAviso;
        private LinearLayout linearLayoutAviso;
        private TextView tvTitulo;
        private TextView tvFecha;
        private ImageView ivAviso;

        public ViewHolder(View itemView) {
            super(itemView);

            linearLayoutContenedorAviso = itemView.findViewById(R.id.linearLayoutContenedorAviso);
            cardAviso = itemView.findViewById(R.id.cardAviso);
            linearLayoutAviso = itemView.findViewById(R.id.linearLayoutAviso);
            tvTitulo = itemView.findViewById(R.id.tvTitulo);
            tvFecha = itemView.findViewById(R.id.tvFecha);
            ivAviso = itemView.findViewById(R.id.ivAviso);
        }

        public void asignarValoresAviso(Aviso aviso) {
            linearLayoutContenedorAviso.setBackgroundColor(aviso.obtenerColor(context));
            tvTitulo.setText(aviso.titulo);
            tvFecha.setText(aviso.getFechaInicio().toString(Fecha.FormatosFecha.EE_d_MMM_aaaa));
            aviso.asignarImagen(context,ivAviso);
            ivAviso.setContentDescription(aviso.idGrupo);

            cardAviso.setOnClickListener(v -> {
                Intent intent = new Intent(context, ActivityAviso.class);
                intent.putExtra("idAviso",aviso.id);
                intent.putExtra("idGrupo",aviso.idGrupo);
                context.startActivity(intent);
            });
        }
    }
}
