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
import androidx.appcompat.app.ActionBar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.navigation.NavigationView;
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
	private TextView tvAvisosSemanales;
	private TextView tvMes;
	private LinearLayout linearLayoutCalendario;
	private RecyclerView rvCalendario;

	private ItemViewModel viewModel;
	private ActionBar actionBar;
	private NavigationView navView;

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

		viewModel = new ViewModelProvider(requireActivity()).get(ItemViewModel.class);
		viewModel.setIdFragmentActual(Menu.FRAGMENT_INICIO);
		viewModel.addIdFragmentActual();

		fechaReferencia = Fecha.FechaActual();
		fechaReferencia.convertirAPrimerDiaMes();
	}

	public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_inicio, container, false);

		tvCitaBiblica = view.findViewById(R.id.tvCitaBiblica);
		tvAvisosSemanales = view.findViewById(R.id.tvAvisosSemanales);
		rvAvisosSemana = view.findViewById(R.id.rvAvisosSemana);
		tvMes = view.findViewById(R.id.tvMes);
		linearLayoutCalendario = view.findViewById(R.id.linearLayoutCalendario);
		rvCalendario = view.findViewById(R.id.rvCalendario);

		rvAvisosSemana.setHasFixedSize(true);
		LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
		rvAvisosSemana.setLayoutManager(linearLayoutManager);

		avisos = new ArrayList<>();

		obtenerCitaBiblica(Url.obtenerCitaBliblica);

		tvMes.setText(fechaReferencia.toString(Fecha.FormatosFecha.MMMM_aaaa));

		dias = new ArrayList<>();
		for (int i = 1; i <= fechaReferencia.diaSemana.getNumeroDia() - 1; i++) {
			dias.add(0);
		}

		int numDiasMes = fechaReferencia.mes.getNumDiasMes();
		for (int i = 1; i <= numDiasMes; i++) {
			dias.add(i);
		}

		rvCalendario.setHasFixedSize(true);
		GridLayoutManager gridLayoutManager = new GridLayoutManager(context, 7);
		rvCalendario.setLayoutManager(gridLayoutManager);

		FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
		if (user != null) {
			Usuario usuario = Usuario.recuperarUsuarioLocal(context);

			for (Categoria categoria : usuario.getCategorias()) {
				FirebaseDatabase.getInstance().getReference().child(Aviso.AVISOS).child(categoria.key).addListenerForSingleValueEvent(new ValueEventListener() {
					@Override
					public void onDataChange(@NotNull DataSnapshot dataSnapshot) {
						guardarListAvisos(dataSnapshot);
						asignarAvisos();
					}

					@Override
					public void onCancelled(@NotNull DatabaseError databaseError) {
						Log.e(databaseError.getMessage(), databaseError.getDetails());
					}
				});
			}

			rvCalendario.setAdapter(new DiaAdaptador(context, dias, fechaReferencia, Usuario.recuperarUsuarioLocal(context), DiaAdaptador.TAMAÑO_PEQUEÑO));
		} else {
			FirebaseDatabase.getInstance().getReference().child(Aviso.AVISOS).child(Categoria.ID_PADRE).addListenerForSingleValueEvent(new ValueEventListener() {
				@Override
				public void onDataChange(@NotNull DataSnapshot dataSnapshot) {
					guardarListAvisos(dataSnapshot);
					asignarAvisos();
				}

				@Override
				public void onCancelled(@NotNull DatabaseError databaseError) {
					Log.e(databaseError.getMessage(), databaseError.getDetails());
				}
			});
			rvCalendario.setAdapter(new DiaAdaptador(context, dias, fechaReferencia, null, DiaAdaptador.TAMAÑO_PEQUEÑO));
		}

		linearLayoutCalendario.setOnClickListener(view1 -> {
			FragmentManager fragmentManager = getParentFragmentManager();
			actionBar = viewModel.getActionBar();
			Menu.iniciarFragmentCalendario(fragmentManager, actionBar);
			navView = viewModel.getNavView();
			Menu.asignarIconosMenu(navView,Menu.FRAGMENT_CALENDARIO);
		});

		rvCalendario.setOnClickListener(view1 -> {
			FragmentManager fragmentManager = getParentFragmentManager();
			actionBar = viewModel.getActionBar();
			Menu.iniciarFragmentCalendario(fragmentManager, actionBar);
			navView = viewModel.getNavView();
			Menu.asignarIconosMenu(navView,Menu.FRAGMENT_CALENDARIO);
		});
		return view;
	}

	public void obtenerCitaBiblica(String url) {
		Volley.newRequestQueue(context).add(new StringRequest(Request.Method.POST, url, response -> {
			tvCitaBiblica.setText(response);
		}, error -> {
			Toast.makeText(context, "Se ha producido un error al conectar con el servidor", Toast.LENGTH_SHORT).show();
		}) {
			@Override
			protected Map<String, String> getParams() throws AuthFailureError {
				return super.getParams();
			}
		});
	}

	public void guardarListAvisos(DataSnapshot dataSnapshot) {
		for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
			Aviso aviso = postSnapshot.getValue(Aviso.class);
			if (aviso != null) {
				int difFechas = Fecha.difereciaFecha(fechaActual, aviso.fechaInicio);
				if (difFechas >= 0 && difFechas <= 7) {
					aviso.key = postSnapshot.getKey();
					avisos.add(aviso);
				}
			}
		}
	}

	public void asignarAvisos() {
		if (!avisos.isEmpty()) {
			rvAvisosSemana.setVisibility(View.VISIBLE);
			tvAvisosSemanales.setText("Avisos Semanales");
			tvAvisosSemanales.getLayoutParams().height = -2;
			if (avisos.size() == 1) {
				rvAvisosSemana.getLayoutParams().height = -2;
			} else {
				rvAvisosSemana.getLayoutParams().height = 450;
			}
			AvisoAdaptador avisoAdaptador = new AvisoAdaptador(context, avisos);
			rvAvisosSemana.setAdapter(avisoAdaptador);
		} else {
			rvAvisosSemana.setVisibility(View.GONE);
			tvAvisosSemanales.setText("No hay ningún aviso esta semana");
			tvAvisosSemanales.getLayoutParams().height = 220;
		}
	}
}