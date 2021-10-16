package com.parroquiasanleandro;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

public class FragmentPerfil extends Fragment {

    private Activity activity;
    private Context context;

    private ImageView ivFotoPerfil;
    private TextView tvNombreUsuario;
    private TextView tvEmail;
    private Button bttnCerrarSesion;

    public FragmentPerfil() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getContext();
        activity = getActivity();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_perfil,container,false);

        ivFotoPerfil = view.findViewById(R.id.ivFotoPerfil);
        tvNombreUsuario = view.findViewById(R.id.tvNombreUsuario);
        tvEmail = view.findViewById(R.id.tvEmail);
        bttnCerrarSesion = view.findViewById(R.id.bttnCerrarSesion);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user == null){
            startActivity(new Intent(context,ActivityInicarSesion.class));
            activity.finish();
        }else{
            Usuario usuario = Usuario.recuperarUsuarioLocal(context);
            tvNombreUsuario.setText(usuario.nombre);
            tvEmail.setText(usuario.email);
            if(usuario.fotoPerfil != null){
                Glide.with(context).load(usuario.fotoPerfil).into(ivFotoPerfil);
            }
        }

        /*if (user != null) {
            FirebaseDatabase.getInstance().getReference("Usuarios").child(user.getUid()).addValueEventListener(new ValueEventListener() {
                @RequiresApi(api = Build.VERSION_CODES.M)
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Usuario usuario = dataSnapshot.getValue(Usuario.class);
                    Usuario usuarioActual = new Usuario();
                    Log.d("CATEGORIA ACTUAL", usuario.suscripciones.values().toString());

                    usuarioActual.nombre = user.getDisplayName();
                    usuarioActual.email = user.getEmail();
                    usuarioActual.fotoPerfil = user.getPhotoUrl();
                    usuarioActual.uid = user.getUid();
                    usuarioActual.numeroTelefono = user.getPhoneNumber();
                    usuarioActual.emailVerified = user.isEmailVerified();
                    usuarioActual.suscripciones = usuario.suscripciones;
                    Set<String> categorias = new ArraySet<>();
                    categorias.addAll(usuarioActual.suscripciones.values());
                    for (String categoria: categorias){
                        Log.d("CATEGORIA", categoria);
                    }
                    if(usuarioActual.fotoPerfil != null){
                        Glide.with(context).load(usuarioActual.fotoPerfil).into(ivFotoPerfil);
                    }

                    Log.d("USUARIO ACTUAL", usuarioActual.toString());
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.w("DatabaseError", "loadPost:onCancelled", databaseError.toException());
                }
            });
        }*/

        bttnCerrarSesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
            }
        });

        List<Categoria> categorias = new ArrayList<>();

        /*FirebaseDatabase.getInstance().getReference().child("Categorias").orderByKey().addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull @NotNull DataSnapshot snapshot, @Nullable @org.jetbrains.annotations.Nullable String previousChildName) {
                //GenericTypeIndicator<Map<String,String>> genericTypeIndicator = new GenericTypeIndicator<Map<String,String>>() {};
                String nombreCategoria = snapshot.getValue(String.class);
                String key = snapshot.getKey();
                categorias.add(new Categoria(key,nombreCategoria));
                //Log.d("CATEGORIAS2",key + ": " + nombreCategoria);
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

        return view;
    }

    /*@Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }*/
}