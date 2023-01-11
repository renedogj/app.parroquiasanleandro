package es.parroquiasanleandro.fragments;

import android.app.Activity;
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
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
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
import es.parroquiasanleandro.MenuOption;
import es.parroquiasanleandro.R;
import es.parroquiasanleandro.Url;
import es.parroquiasanleandro.Usuario;
import es.parroquiasanleandro.adaptadores.AvisoAdaptador;
import es.parroquiasanleandro.adaptadores.MenuIncioAdaptador;
import es.parroquiasanleandro.utils.ItemViewModel;

public class FragmentInicio extends Fragment {
	private Context context;
	private Activity activity;

	private ItemViewModel viewModel;
	private ActionBar actionBar;
	private NavigationView navView;

	private TextView tvCitaBiblica;
	private RecyclerView rvAvisosSemana;
	private TextView tvAvisosSemanales;
	private RecyclerView rvMenu;

	List<Aviso> avisos;
	List<MenuOption> menuOptionList;

	public FragmentInicio() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		context = getContext();
		activity = getActivity();

		viewModel = new ViewModelProvider(requireActivity()).get(ItemViewModel.class);
	}

	public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_inicio, container, false);

		tvCitaBiblica = view.findViewById(R.id.tvCitaBiblica);
		tvAvisosSemanales = view.findViewById(R.id.tvAvisosSemanales);
		rvAvisosSemana = view.findViewById(R.id.rvAvisosSemana);
		rvMenu = view.findViewById(R.id.rvMenu);

		rvAvisosSemana.setHasFixedSize(true);
		LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
		rvAvisosSemana.setLayoutManager(linearLayoutManager);

		rvMenu.setHasFixedSize(true);
		GridLayoutManager gridLayoutManager = new GridLayoutManager(context,2);
		rvMenu.setLayoutManager(gridLayoutManager);

		avisos = new ArrayList<>();
		menuOptionList = MenuOption.obtenerListMenuOptions();

		Usuario usuario = Usuario.recuperarUsuarioLocal(context);

		MenuIncioAdaptador menuIncioAdaptador = new MenuIncioAdaptador(context, activity, menuOptionList, Menu.FRAGMENT_INICIO, getParentFragmentManager(), viewModel.getActionBar(), viewModel.getNavView());
		rvMenu.setAdapter(menuIncioAdaptador);

		obtenerCitaBiblica(Url.obtenerCitaBliblica);

		//Obtener los avisos de esta semana
		RequestQueue requestQueue = Volley.newRequestQueue(context);
		requestQueue.add(new StringRequest(Request.Method.POST, Url.obtenerAvisosSemana, result -> {
			try {
				JSONObject jsonResult = new JSONObject(result);
				if(!jsonResult.getBoolean("error")){
					JSONArray jsonArrayAvisos = jsonResult.getJSONArray("avisos");
					avisos.addAll(Aviso.JSONArrayToAvisos(jsonArrayAvisos));
				}
				mostrarAvisosSemanales();
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
				if(usuario.getId() != null){
					parametros.put("idUsuario",usuario.getId());
				}else {
					parametros.put("idUsuario","0");
				}
				return parametros;
			}
		});

		return view;
	}

	@Override
	public void onResume() {
		super.onResume();
		viewModel.setIdFragmentActual(Menu.FRAGMENT_INICIO);
		viewModel.addIdFragmentActual();
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