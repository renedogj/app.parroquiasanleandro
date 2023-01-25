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
import es.parroquiasanleandro.fragments.FragmentCalendario;
import es.renedogj.fecha.Fecha;

public class DiaAdaptador extends RecyclerView.Adapter<DiaAdaptador.ViewHolder> {
    private final Context context;
    private List<Fecha> fechas;
    private List<Aviso> avisos;
    private Fecha fechaReferencia;
    public int diaSelecionado = -1;
    private DiaAdaptador rvAdapterDia = this;
    private FragmentCalendario fragmentCalendario;

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
        if (diaSelecionado != -1) {
            if (position == diaSelecionado) {
                setHolderSelecionado(holder);
            }
        } else if (fecha.esHoy()) {
            setHolderSelecionado(holder);
        }
    }

    public void setHolderSelecionado(ViewHolder holder) {
        holder.itemView.setSelected(true);
        holder.divider.setBackgroundResource(R.color.negro);
        holder.mostrarAvisosDia();
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
        private View divider;

        private Fecha fecha;
        List<Aviso> avisosDia = new ArrayList<>();

        public ViewHolder(View itemView) {
            super(itemView);
            cardDia = itemView.findViewById(R.id.cardDia);
            tvNumDia = itemView.findViewById(R.id.tvNumDia);
            cardAvisosDia = itemView.findViewById(R.id.cardAvisosDia);
            rvAvisosDia = itemView.findViewById(R.id.rvAvisosDia);
            linearLayoutDiaCalendario = itemView.findViewById(R.id.linearLayoutDiaCalendario);
            divider = itemView.findViewById(R.id.divider);
        }

        public void asignarValores(Fecha fecha) {
            this.fecha = fecha;
            tvNumDia.setText(String.valueOf(fecha.dia));

            if (this.fecha.mes != fechaReferencia.mes) {
                linearLayoutDiaCalendario.setAlpha(0.5f);
            }

            rvAvisosDia.setHasFixedSize(true);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
            rvAvisosDia.setLayoutManager(linearLayoutManager);

            for (Aviso auxAviso : avisos) {
                if (auxAviso.getFechaInicio().dia == fecha.dia && auxAviso.getFechaInicio().mes == fecha.mes) {
                    avisosDia.add(auxAviso);
                } else if (Fecha.isFecha1MayorQueFecha2(fecha, auxAviso.getFechaInicio()) && Fecha.isFecha1MayorQueFecha2(auxAviso.getFechaFin(), fecha)) {
                    avisosDia.add(auxAviso);
                }
            }

            AvisoTituloAdaptador avisoTituloAdaptador = new AvisoTituloAdaptador(context, avisosDia, rvAdapterDia, this);
            rvAvisosDia.setAdapter(avisoTituloAdaptador);

            cardDia.setOnClickListener(v -> {
                diaSelecionado = getAdapterPosition();
                notifyDataSetChanged();
            });

            cardAvisosDia.setOnClickListener(v -> {
                diaSelecionado = getAdapterPosition();
                notifyDataSetChanged();
            });
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
