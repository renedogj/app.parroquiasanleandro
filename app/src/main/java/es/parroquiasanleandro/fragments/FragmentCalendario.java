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

	private RecyclerView rvCalendario;
	private TextView tvMes;
	private TextView tvMesAnterior;
	private TextView tvMesSiguiente;

	private Fecha fechaReferencia;
	private List<Integer> dias;

	private ItemViewModel vmIds;

	public FragmentCalendario() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		context = getContext();

		vmIds = new ViewModelProvider(requireActivity()).get(ItemViewModel.class);
		vmIds.setIdFragmentActual(Menu.FRAGMENT_CALENDARIO);
		vmIds.addIdFragmentActual();

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

		//FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
		Usuario user = null;
		dias = new ArrayList<>();

		setCalendario(user);


		tvMesAnterior.setOnClickListener(view1 -> {
			fechaReferencia.sumMeses(-1);
			fechaReferencia.actualizarDiaSemana();
			setCalendario(user);
		});

		tvMesSiguiente.setOnClickListener(view12 -> {
			fechaReferencia.sumMeses(1);
			fechaReferencia.actualizarDiaSemana();
			setCalendario(user);
		});

		return view;
	}

	public void setCalendario(Usuario user) {
		tvMes.setText(fechaReferencia.toString(Fecha.FormatosFecha.MMMM_aaaa));

		dias.clear();
		for (int i = 1; i <= fechaReferencia.diaSemana.getNumeroDia() - 1; i++) {
			dias.add(0);
		}

		int numDiasMes = fechaReferencia.mes.getNumDiasMes();
		for (int i = 1; i <= numDiasMes; i++) {
			dias.add(i);
		}

		if (user != null) {
			rvCalendario.setAdapter(new DiaAdaptador(context, dias, fechaReferencia, Usuario.recuperarUsuarioLocal(context), DiaAdaptador.TAMAÑO_GRANDE));
		} else {
			rvCalendario.setAdapter(new DiaAdaptador(context, dias, fechaReferencia, null, DiaAdaptador.TAMAÑO_GRANDE));
		}
	}
}