package es.parroquiasanleandro.fragments;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

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
import es.parroquiasanleandro.activitys.ActivityNavigation;
import es.parroquiasanleandro.adaptadores.AvisoAdaptador;
import es.parroquiasanleandro.adaptadores.DiaAdaptador;
import es.parroquiasanleandro.utils.ItemViewModel;
import es.renedogj.fecha.Fecha;
import es.renedogj.fecha.Mes;
import es.renedogj.monthpicker.MonthPicker;

public class FragmentCalendario extends Fragment {
    private Context context;
    private ItemViewModel viewModel;

    private TextView tvMes;
    private TextView tvMesAnterior;
    private TextView tvMesSiguiente;
    private RecyclerView rvCalendario;
    public TextView tvFechaSelecionada;
    public RecyclerView rvAvisosDiaSelecionado;

    private Fecha fechaReferencia;
    private List<Fecha> fechas;
    private List<Aviso> avisos;

    private Usuario usuario;

    public TextView tvNoHayAvisos;

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

        tvMes = view.findViewById(R.id.tvMes);
        tvMesAnterior = view.findViewById(R.id.tvMesAnterior);
        tvMesSiguiente = view.findViewById(R.id.tvMesSiguiente);
        rvCalendario = view.findViewById(R.id.rvCalendario);
        tvFechaSelecionada = view.findViewById(R.id.tvFechaSelecionada);
        rvAvisosDiaSelecionado = view.findViewById(R.id.rvAvisosDiaSelecionado);
        tvNoHayAvisos = view.findViewById(R.id.tvNoHayAvisos);

        rvCalendario.setHasFixedSize(false);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(context, 7);
        rvCalendario.setLayoutManager(gridLayoutManager);

        rvAvisosDiaSelecionado.setHasFixedSize(false);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        rvAvisosDiaSelecionado.setLayoutManager(linearLayoutManager);

        fechas = new ArrayList<>();
        avisos = new ArrayList<>();

        //setCalendario();

        tvMesAnterior.setOnClickListener(view1 -> {
            fechaReferencia.sumMeses(-1);
            fechaReferencia.actualizarDiaSemana();
            setCalendario();
        });

        tvMesSiguiente.setOnClickListener(view12 -> {
            fechaReferencia.sumMeses(1);
            fechaReferencia.actualizarDiaSemana();
            setCalendario();
        });

        tvMes.setOnClickListener(v -> {
            MonthPicker monthPicker = new MonthPicker(context);
            monthPicker.setMesesAbr(Mes.getAbreviaturas(Mes.FormatosMes.Mm_));
            monthPicker.setPositiveButton((month, startDate, endDate, year, monthLabel) -> {
                fechaReferencia.setMes(month - 1);
                fechaReferencia.convertirAPrimerDiaMes();
                fechaReferencia.año = year;
                setCalendario();
            });
            monthPicker.setNegativeButton(Dialog::dismiss).show();
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        ActivityNavigation.actionBar.setTitle(Menu.CALENDARIO);
        viewModel.setIdFragmentActual(Menu.FRAGMENT_CALENDARIO);
        viewModel.addIdFragmentActual();
        setCalendario();
    }

    public void setCalendario() {
        avisos.clear();
        tvFechaSelecionada.setText("");
        AvisoAdaptador avisoAdaptador = new AvisoAdaptador(context, avisos);
        rvAvisosDiaSelecionado.setAdapter(avisoAdaptador);
        fechas.clear();
        for (int i = fechaReferencia.diaSemana.getNumeroDia() - 1; i >= 1; i--) {
            Fecha auxFecha = new Fecha(fechaReferencia.dia, fechaReferencia.mes, fechaReferencia.año);
            auxFecha.sumDias(-i);
            fechas.add(auxFecha);
        }

        for (int i = 0; i <= fechaReferencia.mes.getNumDiasMes() - 1; i++) {
            Fecha auxFecha = new Fecha(fechaReferencia.dia, fechaReferencia.mes, fechaReferencia.año);
            auxFecha.sumDias(i);
            fechas.add(auxFecha);
        }

        Fecha fechaFinMes = new Fecha(fechaReferencia.dia, fechaReferencia.mes, fechaReferencia.año);
        fechaFinMes.dia = fechaFinMes.mes.getNumDiasMes();
        fechaFinMes.actualizarDiaSemana();
        for (int i = 1; i <= 7 - fechaFinMes.diaSemana.getNumeroDia(); i++) {
            Fecha auxFecha = new Fecha(fechaFinMes.dia, fechaFinMes.mes, fechaFinMes.año);
            auxFecha.sumDias(i);
            fechas.add(auxFecha);
        }
        tvNoHayAvisos.setText("");
        obtenerAvisos();
    }

    public void obtenerAvisos() {
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(new StringRequest(Request.Method.POST, Url.obtenerAvisosCalendario, result -> {
            try {
                JSONObject jsonResult = new JSONObject(result);
                if (!jsonResult.getBoolean("error")) {
                    avisos = Aviso.JSONArrayToAvisos(jsonResult.getJSONArray("avisos"));
                    tvMes.setText(fechaReferencia.toString(Fecha.FormatosFecha.MMMM_aaaa));
                    DiaAdaptador diaAdaptador = new DiaAdaptador(context, fechas, avisos, fechaReferencia, this);
                    rvCalendario.setAdapter(diaAdaptador);
                } else {
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
                Map<String, String> parametros = new HashMap<>();
                if (usuario.getId() != null) {
                    parametros.put("idUsuario", usuario.getId());
                } else {
                    parametros.put("idUsuario", "0");
                }
                parametros.put("fechaInicio", fechas.get(0).toString(Fecha.FormatosFecha.aaaa_MM_dd_HH_mm_ss));
                Fecha fechaFin = fechas.get(fechas.size() - 1).clone();
                fechaFin.hora = 23;
                fechaFin.minuto = 59;
                fechaFin.segundo = 59;
                parametros.put("fechaFin", fechaFin.toString(Fecha.FormatosFecha.aaaa_MM_dd_HH_mm_ss));
                return parametros;
            }
        });
    }
}