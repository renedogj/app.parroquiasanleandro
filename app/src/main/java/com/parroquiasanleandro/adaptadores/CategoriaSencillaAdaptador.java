package com.parroquiasanleandro.adaptadores;

import android.content.Context;
import android.graphics.Color;
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
import com.google.firebase.storage.FirebaseStorage;
import com.parroquiasanleandro.Categoria;
import com.parroquiasanleandro.R;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class CategoriaSencillaAdaptador extends RecyclerView.Adapter<CategoriaSencillaAdaptador.ViewHolder> {

	private final Context context;
	private final List<Categoria> categorias;

	public CategoriaSencillaAdaptador(Context context, List<Categoria> categorias) {
		this.context = context;
		this.categorias = categorias;
	}

	@NonNull
	@NotNull
	@Override
	public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
		View view = LayoutInflater.from(context).inflate(R.layout.categoria_sencilla_item, parent, false);
		return new ViewHolder(view);
	}

	@Override
	public void onBindViewHolder(@NonNull @NotNull ViewHolder holder, int position) {
		Categoria categoria = categorias.get(position);
		holder.asignarValores(categoria);
	}

	@Override
	public int getItemCount() {
		return categorias.size();
	}

	public class ViewHolder extends RecyclerView.ViewHolder {

		private final LinearLayout linearLayoutCategoria;
		private final TextView tvNombreCategoria;
		private final CardView cardImgCategoria;
		private final ImageView imgCategoria;

		public Categoria categoria;

		public ViewHolder(View itemView) {
			super(itemView);
			linearLayoutCategoria = itemView.findViewById(R.id.linearLayoutCategoria);
			tvNombreCategoria = itemView.findViewById(R.id.tvNombreCategoria);
			cardImgCategoria = itemView.findViewById(R.id.cardImgCategoria);
			imgCategoria = itemView.findViewById(R.id.imgCategoria);
		}

		public void asignarValores(Categoria categoria) {
			this.categoria = categoria;

			tvNombreCategoria.setText(categoria.nombre);
			linearLayoutCategoria.setBackgroundColor(Color.parseColor(categoria.color));
			imgCategoria.setBackgroundColor(Color.parseColor(categoria.color));

			if (categoria.key.equals("A")) {
				Glide.with(context).load(R.drawable.fondo_parroquia_dark).into(imgCategoria);
			} else {
				FirebaseStorage.getInstance().getReference().child("ImagenesAvisos").child(categoria.key).child("imagenPredeterminada.jpg").getDownloadUrl()
						.addOnSuccessListener(uri -> {
							Glide.with(context).load(uri).into(imgCategoria);
						});

			}
		}
	}
}