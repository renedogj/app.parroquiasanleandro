package es.parroquiasanleandro.adaptadores;

import android.content.Context;
import android.graphics.Color;
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
import es.parroquiasanleandro.fragments.FragmentCalendario;
import es.renedogj.fecha.Fecha;

public class DiaAdaptador extends RecyclerView.Adapter<DiaAdaptador.ViewHolder> {
    private final Context context;
    private final List<Fecha> fechas;
    private final List<Aviso> avisos;
    private final Fecha fechaReferencia;
    public int diaPrevioSelecionado = -1;
    public int diaSelecionado = -1;
    public FragmentCalendario fragmentCalendario;

    public DiaAdaptador(Context context, List<Fecha> fechas, List<Aviso> avisos, Fecha fechaReferencia, FragmentCalendario fragmentCalendario) {
        this.context = context;
        this.fechas = fechas;
        this.avisos = avisos;
        this.fechaReferencia = fechaReferencia;
        this.fragmentCalendario = fragmentCalendario;
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

        if(diaSelecionado == position){
            setHolderSelecionado(holder);
        }

        if(diaPrevioSelecionado == position){
            setHolderNoSelecionado(holder);
        }

        if (fecha.esHoy()) {
            setHolderSelecionado(holder);
        }
    }

    public void setHolderSelecionado(ViewHolder holder) {
        holder.itemView.setSelected(true);
        holder.tvNumDia.setTextColor(Color.parseColor("#FFF5F5F5"));
        holder.lnlyBgTvNumDia.setBackgroundResource(R.color.primary_color);
        holder.mostrarAvisosDia();
    }

    public void setHolderNoSelecionado(ViewHolder holder) {
        holder.itemView.setSelected(false);
        holder.tvNumDia.setTextColor(Color.parseColor("#FF31666C"));
        holder.lnlyBgTvNumDia.setBackgroundResource(R.color.blanco);
    }

    @Override
    public int getItemCount() {
        return fechas.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final CardView cardDia;
        private final TextView tvNumDia; //Texto de la fecha de cada uno de los días
        private final CardView cardAvisosDia;
        private final RecyclerView rvAvisosDia; //Rv con los avisos del día seleccionado
        private final LinearLayout linearLayoutDiaCalendario;  //LnLy de cada día del calendario
        private final LinearLayout lnlyBgTvNumDia;

        private Fecha fecha;
        List<Aviso> avisosDia = new ArrayList<>();

        public ViewHolder(View itemView) {
            super(itemView);
            cardDia = itemView.findViewById(R.id.cardDia);
            tvNumDia = itemView.findViewById(R.id.tvNumDia);
            cardAvisosDia = itemView.findViewById(R.id.cardAvisosDia);
            rvAvisosDia = itemView.findViewById(R.id.rvAvisosDia);
            linearLayoutDiaCalendario = itemView.findViewById(R.id.linearLayoutDiaCalendario);
            lnlyBgTvNumDia = itemView.findViewById(R.id.lnlyBgTvNumDia);
        }

        public void asignarValores(Fecha fecha) {
            this.fecha = fecha;
            tvNumDia.setText(String.valueOf(fecha.dia));

            if (this.fecha.mes != fechaReferencia.mes) {
                cardAvisosDia.setBackgroundResource(R.color.gris_claro);
                linearLayoutDiaCalendario.setAlpha(0.6f);
            }

            rvAvisosDia.setHasFixedSize(true);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
            rvAvisosDia.setLayoutManager(linearLayoutManager);

            avisosDia.clear();
            for (Aviso auxAviso : avisos) {
                if (auxAviso.getFechaInicio().dia == fecha.dia && auxAviso.getFechaInicio().mes == fecha.mes) {
                    avisosDia.add(auxAviso);
                } else if (Fecha.isFecha1MayorQueFecha2(fecha, auxAviso.getFechaInicio()) && Fecha.isFecha1MayorQueFecha2(auxAviso.getFechaFin(), fecha)) {
                    avisosDia.add(auxAviso);
                }
            }

            AvisoTituloAdaptador avisoTituloAdaptador = new AvisoTituloAdaptador(context, avisosDia, this);
            rvAvisosDia.setAdapter(avisoTituloAdaptador);

            cardDia.setOnClickListener(v -> {
                selecionarDia();
            });

            cardAvisosDia.setOnClickListener(v -> {
                selecionarDia();
            });
        }

        public void selecionarDia () {
            diaPrevioSelecionado = diaSelecionado;
            diaSelecionado = getAbsoluteAdapterPosition();
            if (diaSelecionado != -1) {
                notifyItemChanged(diaSelecionado);
            }
            if(diaPrevioSelecionado != -1){
                notifyItemChanged(diaPrevioSelecionado);
            }
        }

        public void mostrarAvisosDia() {
            fragmentCalendario.tvFechaSelecionada.setText("Avisos " + fecha.toString(Fecha.FormatosFecha.EEEE_d_MMMM));
            AvisoAdaptador avisoAdaptador = new AvisoAdaptador(context, avisosDia);
            fragmentCalendario.rvAvisosDiaSelecionado.setAdapter(avisoAdaptador);
            if (avisosDia.isEmpty()) {
                fragmentCalendario.tvNoHayAvisos.setText("No hay ningún aviso");
            } else {
                fragmentCalendario.tvNoHayAvisos.setText("");
            }
        }
    }
}
