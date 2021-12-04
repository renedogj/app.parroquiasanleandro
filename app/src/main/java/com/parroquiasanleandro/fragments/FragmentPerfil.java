package com.parroquiasanleandro.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.parroquiasanleandro.ItemViewModel;
import com.parroquiasanleandro.Menu;
import com.parroquiasanleandro.R;
import com.parroquiasanleandro.Usuario;

public class FragmentPerfil extends Fragment {
    private Activity activity;
    private Context context;

    private ImageView ivFotoPerfil;
    private TextView tvNombreUsuario;
    private TextView tvEmail;
    private LinearLayout linearLayoutEmail;
    private LinearLayout linearLayoutCategorias;

    private ItemViewModel vmIds;

    public FragmentPerfil() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        context = getContext();
        activity = getActivity();

        vmIds = new ViewModelProvider(requireActivity()).get(ItemViewModel.class);
        vmIds.setIdFragmentActual(Menu.FRAGMENT_PERFIL);
        vmIds.addIdFragmentActual();
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_perfil, container, false);

        ivFotoPerfil = view.findViewById(R.id.ivFotoPerfil);
        tvNombreUsuario = view.findViewById(R.id.tvNombreUsuario);
        tvEmail = view.findViewById(R.id.tvEmail);
        linearLayoutEmail = view.findViewById(R.id.linearLayoutEmail);
        linearLayoutCategorias = view.findViewById(R.id.linearLayoutCategorias);

        vmIds = new ViewModelProvider(requireActivity()).get(ItemViewModel.class);

        Usuario usuario = Usuario.recuperarUsuarioLocal(context);
        tvNombreUsuario.setText(usuario.nombre);
        tvEmail.setText(usuario.email);
        if (usuario.fotoPerfil != null) {
            Glide.with(context).load(usuario.fotoPerfil).into(ivFotoPerfil);
        }

        linearLayoutEmail.setOnClickListener(v -> {
            //startActivity(new Intent(context,ActivityCategorias.class));
        });

        linearLayoutCategorias.setOnClickListener(v -> {
            FragmentManager fragmentManager = getParentFragmentManager();
            Menu.iniciarFragmentCategorias(fragmentManager);
        });

        return view;
    }
}