package es.parroquiasanleandro.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import es.parroquiasanleandro.Grupo;
import es.parroquiasanleandro.Menu;
import es.parroquiasanleandro.R;
import es.parroquiasanleandro.adaptadores.GrupoAdaptador;
import es.parroquiasanleandro.utils.ItemViewModel;

public class FragmentGrupos extends Fragment {
    private Context context;
    public static RecyclerView rvGrupos;

    List<Grupo> grupos;

    private ItemViewModel viewModel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getContext();
        viewModel = new ViewModelProvider(requireActivity()).get(ItemViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_grupos, container, false);

        rvGrupos = view.findViewById(R.id.rvGrupos);

        rvGrupos.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        rvGrupos.setLayoutManager(linearLayoutManager);
        
        grupos = Grupo.recuperarGruposDeLocal(context);
        GrupoAdaptador grupoAdaptador = new GrupoAdaptador(context, grupos, Grupo.ID_PADRE, rvGrupos, viewModel);
        rvGrupos.setAdapter(grupoAdaptador);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        viewModel.setIdFragmentActual(Menu.FRAGMENT_GRUPOS);
        viewModel.addIdFragmentActual();
        viewModel.setGrupoActual(Grupo.ID_PADRE);
        viewModel.addIdGrupoActual();
    }
}