package com.parroquiasanleandro.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.parroquiasanleandro.Categoria;
import com.parroquiasanleandro.utils.ItemViewModel;
import com.parroquiasanleandro.Menu;
import com.parroquiasanleandro.R;
import com.parroquiasanleandro.adaptadores.CategoriaAdaptador;

import java.util.List;

public class FragmentCategorias extends Fragment {
    private Context context;

    public static RecyclerView rvCategorias;

    List<Categoria> categorias;

    private ItemViewModel vmIds;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        context = getContext();

        vmIds = new ViewModelProvider(requireActivity()).get(ItemViewModel.class);
        vmIds.setIdFragmentActual(Menu.FRAGMENT_CATEGORIAS);
        vmIds.addIdFragmentActual();
        vmIds.setCategoriaActual(Categoria.ID_PADRE);
        vmIds.addIdCategoria();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_categorias, container, false);

        rvCategorias = view.findViewById(R.id.rvCategorias);

        rvCategorias.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        rvCategorias.setLayoutManager(linearLayoutManager);

        categorias = Categoria.recuperarCategoriasLocal(context);
        CategoriaAdaptador categoriaAdaptador = new CategoriaAdaptador(context, categorias,Categoria.ID_PADRE,rvCategorias,vmIds);
        rvCategorias.setAdapter(categoriaAdaptador);

        return view;
    }
}