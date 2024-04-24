package es.parroquiasanleandro.fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import es.parroquiasanleandro.Aviso;
import es.parroquiasanleandro.Menu;
import es.parroquiasanleandro.MenuOption;
import es.parroquiasanleandro.R;
import es.parroquiasanleandro.Url;
import es.parroquiasanleandro.Usuario;
import es.parroquiasanleandro.activitys.ActivityNavigation;
import es.parroquiasanleandro.adaptadores.AvisoAdaptador;
import es.parroquiasanleandro.adaptadores.MenuIncioAdaptador;
import es.parroquiasanleandro.utils.ItemViewModel;
import es.renedogj.fecha.Fecha;

public class FragmentInicio extends Fragment {
    public static final String CITA_BIBLICA = "citaBiblica";
    public static final String FECHA = "fecha";

    private Context context;
    private Activity activity;

    private ItemViewModel viewModel;

    private TextView tvCitaBiblica;
    private RecyclerView rvAvisosSemana;
    private TextView tvAvisosSemanales;
    private RecyclerView rvMenu;
    private ImageView imgFacebook;
    private ImageView imgYoutube;
    private ImageView imgInstagram;
    private ImageView imgTwitter;
    private ImageView imgEnlace;

    List<Aviso> avisos;
    List<MenuOption> menuOptionList;

    private Usuario usuario;

    public FragmentInicio() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getContext();
        activity = getActivity();

