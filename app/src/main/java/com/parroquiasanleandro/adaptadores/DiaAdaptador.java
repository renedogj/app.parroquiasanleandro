package com.parroquiasanleandro.adaptadores;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.parroquiasanleandro.Aviso;
import com.parroquiasanleandro.Categoria;
import com.parroquiasanleandro.R;
import com.parroquiasanleandro.Usuario;
import com.parroquiasanleandro.fecha.Fecha;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DiaAdaptador extends RecyclerView.Adapter<DiaAdaptador.ViewHolder> {
	public static final int TAMAÑO_GRANDE = 184;
	public static final int TAMAÑO_PEQUEÑO = 160;

	private Context context;
	private List<Integer> dias;
	private Usuario usuario;
	private Fecha fechaReferencia;
	private final int tamaño;

	public DiaAdaptador(Context context, List<Integer> dias, Fecha fechaReferencia, Usuario usuario, int tamaño) {
		this.context = context;
		this.dias = dias;
		this.fechaReferencia = fechaReferencia;
		this.usuario = usuario;
		this.tamaño = tamaño;
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
		if (dia != 0) {
			holder.asignarValores(dia);
		} else {
			holder.asignarValoresInvisibles();
		}
	}

	@Override
	public int getItemCount() {
		return dias.size();
	}

	public class ViewHolder extends RecyclerView.ViewHolder {
		private CardView cardDia;
		private TextView tvNumDia;
		private RecyclerView rvAvisosDia;
		private LinearLayout linearLayoutDiaCalendario;

		List<Aviso> avisosDia = new ArrayList<>();

		public ViewHolder(@NonNull @NotNull View itemView) {
			super(itemView);
			cardDia = itemView.findViewById(R.id.cardDia);
			tvNumDia = itemView.findViewById(R.id.tvNumDia);
			rvAvisosDia = itemView.findViewById(R.id.rvAvisosDia);
			linearLayoutDiaCalendario = itemView.findViewById(R.id.linearLayoutDiaCalendario);
		}

		public void asignarValores(int dia) {
			tvNumDia.setText(String.valueOf(dia));

			cardDia.getLayoutParams().height = tamaño;

			rvAvisosDia.setHasFixedSize(true);
			LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
			rvAvisosDia.setLayoutManager(linearLayoutManager);

			if (usuario != null) {
				FirebaseDatabase.getInstance().getReference().child(Fecha.CALENDARIO)
						.child(fechaReferencia.toString(Fecha.FormatosFecha.aaaaMM)).child(dia + "").addChildEventListener(new ChildEventListener() {
					@Override
					public void onChildAdded(@NonNull @NotNull DataSnapshot snapshot, @Nullable String previousChildName) {
						String key = snapshot.getKey();
						String categoria = snapshot.getValue(String.class);
						if (key != null && categoria != null && Arrays.asList(Categoria.getKeysCategorias(usuario.getCategorias())).contains(categoria)) {
							FirebaseDatabase.getInstance().getReference().child(Aviso.AVISOS)
									.child(categoria).child(key).addValueEventListener(new ValueEventListener() {
								@Override
								public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
									Aviso aviso = snapshot.getValue(Aviso.class);
									if (aviso != null) {
										aviso.key = key;
										avisosDia.add(aviso);
									}
									AvisoTituloAdaptador avisoTituloAdaptador = new AvisoTituloAdaptador(context, avisosDia);
									rvAvisosDia.setAdapter(avisoTituloAdaptador);
								}

								@Override
								public void onCancelled(@NonNull @NotNull DatabaseError databaseError) {
									Log.e(databaseError.getMessage(), databaseError.getDetails());
								}
							});
						}
					}

					@Override
					public void onChildChanged(@NonNull @NotNull DataSnapshot snapshot, @Nullable String previousChildName) {

					}

					@Override
					public void onChildRemoved(@NonNull @NotNull DataSnapshot snapshot) {

					}

					@Override
					public void onChildMoved(@NonNull @NotNull DataSnapshot snapshot, @Nullable String previousChildName) {

					}

					@Override
					public void onCancelled(@NonNull @NotNull DatabaseError error) {

					}
				});
			} else {
				FirebaseDatabase.getInstance().getReference().child(Fecha.CALENDARIO)
						.child(fechaReferencia.toString(Fecha.FormatosFecha.aaaaMM)).child(dia + "").addChildEventListener(new ChildEventListener() {
					@Override
					public void onChildAdded(@NonNull @NotNull DataSnapshot snapshot, @Nullable String previousChildName) {
						String key = snapshot.getKey();
						String categoria = snapshot.getValue(String.class);
						if (key != null && categoria != null && categoria.equals(Categoria.ID_PADRE)) {
							FirebaseDatabase.getInstance().getReference().child(Aviso.AVISOS)
									.child(categoria).child(key).addValueEventListener(new ValueEventListener() {
								@Override
								public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
									Aviso aviso = snapshot.getValue(Aviso.class);
									if (aviso != null) {
										aviso.key = key;
										avisosDia.add(aviso);
									}
									AvisoTituloAdaptador avisoTituloAdaptador = new AvisoTituloAdaptador(context, avisosDia);
									rvAvisosDia.setAdapter(avisoTituloAdaptador);
								}

								@Override
								public void onCancelled(@NonNull @NotNull DatabaseError databaseError) {
									Log.e(databaseError.getMessage(), databaseError.getDetails());
								}
							});
						}
					}

					@Override
					public void onChildChanged(@NonNull @NotNull DataSnapshot snapshot, @Nullable String previousChildName) {

					}

					@Override
					public void onChildRemoved(@NonNull @NotNull DataSnapshot snapshot) {

					}

					@Override
					public void onChildMoved(@NonNull @NotNull DataSnapshot snapshot, @Nullable String previousChildName) {

					}

					@Override
					public void onCancelled(@NonNull @NotNull DatabaseError error) {

					}
				});
			}
		}

		public void asignarValoresInvisibles() {
			cardDia.setVisibility(View.INVISIBLE);
		}
	}
}
