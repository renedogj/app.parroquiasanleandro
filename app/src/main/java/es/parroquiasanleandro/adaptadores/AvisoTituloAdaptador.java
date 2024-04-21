package es.parroquiasanleandro.adaptadores;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import es.parroquiasanleandro.Aviso;
import es.parroquiasanleandro.R;

public class AvisoTituloAdaptador extends RecyclerView.Adapter<AvisoTituloAdaptador.ViewHolder> {

    private final Context context;
    private final List<Aviso> avisosDia;
    private final DiaAdaptador.ViewHolder diaAdaptadorViewHolder;

    public AvisoTituloAdaptador(Context context, List<Aviso> avisosDia, DiaAdaptador.ViewHolder diaAdaptadorViewHolder) {
        this.context = context;
        this.avisosDia = avisosDia;
        this.diaAdaptadorViewHolder = diaAdaptadorViewHolder;
    }

    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_aviso_titulo, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ViewHolder holder, int position) {
        Aviso aviso = avisosDia.get(position);
        holder.asignarValores(aviso);
    }

    @Override
    public int getItemCount() {
        return avisosDia.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final CardView cardTituloAviso;
        private final TextView tvTituloAviso;

        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            cardTituloAviso = itemView.findViewById(R.id.cardTituloAviso);
            tvTituloAviso = itemView.findViewById(R.id.tvTituloAviso);
        }

        public void asignarValores(Aviso aviso) {
            tvTituloAviso.setText(aviso.titulo);

            cardTituloAviso.setOnClickListener(v -> {
                diaAdaptadorViewHolder.selecionarDia();
            });
        }
    }
}
