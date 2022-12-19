package es.parroquiasanleandro.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.navigation.NavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import es.parroquiasanleandro.Aviso;
import es.parroquiasanleandro.Menu;
import es.parroquiasanleandro.R;
import es.parroquiasanleandro.Url;
import es.parroquiasanleandro.Usuario;
import es.parroquiasanleandro.adaptadores.AvisoAdaptador;
import es.parroquiasanleandro.fecha.Fecha;
import es.parroquiasanleandro.utils.ItemViewModel;

public class FragmentInicio extends Fragment {
	private Context context;

	private TextView tvCitaBiblica;
	private RecyclerView rvAvisosSemana;
	private TextView tvAvisosSemanales;
	private TextView tvMes;
	//private LinearLayout linearLayoutCalendario;
	//private RecyclerView rvCalendario;

	private ItemViewModel viewModel;
	private ActionBar actionBar;
	private NavigationView navView;

	private Fecha fechaReferencia;
	//private List<Integer> dias;

	//private Usuario usuario;

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
		//linearLayoutCalendario = view.findViewById(R.id.linearLayoutCalendario);
		//rvCalendario = view.findViewById(R.id.rvCalendario);

		rvAvisosSemana.setHasFixedSize(true);
		LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
		rvAvisosSemana.setLayoutManager(linearLayoutManager);

		avisos = new ArrayList<>();
		//dias = new ArrayList<>();

		Usuario usuario = Usuario.recuperarUsuarioLocal(context);
		obtenerCitaBiblica(Url.obtenerCitaBliblica);

		/*tvMes.setText(fechaReferencia.toString(Fecha.FormatosFecha.MMMM_aaaa));

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


		if (usuario.getId() != null) {
			rvCalendario.setAdapter(new DiaAdaptador(context, dias, fechaReferencia, Usuario.recuperarUsuarioLocal(context), DiaAdaptador.TAMAÑO_GRANDE));
		} else {
			rvCalendario.setAdapter(new DiaAdaptador(context, dias, fechaReferencia, null, DiaAdaptador.TAMAÑO_GRANDE));
		}*/
		//setCalendario(usuario);

		//Obtener los avisos de esta semana
		RequestQueue requestQueue = Volley.newRequestQueue(context);
		requestQueue.add(new StringRequest(Request.Method.POST, Url.obtenerAvisosSemana, result -> {
			try {
				JSONObject jsonResult = new JSONObject(result);
				if(!jsonResult.getBoolean("error")){
					JSONArray jsonArrayAvisos = jsonResult.getJSONArray("avisos");
					avisos.addAll(Aviso.JSONArrayToAviso(jsonArrayAvisos));

					mostrarAvisosSemanales();
				}
			} catch (JSONException e) {
				Toast.makeText(context, "Se ha producido un error en el servidor al recuperar los avisos de esta semana", Toast.LENGTH_SHORT).show();
				e.printStackTrace();
			}
		}, error -> {
			Toast.makeText(context, "Se ha producido un error al conectar con el servidor", Toast.LENGTH_SHORT).show();
		}) {
			@Override
			protected Map<String, String> getParams() {
				Map<String,String> parametros = new HashMap<>();
				parametros.put("idUsuario",usuario.getId());
				return parametros;
			}
		});

		/*linearLayoutCalendario.setOnClickListener(view1 -> {
			iniciarFragmentCalendario();
		});*/

		/*rvCalendario.setOnClickListener(view1 -> {
			iniciarFragmentCalendario();
		});*/
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

	/*public void setCalendario(Usuario user) {
		tvMes.setText(fechaReferencia.toString(Fecha.FormatosFecha.MMMM_aaaa));

		dias.clear();
		for (int i = 1; i <= fechaReferencia.diaSemana.getNumeroDia() - 1; i++) {
			dias.add(0);
		}

		int numDiasMes = fechaReferencia.mes.getNumDiasMes();
		for (int i = 1; i <= numDiasMes; i++) {
			dias.add(i);
		}

		Log.d("DIAS",dias.size() + " ");
		if (user != null) {
			rvCalendario.setAdapter(new DiaAdaptador(context, dias, fechaReferencia, Usuario.recuperarUsuarioLocal(context), DiaAdaptador.TAMAÑO_GRANDE));
			Log.d("CALENDAR",dias.size() + "AAAAAAAAAAA");
		} else {
			rvCalendario.setAdapter(new DiaAdaptador(context, dias, fechaReferencia, null, DiaAdaptador.TAMAÑO_GRANDE));
			Log.d("CALENDAR",dias.size() + "BBBBBBBB");
		}
	}*/

	public void iniciarFragmentCalendario(){
		FragmentManager fragmentManager = getParentFragmentManager();
		actionBar = viewModel.getActionBar();
		Menu.iniciarFragmentCalendario(fragmentManager, actionBar);
		navView = viewModel.getNavView();
		Menu.asignarIconosMenu(navView,Menu.FRAGMENT_CALENDARIO);
	}

	public void mostrarAvisosSemanales() {
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