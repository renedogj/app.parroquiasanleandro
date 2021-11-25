package com.parroquiasanleandro.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.parroquiasanleandro.Categoria;
import com.parroquiasanleandro.ItemViewModel;
import com.parroquiasanleandro.Menu;
import com.parroquiasanleandro.R;
import com.parroquiasanleandro.adaptadores.CategoriaAdaptador;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class FragmentCategorias extends Fragment {
    private Context context;

    public static RecyclerView rvCategorias;

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

        List<Categoria> categorias = new ArrayList<>();

        FirebaseDatabase.getInstance().getReference().child(Categoria.CATEGORIAS).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull @NotNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Categoria categoria = snapshot.getValue(Categoria.class);
                String key = snapshot.getKey();
                if (categoria != null) {
                    categoria.key = key;
                    categorias.add(categoria);
                }

                CategoriaAdaptador categoriaAdaptador = new CategoriaAdaptador(context, categorias,"A",rvCategorias,vmIds);
                rvCategorias.setAdapter(categoriaAdaptador);
            }

            @Override
            public void onChildChanged(@NonNull @NotNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull @NotNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull @NotNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });

        return view;
    }
}