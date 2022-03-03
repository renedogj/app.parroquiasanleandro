package com.parroquiasanleandro.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.parroquiasanleandro.Categoria;
import com.parroquiasanleandro.ItemViewModel;
import com.parroquiasanleandro.Menu;
import com.parroquiasanleandro.R;
import com.parroquiasanleandro.Url;
import com.parroquiasanleandro.adaptadores.CategoriaAdaptador;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FragmentCategorias extends Fragment {
    private Context context;

    public static RecyclerView rvCategorias;

    List<Categoria> categorias;

    RequestQueue requestQueue;

    private ItemViewModel vmIds;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        context = getContext();

        vmIds = new ViewModelProvider(requireActivity()).get(ItemViewModel.class);
        vmIds.setIdFragmentActual(Menu.FRAGMENT_CATEGORIAS);
        vmIds.addIdFragmentActual();
        vmIds.setCategoriaActual("A");
        vmIds.addIdCategoria();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_categorias, container, false);

        rvCategorias = view.findViewById(R.id.rvCategorias);

        rvCategorias.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        rvCategorias.setLayoutManager(linearLayoutManager);

        categorias = new ArrayList<>();

        //Obtener JSON de las categorias del servidor
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Url.obtenerCategorias, new Response.Listener<JSONArray>(){
            @Override
            public void onResponse(JSONArray response) {
                JSONObject jsonObject;
                categorias.clear();
                for (int i = 0; i < response.length(); i++) {
                    try {
                        jsonObject = response.getJSONObject(i);
                        Categoria categoria = new Categoria(jsonObject.getString("id"),jsonObject.getString("nombre"),jsonObject.getString("color"));
                        categorias.add(categoria);
                    } catch (JSONException e) {
                        Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
                CategoriaAdaptador categoriaAdaptador = new CategoriaAdaptador(context, categorias,"A",rvCategorias,vmIds);
                rvCategorias.setAdapter(categoriaAdaptador);
            }
        }, error -> {
            Toast.makeText(context, "Se ha producido un error al conectar con el servidor", Toast.LENGTH_SHORT).show();
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return super.getParams();
            }
        };

        requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(jsonArrayRequest);

        return view;
    }
}