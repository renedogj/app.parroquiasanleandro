package es.parroquiasanleandro.adaptadores;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import es.parroquiasanleandro.Grupo;
import es.parroquiasanleandro.R;
import es.parroquiasanleandro.Url;

public class GrupoSencilloAdaptador extends RecyclerView.Adapter<GrupoSencilloAdaptador.ViewHolder> {

	private final Context context;
	private final List<Grupo> grupos;

	public GrupoSencilloAdaptador(Context context, List<Grupo> grupos) {
		this.context = context;
		this.grupos = grupos;
	}

	@NonNull
	@NotNull
	@Override
	public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
		View view = LayoutInflater.from(context).inflate(R.layout.grupo_sencillo_item, parent, false);
		return new ViewHolder(view);
	}

	@Override
	public void onBindViewHolder(@NonNull @NotNull ViewHolder holder, int position) {
		Grupo grupo = grupos.get(position);
		holder.asignarValores(grupo);
	}

	@Override
	public int getItemCount() {
		return grupos.size();
	}

	public class ViewHolder extends RecyclerView.ViewHolder {

		private final LinearLayout linearLayoutGrupo;
		private final TextView tvNombreGrupo;
		private final CardView cardImgGrupo;
		private final ImageView imgGrupo;

		public Grupo grupo;

		public ViewHolder(View itemView) {
			super(itemView);
			linearLayoutGrupo = itemView.findViewById(R.id.linearLayoutGrupo);
			tvNombreGrupo = itemView.findViewById(R.id.tvNombreGrupo);
			cardImgGrupo = itemView.findViewById(R.id.cardImgGrupo);
			imgGrupo = itemView.findViewById(R.id.imgGrupo);
		}

		public void asignarValores(Grupo grupo) {
			this.grupo = grupo;


			tvNombreGrupo.setText(grupo.nombre);
			linearLayoutGrupo.setBackgroundColor(Color.parseColor(grupo.color));
			imgGrupo.setBackgroundColor(Color.parseColor(grupo.color));
			Log.d("URL IMAGEN",Url.obtenerImagenAviso + grupo.key +"/img/" + grupo.imagen);
			Glide.with(context).load(Url.obtenerImagenAviso + grupo.key +"/img/" + grupo.imagen).into(imgGrupo);

			/*if (grupo.key.equals("A")) {
				Glide.with(context).load(R.drawable.fondo_parroquia_dark).into(imgGrupo);
			} else {
				Glide.with(context).load(Url.obtenerImagenAviso + grupo.key +"/img/" + grupo.imagen).into(imgGrupo);
			}*/
		}
	}
}