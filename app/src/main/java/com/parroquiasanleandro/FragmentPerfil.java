package com.parroquiasanleandro;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;

public class FragmentPerfil extends Fragment {
    private Activity activity;
    private Context context;

    private ImageView ivFotoPerfil;
    private TextView tvNombreUsuario;
    private TextView tvEmail;
    private Button bttnCerrarSesion;
    //private RecyclerView rvCategorias;
    private LinearLayout linearLayoutEmail;
    private LinearLayout linearLayoutCategorias;

    public FragmentPerfil() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getContext();
        activity = getActivity();
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_perfil, container, false);

        ivFotoPerfil = view.findViewById(R.id.ivFotoPerfil);
        tvNombreUsuario = view.findViewById(R.id.tvNombreUsuario);
        tvEmail = view.findViewById(R.id.tvEmail);
        //rvCategorias = view.findViewById(R.id.rvCategorias);
        bttnCerrarSesion = view.findViewById(R.id.bttnCerrarSesion);
        linearLayoutEmail = view.findViewById(R.id.linearLayoutEmail);
        linearLayoutCategorias = view.findViewById(R.id.linearLayoutCategorias);

        //FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        //if (user == null) {
        //startActivity(new Intent(context, ActivityInicarSesion.class));
        //activity.finish();
        //} else {
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
            startActivity(new Intent(context,ActivityCategorias.class));
        });

            /*rvCategorias.setHasFixedSize(true);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
            rvCategorias.setLayoutManager(linearLayoutManager);

            List<Categoria> categorias = new ArrayList<>();

            FirebaseDatabase.getInstance().getReference().child(Categoria.CATEGORIAS).addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull @NotNull DataSnapshot snapshot, @Nullable @org.jetbrains.annotations.Nullable String previousChildName) {
                    String nombreCategoria = snapshot.getValue(String.class);
                    String key = snapshot.getKey();
                    categorias.add(new Categoria(key, nombreCategoria));

                    CategoriaAdaptador categoriaAdaptador = new CategoriaAdaptador(context, categorias, usuario);
                    rvCategorias.setAdapter(categoriaAdaptador);
                }

                @Override
                public void onChildChanged(@NonNull @NotNull DataSnapshot snapshot, @Nullable @org.jetbrains.annotations.Nullable String previousChildName) {

                }

                @Override
                public void onChildRemoved(@NonNull @NotNull DataSnapshot snapshot) {

                }

                @Override
                public void onChildMoved(@NonNull @NotNull DataSnapshot snapshot, @Nullable @org.jetbrains.annotations.Nullable String previousChildName) {

                }

                @Override
                public void onCancelled(@NonNull @NotNull DatabaseError error) {

                }
            });*/
        //}

        bttnCerrarSesion.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            Toast.makeText(context, "Se ha cerrado sesion", Toast.LENGTH_SHORT).show();
            context.startActivity(new Intent(context, ActivityNavigation.class));
            activity.finish();
        });

        return view;
    }
}