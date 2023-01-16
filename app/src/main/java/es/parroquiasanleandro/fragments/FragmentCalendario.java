package es.parroquiasanleandro.fragments;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

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
import es.parroquiasanleandro.adaptadores.DiaAdaptador;
import es.parroquiasanleandro.utils.ItemViewModel;
import es.renedogj.fecha.Fecha;
import es.renedogj.fecha.Mes;
import es.renedogj.monthpicker.MonthPicker;

public class FragmentCalendario extends Fragment {
	private Context context;
	private ItemViewModel viewModel;

	private RecyclerView rvCalendario;
	private TextView tvMes;
	private TextView tvMesAnterior;
	private TextView tvMesSiguiente;

	private Fecha fechaReferencia;
	//private List<Integer> dias;
	private List<Fecha> fechas;
	private List<Aviso> avisos;

	private Usuario usuario;

	public FragmentCalendario() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		context = getContext();
		viewModel = new ViewModelProvider(requireActivity()).get(ItemViewModel.class);

		usuario = Usuario.recuperarUsuarioLocal(context);

		fechaReferencia = Fecha.FechaPrimerDiaMesActual();
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

		//dias = new ArrayList<>();
		fechas = new ArrayList<>();
		avisos = new ArrayList<>();

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

		tvMes.setOnClickListener(v -> {
			MonthPicker monthPicker = new MonthPicker(context);
			monthPicker.setMesesAbr(Mes.getAbreviaturas(Mes.FormatosMes.Mes_));
			monthPicker.setPositiveButton((month, startDate, endDate, year, monthLabel) -> {
				fechaReferencia.setMes(month - 1);
				fechaReferencia.convertirAPrimerDiaMes();
				fechaReferencia.año = year;
				tvMes.setText(fechaReferencia.toString(Fecha.FormatosFecha.MMMM_aaaa));
			});
			monthPicker.setNegativeButton(Dialog::dismiss).show();
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

		/*dias.clear();
		for (int i = 1; i <= fechaReferencia.diaSemana.getNumeroDia() - 1; i++) {
			dias.add(0);
		}

		int numDiasMes = fechaReferencia.mes.getNumDiasMes();
		for (int i = 1; i <= numDiasMes; i++) {
			dias.add(i);
		}*/



		fechas.clear();
		for (int i = 1; i <= fechaReferencia.diaSemana.getNumeroDia() -1; i++) {
			Fecha auxFecha = new Fecha(fechaReferencia.dia,fechaReferencia.mes,fechaReferencia.año);
			auxFecha.sumDias(-i);
			fechas.add(auxFecha);
		}

		for (int i = 0; i <= fechaReferencia.mes.getNumDiasMes() -1; i++) {
			Fecha auxFecha = new Fecha(fechaReferencia.dia,fechaReferencia.mes,fechaReferencia.año);
			auxFecha.sumDias(i);
			fechas.add(auxFecha);
		}

		Fecha fechaFinMes = new Fecha(fechaReferencia.dia, fechaReferencia.mes, fechaReferencia.año);
		fechaFinMes.dia = fechaFinMes.mes.getNumDiasMes();
		fechaFinMes.actualizarDiaSemana();
		for (int i = 0; i < 7-fechaFinMes.diaSemana.getNumeroDia(); i++) {
			Fecha auxFecha = new Fecha(fechaFinMes.dia,fechaFinMes.mes,fechaFinMes.año);
			auxFecha.sumDias(i);
			fechas.add(auxFecha);

		}

		obtenerAvisos();

		if (usuario.getId() != null) {
			rvCalendario.setAdapter(new DiaAdaptador(context, fechas, avisos, fechaReferencia, usuario));
		} else {
			rvCalendario.setAdapter(new DiaAdaptador(context, fechas, avisos, fechaReferencia, null));
		}
	}

	public void obtenerAvisos(){
		RequestQueue requestQueue = Volley.newRequestQueue(context);
		requestQueue.add(new StringRequest(Request.Method.POST, Url.obtenerAvisosCalendario, result -> {
			Log.d("Resultado",result);
			try {
				JSONObject jsonResult = new JSONObject(result);
				if(!jsonResult.getBoolean("error")){
					Log.d("Resultado",jsonResult.getJSONArray("avisos").toString());
					JSONArray jsonArray = jsonResult.getJSONArray("avisos");
					avisos = Aviso.JSONArrayToAvisos(jsonResult.getJSONArray("avisos"));
				}else{
					Toast.makeText(context, "Correo o contraseña incorrecta", Toast.LENGTH_SHORT).show();
				}
			} catch (JSONException e) {
				Toast.makeText(context, "Se ha producido un error en el servidor al iniciar sesion", Toast.LENGTH_SHORT).show();
				e.printStackTrace();
			}
		}, error -> {
			Toast.makeText(context, "Se ha producido un error al conectar con el servidor", Toast.LENGTH_SHORT).show();
		}) {
			@Override
			protected Map<String, String> getParams() {
				Map<String,String> parametros = new HashMap<>();
				parametros.put("idUsuario",usuario.getId());
				parametros.put("fechaInicio",fechas.get(0).toString(Fecha.FormatosFecha.aaaa_MM_dd_HH_mm_ss));
				parametros.put("fechaFin",fechas.get(fechas.size()-1).toString(Fecha.FormatosFecha.aaaa_MM_dd_HH_mm_ss));
				return parametros;
			}
		});
	}
}