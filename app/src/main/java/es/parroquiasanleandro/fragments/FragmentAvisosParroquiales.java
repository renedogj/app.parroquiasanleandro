package es.parroquiasanleandro.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.cardview.widget.CardView;
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
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import es.parroquiasanleandro.Aviso;
import es.parroquiasanleandro.Grupo;
import es.parroquiasanleandro.Menu;
import es.parroquiasanleandro.R;
import es.parroquiasanleandro.Url;
import es.parroquiasanleandro.Usuario;
import es.parroquiasanleandro.activitys.ActivityNavigation;
import es.parroquiasanleandro.activitys.ActivityNuevoAviso;
import es.parroquiasanleandro.adaptadores.AvisoAdaptador;
import es.parroquiasanleandro.utils.ItemViewModel;

public class FragmentAvisosParroquiales extends Fragment {
    private Context context;

    private CardView cardFiltroAvisos;
    private Spinner spinnerGrupo;
    private RecyclerView rvAvisos;
    private FloatingActionButton bttnNuevoAviso;

    Usuario usuario;
    List<Aviso> avisos;

    private ItemViewModel viewModel;

    public FragmentAvisosParroquiales() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getContext();
        viewModel = new ViewModelProvider(requireActivity()).get(ItemViewModel.class);

        usuario = Usuario.recuperarUsuarioLocal(context);
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_avisos_parroquiales, container, false);

        cardFiltroAvisos = view.findViewById(R.id.cardFiltroAvisos);
        spinnerGrupo = view.findViewById(R.id.spinnerGrupo);
        rvAvisos = view.findViewById(R.id.rvAvisos);
        bttnNuevoAviso = view.findViewById(R.id.bttnNuevoAviso);

        rvAvisos.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        rvAvisos.setLayoutManager(linearLayoutManager);

        avisos = new ArrayList<>();

        List<String> nombreGruposAdministrados = new ArrayList<>();
        if (usuario.getGruposSeguidos() != null && usuario.getGruposSeguidos().length > 1) {
            nombreGruposAdministrados.add("Todos los grupos");
            nombreGruposAdministrados.addAll(Arrays.asList(Grupo.getNombreGrupos(usuario.getGruposSeguidos())));
            ArrayAdapter<String> adapter = new ArrayAdapter<>(context, R.layout.item_spinner_grupo, nombreGruposAdministrados);
            spinnerGrupo.setAdapter(adapter);
            spinnerGrupo.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_END);
        } else {
            cardFiltroAvisos.setVisibility(View.GONE);
        }

        if (usuario.esAdministrador) {
            bttnNuevoAviso.setVisibility(View.VISIBLE);
        }

        spinnerGrupo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0) {
                    Grupo grupo = usuario.getGruposSeguidos()[position - 1];
                    List<Aviso> avisosFiltrados = new ArrayList<>();
                    for (Aviso aviso : avisos) {
                        if (aviso.idGrupo.equals(grupo.id)) {
                            avisosFiltrados.add(aviso);
                        }
                    }
                    //if(avisosFiltrados.isEmpty()){

                    //}else{
                    AvisoAdaptador avisoAdaptador = new AvisoAdaptador(context, avisosFiltrados);
                    rvAvisos.setAdapter(avisoAdaptador);
                    //}
                } else {
                    AvisoAdaptador avisoAdaptador = new AvisoAdaptador(context, avisos);
                    rvAvisos.setAdapter(avisoAdaptador);
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        bttnNuevoAviso.setOnClickListener(v -> {
            Intent intent = new Intent(context, ActivityNuevoAviso.class);
            startActivity(intent);
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        ActivityNavigation.actionBar.setTitle(Menu.AVISOS);
        viewModel.setIdFragmentActual(Menu.FRAGMENT_AVISOS);
        viewModel.addIdFragmentActual();
        obtenerMostrarAvisos();
    }

    public void obtenerMostrarAvisos() {
        avisos.clear();
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(new StringRequest(Request.Method.POST, Url.obtenerAvisos, result -> {
            try {
                JSONObject jsonResult = new JSONObject(result);
                if (!jsonResult.getBoolean("error")) {
                    JSONArray jsonArrayAvisos = jsonResult.getJSONArray("avisos");
                    avisos.addAll(Aviso.JSONArrayToAvisos(jsonArrayAvisos));

                    AvisoAdaptador avisoAdaptador = new AvisoAdaptador(context, avisos);
                    rvAvisos.setAdapter(avisoAdaptador);
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
                Map<String, String> parametros = new HashMap<>();
                if (usuario.getId() != null) {
                    parametros.put("idUsuario", usuario.getId());
                } else {
                    parametros.put("idUsuario", "0");
                }
                return parametros;
            }
        });
    }
}