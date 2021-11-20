package com.parroquiasanleandro.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.parroquiasanleandro.ItemViewModel;
import com.parroquiasanleandro.Menu;
import com.parroquiasanleandro.R;
import com.parroquiasanleandro.adaptadores.DiaAdaptador;
import com.parroquiasanleandro.fecha.Fecha;
import com.parroquiasanleandro.fecha.Mes;

import java.util.ArrayList;
import java.util.List;

public class FragmentCalendario extends Fragment {
    private Context context;

    private RecyclerView rvCalendario;
    private TextView tvMes;

    private Fecha fechaReferencia = Fecha.FechaActual();

    private ItemViewModel vmIds;

    public FragmentCalendario() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        context = getContext();

        vmIds = new ViewModelProvider(requireActivity()).get(ItemViewModel.class);
        vmIds.setIdFragmentActual(Menu.FRAGMENT_CALENDARIO);
        vmIds.addIdFragmentActual();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_calendario, container, false);

        rvCalendario = view.findViewById(R.id.rvCalendario);
        tvMes = view.findViewById(R.id.tvMes);

        rvCalendario.setHasFixedSize(false);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(context, 7);
        rvCalendario.setLayoutManager(gridLayoutManager);

        List<Integer> dias = new ArrayList<>();

        Mes mesActual = fechaReferencia.mes;


        int numDiasMes = mesActual.getNumDiasMes();
        for (int i = 1; i <= numDiasMes; i++) {
            dias.add(i);
        }
        DiaAdaptador diaAdaptador = new DiaAdaptador(context, dias);
        rvCalendario.setAdapter(diaAdaptador);
        return view;
    }
}