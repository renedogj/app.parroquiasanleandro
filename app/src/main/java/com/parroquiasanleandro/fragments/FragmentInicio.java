package com.parroquiasanleandro.fragments;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.parroquiasanleandro.Aviso;
import com.parroquiasanleandro.ItemViewModel;
import com.parroquiasanleandro.Menu;
import com.parroquiasanleandro.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FragmentInicio extends Fragment {
	private Context context;

	private TextView tvCitaBiblica;
	private RecyclerView rvAvisosSemana;

	private ItemViewModel vmIds;

	RequestQueue requestQueue;

	List<Aviso> avisos;

	public FragmentInicio() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		context = getContext();

		vmIds = new ViewModelProvider(requireActivity()).get(ItemViewModel.class);
		vmIds.setIdFragmentActual(Menu.FRAGMENT_INICIO);
		vmIds.addIdFragmentActual();
	}

	public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_inicio, container, false);

		tvCitaBiblica = view.findViewById(R.id.tvCitaBiblica);
		rvAvisosSemana = view.findViewById(R.id.rvAvisosSemana);
		rvAvisosSemana.setHasFixedSize(true);
		LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
		rvAvisosSemana.setLayoutManager(linearLayoutManager);

		avisos = new ArrayList<>();

		buscar("http://192.168.1.2/administracion.parroquiaSanLeandro/obtenerCitaBiblicaActual.php");

		return view;
	}

	public void buscar(String url) {
		StringRequest stringRequest = new StringRequest(Request.Method.POST, url, response -> {
			try {
				JSONObject respuesta = new JSONObject(response);
				Log.d("JSONArray", respuesta.toString());

				tvCitaBiblica.setText(respuesta.getString("cita"));
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}, error -> {
			Toast.makeText(context, "ERROR DE CONEXION", Toast.LENGTH_SHORT).show();
			Log.e("ERROR DE CONEXION", error.toString());
			Log.e("ERROR DE CONEXION", error.getMessage());
		}) {
			@Override
			protected Map<String, String> getParams() throws AuthFailureError {
				//Map<String, String> parametros = new HashMap<String, String>();
				//parametros.put("idProducto","11");
				//return parametros;
				return super.getParams();
			}
		};

		requestQueue = Volley.newRequestQueue(context);
		requestQueue.add(stringRequest);
	}
}