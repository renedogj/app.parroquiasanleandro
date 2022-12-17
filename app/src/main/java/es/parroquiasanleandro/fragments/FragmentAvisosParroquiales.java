package es.parroquiasanleandro.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.jetbrains.annotations.NotNull;
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
import es.parroquiasanleandro.activitys.ActivityNuevoAviso;
import es.parroquiasanleandro.adaptadores.AvisoAdaptador;
import es.parroquiasanleandro.utils.ItemViewModel;


public class FragmentAvisosParroquiales extends Fragment {
    private Context context;

    private RecyclerView rvAvisos;
    private FloatingActionButton bttnNuevoAviso;

    Usuario usuario;
    List<Aviso> avisos;

    private ItemViewModel vmIds;

    public FragmentAvisosParroquiales() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        context = getContext();

        usuario = Usuario.recuperarUsuarioLocal(context);

        vmIds = new ViewModelProvider(requireActivity()).get(ItemViewModel.class);
        vmIds.setIdFragmentActual(Menu.FRAGMENT_AVISOS);
        vmIds.addIdFragmentActual();
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_avisos_parroquiales, container, false);

        rvAvisos = view.findViewById(R.id.rvAvisos);
        bttnNuevoAviso = view.findViewById(R.id.bttnNuevoAviso);

        rvAvisos.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        rvAvisos.setLayoutManager(linearLayoutManager);

        avisos = new ArrayList<>();

        if(usuario.getId() == null){
            usuario.setId("0");
        }

        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(new StringRequest(Request.Method.POST, Url.obtenerAvisos, result -> {
            Log.d("Resultado",result);
            try {
                JSONObject jsonResult = new JSONObject(result);
                Log.d("jsonResult",jsonResult.toString());
                if(!jsonResult.getBoolean("error")){
                    JSONArray jsonArrayAvisos = jsonResult.getJSONArray("avisos");
                    avisos.addAll(Aviso.JSONArrayToAviso(jsonArrayAvisos));

                    if(!avisos.isEmpty()) {
                        AvisoAdaptador avisoAdaptador = new AvisoAdaptador(context, avisos);
                        rvAvisos.setAdapter(avisoAdaptador);
                    }
                }
            } catch (JSONException e) {
                Toast.makeText(context, "Se ha producido un error en el servidor al recuperar los avisos", Toast.LENGTH_SHORT).show();
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

        bttnNuevoAviso.setOnClickListener(v -> startActivity(new Intent(context, ActivityNuevoAviso.class)));

        return view;
    }
}