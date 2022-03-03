package com.parroquiasanleandro.fragments;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.parroquiasanleandro.Aviso;
import com.parroquiasanleandro.Categoria;
import com.parroquiasanleandro.ItemViewModel;
import com.parroquiasanleandro.Menu;
import com.parroquiasanleandro.R;
import com.parroquiasanleandro.Url;
import com.parroquiasanleandro.Usuario;
import com.parroquiasanleandro.adaptadores.AvisoAdaptador;
import com.parroquiasanleandro.adaptadores.DiaAdaptador;
import com.parroquiasanleandro.fecha.Fecha;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FragmentInicio extends Fragment {
	private Context context;

	private TextView tvCitaBiblica;
	private RecyclerView rvAvisosSemana;
	private TextView tvMes;
	private LinearLayout linearLayoutCalendario;
	private RecyclerView rvCalendario;

	private ItemViewModel vmIds;

	RequestQueue requestQueue;

	private Fecha fechaReferencia;
	private List<Integer> dias;

	List<Aviso> avisos;
	Fecha fechaActual = Fecha.FechaActual();

	public FragmentInicio() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		context = getContext();

		vmIds = new ViewModelProvider(requireActivity()).get(ItemViewModel.class);
		vmIds.setIdFragmentActual(Menu.FRAGMENT_INICIO);
		vmIds.addIdFragmentActual();

		fechaReferencia = Fecha.FechaActual();
		fechaReferencia.convertirAPrimerDiaMes();
	}

	public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_inicio, container, false);

		tvCitaBiblica = view.findViewById(R.id.tvCitaBiblica);
		rvAvisosSemana = view.findViewById(R.id.rvAvisosSemana);
		tvMes = view.findViewById(R.id.tvMes);
		linearLayoutCalendario = view.findViewById(R.id.linearLayoutCalendario);
		rvCalendario = view.findViewById(R.id.rvCalendario);

		rvAvisosSemana.setHasFixedSize(true);
		LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
		rvAvisosSemana.setLayoutManager(linearLayoutManager);

		avisos = new ArrayList<>();

		obtenerCitaBiblica(Url.obtenerCitaBliblica);

		FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
		if (user != null) {
			Usuario usuario = Usuario.recuperarUsuarioLocal(context);

			for (Categoria categoria : usuario.getCategorias()) {
				FirebaseDatabase.getInstance().getReference().child(Aviso.AVISOS).child(categoria.key).addListenerForSingleValueEvent(new ValueEventListener() {
					@Override
					public void onDataChange(@NotNull DataSnapshot dataSnapshot) {
						for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
							Aviso aviso = postSnapshot.getValue(Aviso.class);
							if (aviso != null) {
								if(Fecha.difereciaFecha(fechaActual,aviso.fechaInicio) <= 7){
									aviso.key = postSnapshot.getKey();
									avisos.add(aviso);
								}

							}
						}
						if(!avisos.isEmpty()) {
							AvisoAdaptador avisoAdaptador = new AvisoAdaptador(context, avisos);
							rvAvisosSemana.setAdapter(avisoAdaptador);
						}
					}

					@Override
					public void onCancelled(@NotNull DatabaseError databaseError) {
						Log.e(databaseError.getMessage(), databaseError.getDetails());
					}
				});
			}
		}else{
			FirebaseDatabase.getInstance().getReference().child(Aviso.AVISOS).child("A").addListenerForSingleValueEvent(new ValueEventListener() {
				@Override
				public void onDataChange(@NotNull DataSnapshot dataSnapshot) {
					for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
						Aviso aviso = postSnapshot.getValue(Aviso.class);
						if (aviso != null) {
							if(Fecha.difereciaFecha(fechaActual,aviso.fechaInicio) <= 7){
								aviso.key = postSnapshot.getKey();
								avisos.add(aviso);
							}
						}
					}
					if(!avisos.isEmpty()) {
						AvisoAdaptador avisoAdaptador = new AvisoAdaptador(context, avisos);
						rvAvisosSemana.setAdapter(avisoAdaptador);
					}
				}

				@Override
				public void onCancelled(@NotNull DatabaseError databaseError) {
					Log.e(databaseError.getMessage(), databaseError.getDetails());
				}
			});
		}

		tvMes.setText(fechaReferencia.toString(Fecha.FormatosFecha.MMMM_aaaa));

		dias = new ArrayList<>();
		for(int i = 1; i <= fechaReferencia.diaSemana.getNumeroDia()-1; i++){
			dias.add(0);
		}

		int numDiasMes = fechaReferencia.mes.getNumDiasMes();
		for (int i = 1; i <= numDiasMes; i++) {
			dias.add(i);
		}

		rvCalendario.setHasFixedSize(true);
		GridLayoutManager gridLayoutManager = new GridLayoutManager(context, 7);
		rvCalendario.setLayoutManager(gridLayoutManager);
		if (user != null) {
			rvCalendario.setAdapter(new DiaAdaptador(context, dias,fechaReferencia,Usuario.recuperarUsuarioLocal(context)));
		}else{
			rvCalendario.setAdapter(new DiaAdaptador(context, dias,fechaReferencia,null));
		}

		/*linearLayoutCalendario.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				//Menu.iniciarFragmentCalendario(fragmentManager,actionBar);
			}
		});*/
		return view;
	}

	public void obtenerCitaBiblica(String url) {
		StringRequest stringRequest = new StringRequest(Request.Method.POST, url, response -> {
			tvCitaBiblica.setText(response);
		}, error -> {
			Toast.makeText(context, "Se ha producido un error al conectar con el servidor", Toast.LENGTH_SHORT).show();
		}) {
			@Override
			protected Map<String, String> getParams() throws AuthFailureError {
				return super.getParams();
			}
		};

		requestQueue = Volley.newRequestQueue(context);
		requestQueue.add(stringRequest);
	}
}