        viewModel = new ViewModelProvider(requireActivity()).get(ItemViewModel.class);
    }

    @SuppressLint("IntentReset")
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_inicio, container, false);

        tvCitaBiblica = view.findViewById(R.id.tvCitaBiblica);
        tvAvisosSemanales = view.findViewById(R.id.tvAvisosSemanales);
        rvAvisosSemana = view.findViewById(R.id.rvAvisosSemana);
        rvMenu = view.findViewById(R.id.rvMenu);
        imgFacebook = view.findViewById(R.id.imgFacebook);
        imgYoutube = view.findViewById(R.id.imgYoutube);
        imgInstagram = view.findViewById(R.id.imgInstagram);
        imgTwitter = view.findViewById(R.id.imgTwitter);
        imgEnlace = view.findViewById(R.id.imgEnlace);

        rvAvisosSemana.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        rvAvisosSemana.setLayoutManager(linearLayoutManager);

        rvMenu.setHasFixedSize(true);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(context, 2);
        rvMenu.setLayoutManager(gridLayoutManager);

        avisos = new ArrayList<>();
        menuOptionList = MenuOption.obtenerListMenuOptions();

        usuario = Usuario.recuperarUsuarioLocal(context);

        /*if(usuario.esAdministrador){
            if(Grupo.getGrupoPadre().isGrupoAdministrado(usuario)){
                menuOptionList.add(new MenuOption(Menu.ADMINISTRACION, Menu.FRAGMENT_ADMINISTRACION, R.drawable.ic_app, FragmentAdministracion.class));
            }
        }*/

        MenuIncioAdaptador menuIncioAdaptador = new MenuIncioAdaptador(context, activity, menuOptionList, Menu.FRAGMENT_INICIO);
        rvMenu.setAdapter(menuIncioAdaptador);

        obtenerCitaBiblica();

        imgFacebook.setOnClickListener(v -> {
            Uri uri = Uri.parse("https://www.facebook.com/parroquiasanleandro");
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            intent.setPackage("com.facebook.katana");

            try {
                startActivity(intent);
            } catch (ActivityNotFoundException e) {
                //No encontró la aplicación, abre la versión web.
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/parroquiasanleandro")));
            }
        });

        imgYoutube.setOnClickListener(v -> {
            Uri uri = Uri.parse("https://www.youtube.com/ParroquiaSanLeandro");
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            intent.setPackage("com.google.android.youtube");

            try {
                startActivity(intent);
            } catch (ActivityNotFoundException e) {
                //No encontró la aplicación, abre la versión web.
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/ParroquiaSanLeandro")));
            }
        });

        imgInstagram.setOnClickListener(v -> {
            Uri uri = Uri.parse("https://instagram.com/_u/psanleandro");
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            intent.setPackage("com.instagram.android");

            try {
                startActivity(intent);
            } catch (ActivityNotFoundException e) {
                //No encontró la aplicación, abre la versión web.
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://instagram.com/psanleandro")));
            }
        });

        imgTwitter.setOnClickListener(v -> {
            Uri uri = Uri.parse("https://twitter.com/psanleandro");
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            intent.setType("application/twitter");

            try {
                startActivity(intent);
            } catch (ActivityNotFoundException e) {
                //No encontró la aplicación, abre la versión web.
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://twitter.com/psanleandro")));
            }
        });

        imgEnlace.setOnClickListener(v -> {
            Uri uri = Uri.parse("https://www.parroquiasanleandro.es/");
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        ActivityNavigation.actionBar.setTitle(Menu.INICIO);
        viewModel.setIdFragmentActual(Menu.FRAGMENT_INICIO);
        viewModel.addIdFragmentActual();
        obtenerAvisosSemanales();
    }

    public void obtenerCitaBiblica() {
        SharedPreferences sharedPreferences = context.getSharedPreferences(CITA_BIBLICA, Context.MODE_PRIVATE);

        String sFecha = sharedPreferences.getString(FECHA, null);
        String citaBiblica = null;

        if(sFecha != null) {
            Fecha fecha = Fecha.stringToFecha(sFecha, Fecha.FormatosFecha.aaaa_MM_dd);
            if (fecha.esHoy()) {
                citaBiblica = sharedPreferences.getString(CITA_BIBLICA, null);
            }
        }

        if(citaBiblica == null){
            Volley.newRequestQueue(context).add(new StringRequest(Request.Method.POST, Url.obtenerCitaBliblica, response -> {
                tvCitaBiblica.setText(response);

                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(FECHA, Fecha.FechaActual().toString(Fecha.FormatosFecha.aaaa_MM_dd));
                editor.putString(CITA_BIBLICA, response);
                editor.apply();
            }, error -> {
                Log.e("Error Cita Biblica", Objects.requireNonNull(error.getMessage()));
                Toast.makeText(context, "Se ha producido un error al conectar con el servidor", Toast.LENGTH_SHORT).show();
            }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    return super.getParams();
                }
            });
        }else{
            tvCitaBiblica.setText(citaBiblica);
        }
    }

    public void mostrarAvisosSemanales() {
        if (!avisos.isEmpty()) {
            rvAvisosSemana.setVisibility(View.VISIBLE);
            tvAvisosSemanales.setText("Avisos Semanales");
            tvAvisosSemanales.getLayoutParams().height = -2;
            if (avisos.size() == 1) {
                rvAvisosSemana.getLayoutParams().height = -2;
            } else {
                //rvAvisosSemana.getLayoutParams().height = 450;
                rvAvisosSemana.getLayoutParams().height = 490;
                //rvAvisosSemana.getLayoutParams().height = 500;
            }
            AvisoAdaptador avisoAdaptador = new AvisoAdaptador(context, avisos);
            rvAvisosSemana.setAdapter(avisoAdaptador);
        } else {
            rvAvisosSemana.setVisibility(View.GONE);
            tvAvisosSemanales.setText("No hay avisos para los proximos 7 días");
            tvAvisosSemanales.getLayoutParams().height = 220;
        }
    }

    //Obtener los avisos de esta semana
    public void obtenerAvisosSemanales() {
        avisos.clear();
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(new StringRequest(Request.Method.POST, Url.obtenerAvisosSemana, result -> {
            try {
                JSONObject jsonResult = new JSONObject(result);
                if (!jsonResult.getBoolean("error")) {
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