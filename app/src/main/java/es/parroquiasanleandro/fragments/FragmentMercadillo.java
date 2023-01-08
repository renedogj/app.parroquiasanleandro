package es.parroquiasanleandro.fragments;

import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import es.parroquiasanleandro.Articulo;
import es.parroquiasanleandro.Menu;
import es.parroquiasanleandro.R;
import es.parroquiasanleandro.Url;
import es.parroquiasanleandro.adaptadores.ArticuloAdaptador;
import es.parroquiasanleandro.utils.ItemViewModel;

public class FragmentMercadillo extends Fragment {
    private Context context;
    private ItemViewModel vmIds;

    private RecyclerView rvArticulos;

    List<Articulo> articulos;

    public FragmentMercadillo() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        context = getContext();

        vmIds = new ViewModelProvider(requireActivity()).get(ItemViewModel.class);
        vmIds.setIdFragmentActual(Menu.FRAGMENT_MERCADILLO);
        vmIds.addIdFragmentActual();
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mercadillo, container, false);

        rvArticulos = view.findViewById(R.id.rvArticulos);
        articulos = new ArrayList<>();

        rvArticulos.setHasFixedSize(true);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(context, 2);
        rvArticulos.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                if (parent.getChildLayoutPosition(view) % 2 != 0) {
                    outRect.top = 25;
                    outRect.bottom = -25;
                } else {
                    outRect.top = 0;
                }
            }
        });
        rvArticulos.setLayoutManager(gridLayoutManager);

        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(new StringRequest(Request.Method.POST, Url.obtenerArticulos, result -> {
            try {
                JSONArray jsonArrayArticulos = new JSONArray(result);
                articulos.addAll(Articulo.JSONArrayToArticulos(jsonArrayArticulos));

                if (!articulos.isEmpty()) {
                    ArticuloAdaptador articuloAdaptador = new ArticuloAdaptador(context, articulos);
                    rvArticulos.setAdapter(articuloAdaptador);
                }
            } catch (JSONException e) {
                Toast.makeText(context, "Se ha producido un error en el servidor al obtener los articulos", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        }, error -> {
            Toast.makeText(context, "Se ha producido un error al conectar con el servidor", Toast.LENGTH_SHORT).show();
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> parametros = new HashMap<>();
                parametros.put("seleccionCategoria", "0");
                parametros.put("seleccionOrden", "0");
                return parametros;
            }
        });
        return view;
    }
}
