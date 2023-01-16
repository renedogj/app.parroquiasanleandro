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

import java.util.List;

import es.parroquiasanleandro.Aviso;
import es.parroquiasanleandro.R;
import es.parroquiasanleandro.Usuario;
import es.renedogj.fecha.Fecha;

public class DiaAdaptador extends RecyclerView.Adapter<DiaAdaptador.ViewHolder> {
	//public static final int TAMAÑO_GRANDE = 184;
	//public static final int TAMAÑO_PEQUEÑO = 160;

	private final Context context;
	//private List<Integer> dias;
	private List<Fecha> fechas;
	private List<Aviso> avisos;
	private Usuario usuario;
	private Fecha fechaReferencia;
	//private final int tamaño;

	public DiaAdaptador(Context context, List<Fecha> fechas, List<Aviso> avisos, Fecha fechaReferencia, Usuario usuario/*, int tamaño*/) {
		this.context = context;
		this.fechas = fechas;
		this.avisos = avisos;
		//this.fechaReferencia = fechaReferencia;
		//this.usuario = usuario;
		//this.tamaño = tamaño;
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
		//if (dia != 0) {
			holder.asignarValores(fecha);
		//} else {
		//	holder.asignarValoresInvisibles();
		//}
	}

	@Override
	public int getItemCount() {
		return fechas.size();
	}

	public class ViewHolder extends RecyclerView.ViewHolder {
		private CardView cardDia;
		private TextView tvNumDia;
		private RecyclerView rvAvisosDia;
		private LinearLayout linearLayoutDiaCalendario;

		//List<Aviso> avisosDia = new ArrayList<>();

		public ViewHolder(@NonNull @NotNull View itemView) {
			super(itemView);
			cardDia = itemView.findViewById(R.id.cardDia);
			tvNumDia = itemView.findViewById(R.id.tvNumDia);
			rvAvisosDia = itemView.findViewById(R.id.rvAvisosDia);
			linearLayoutDiaCalendario = itemView.findViewById(R.id.linearLayoutDiaCalendario);
		}

		public void asignarValores(Fecha fecha) {
			tvNumDia.setText(String.valueOf(fecha.dia));

			//cardDia.getLayoutParams().height = tamaño;

			rvAvisosDia.setHasFixedSize(true);
			LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
			rvAvisosDia.setLayoutManager(linearLayoutManager);

		}

		public void asignarValoresInvisibles() {
			cardDia.setVisibility(View.INVISIBLE);
		}
	}
}
