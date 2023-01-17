package es.parroquiasanleandro.adaptadores;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import es.parroquiasanleandro.Aviso;
import es.parroquiasanleandro.R;
import es.renedogj.fecha.Fecha;

public class DiaAdaptador extends RecyclerView.Adapter<DiaAdaptador.ViewHolder> {
    private final Context context;
    private List<Fecha> fechas;
    private List<Aviso> avisos;
    private Fecha fechaReferencia;
    private TextView tvFechaSelecionada;
    private RecyclerView rvAvisosDiaSelecionado;

    public DiaAdaptador(Context context, List<Fecha> fechas, List<Aviso> avisos, Fecha fechaReferencia, TextView tvFechaSelecionada, RecyclerView rvAvisosDiaSelecionado) {
        this.context = context;
        this.fechas = fechas;
        this.avisos = avisos;
        this.fechaReferencia = fechaReferencia;
        this.tvFechaSelecionada = tvFechaSelecionada;
        this.rvAvisosDiaSelecionado = rvAvisosDiaSelecionado;
    }

    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_dia, parent, false);
        return new DiaAdaptador.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ViewHolder holder, int position) {
        Fecha fecha = fechas.get(position);
        holder.asignarValores(fecha);
    }

    @Override
    public int getItemCount() {
        return fechas.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private CardView cardDia;
        private TextView tvNumDia;
        private CardView cardAvisosDia;
        private RecyclerView rvAvisosDia;
        private LinearLayout linearLayoutDiaCalendario;

        List<Aviso> avisosDia = new ArrayList<>();

        public ViewHolder(View itemView) {
            super(itemView);
            cardDia = itemView.findViewById(R.id.cardDia);
            tvNumDia = itemView.findViewById(R.id.tvNumDia);
            cardAvisosDia = itemView.findViewById(R.id.cardAvisosDia);
            rvAvisosDia = itemView.findViewById(R.id.rvAvisosDia);
            linearLayoutDiaCalendario = itemView.findViewById(R.id.linearLayoutDiaCalendario);
        }

        public void asignarValores(Fecha fecha) {
            tvNumDia.setText(String.valueOf(fecha.dia));

            if(fecha.mes != fechaReferencia.mes){
                linearLayoutDiaCalendario.setAlpha(0.5f);
            }

            rvAvisosDia.setHasFixedSize(true);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
            rvAvisosDia.setLayoutManager(linearLayoutManager);

            for (Aviso auxAviso : avisos) {
                if (auxAviso.getFechaInicio().dia == fecha.dia && auxAviso.getFechaInicio().mes == fecha.mes) {
                    avisosDia.add(auxAviso);
                }
            }

            AvisoTituloAdaptador avisoTituloAdaptador = new AvisoTituloAdaptador(context, avisosDia, tvFechaSelecionada, rvAvisosDiaSelecionado);
            rvAvisosDia.setAdapter(avisoTituloAdaptador);

            cardDia.setOnClickListener(v -> {
                tvFechaSelecionada.setText("Avisos " + fecha.toString(Fecha.FormatosFecha.EEEE_d_MMMM));
                AvisoAdaptador avisoAdaptador = new AvisoAdaptador(context, avisosDia);
                rvAvisosDiaSelecionado.setAdapter(avisoAdaptador);
            });

            cardAvisosDia.setOnClickListener(v -> {
                tvFechaSelecionada.setText("Avisos " + fecha.toString(Fecha.FormatosFecha.EEEE_d_MMMM));
                AvisoAdaptador avisoAdaptador = new AvisoAdaptador(context, avisosDia);
                rvAvisosDiaSelecionado.setAdapter(avisoAdaptador);
            });
        }
    }
}
