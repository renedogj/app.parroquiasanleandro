package es.parroquiasanleandro.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import es.parroquiasanleandro.Menu;
import es.parroquiasanleandro.R;
import es.parroquiasanleandro.Usuario;
import es.parroquiasanleandro.adaptadores.DiaAdaptador;
import es.parroquiasanleandro.fecha.Fecha;
import es.parroquiasanleandro.utils.ItemViewModel;

;

public class FragmentCalendario extends Fragment {
	private Context context;
	private ItemViewModel viewModel;

	private RecyclerView rvCalendario;
	private TextView tvMes;
	private TextView tvMesAnterior;
	private TextView tvMesSiguiente;

	private Fecha fechaReferencia;
	private List<Integer> dias;

	private Usuario usuario;

	public FragmentCalendario() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		context = getContext();
		viewModel = new ViewModelProvider(requireActivity()).get(ItemViewModel.class);

		usuario = Usuario.recuperarUsuarioLocal(context);

		fechaReferencia = Fecha.FechaActual();
		fechaReferencia.convertirAPrimerDiaMes();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_calendario, container, false);

		rvCalendario = view.findViewById(R.id.rvCalendario);
		tvMes = view.findViewById(R.id.tvMes);
		tvMesAnterior = view.findViewById(R.id.tvMesAnterior);
		tvMesSiguiente = view.findViewById(R.id.tvMesSiguiente);

		rvCalendario.setHasFixedSize(false);
		GridLayoutManager gridLayoutManager = new GridLayoutManager(context, 7);
		rvCalendario.setLayoutManager(gridLayoutManager);

		dias = new ArrayList<>();

		setCalendario(usuario);

		tvMesAnterior.setOnClickListener(view1 -> {
			fechaReferencia.sumMeses(-1);
			fechaReferencia.actualizarDiaSemana();
			setCalendario(usuario);
		});

		tvMesSiguiente.setOnClickListener(view12 -> {
			fechaReferencia.sumMeses(1);
			fechaReferencia.actualizarDiaSemana();
			setCalendario(usuario);
		});

		return view;
	}

	@Override
	public void onResume() {
		super.onResume();
		viewModel.setIdFragmentActual(Menu.FRAGMENT_CALENDARIO);
		viewModel.addIdFragmentActual();
	}

	public void setCalendario(Usuario usuario) {
		tvMes.setText(fechaReferencia.toString(Fecha.FormatosFecha.MMMM_aaaa));

		dias.clear();
		for (int i = 1; i <= fechaReferencia.diaSemana.getNumeroDia() - 1; i++) {
			dias.add(0);
		}

		int numDiasMes = fechaReferencia.mes.getNumDiasMes();
		for (int i = 1; i <= numDiasMes; i++) {
			dias.add(i);
		}

		if (usuario.getId() != null) {
			rvCalendario.setAdapter(new DiaAdaptador(context, dias, fechaReferencia, Usuario.recuperarUsuarioLocal(context), DiaAdaptador.TAMAÑO_GRANDE));
		} else {
			rvCalendario.setAdapter(new DiaAdaptador(context, dias, fechaReferencia, null, DiaAdaptador.TAMAÑO_GRANDE));
		}
	}
}