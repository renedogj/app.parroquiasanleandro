package com.parroquiasanleandro;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class FragmentAvisosParroquiales extends Fragment {
    private Context context;

    private RecyclerView rvAvisos;
    private FloatingActionButton bttnNuevoAviso;

    List<Aviso> avisos;

    public FragmentAvisosParroquiales() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        context = getContext();
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_avisos_parroquiales, container, false);

        rvAvisos = view.findViewById(R.id.rvAvisos);
        bttnNuevoAviso = view.findViewById(R.id.bttnNuevoAviso);

        rvAvisos.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        rvAvisos.setLayoutManager(linearLayoutManager);

        avisos = new ArrayList<>();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            Usuario usuario = Usuario.recuperarUsuarioLocal(context);

            if (!usuario.esAdministrador) {
                bttnNuevoAviso.setVisibility(View.INVISIBLE);
            }

            for (Categoria categoria : usuario.categorias) {
                FirebaseDatabase.getInstance().getReference().child(Aviso.AVISOS).child(categoria.key).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NotNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                            Aviso aviso = postSnapshot.getValue(Aviso.class);
                            if (aviso != null) {
                                aviso.key = postSnapshot.getKey();
                                avisos.add(aviso);
                                AvisoAdaptador avisoAdaptador = new AvisoAdaptador(context, avisos);
                                rvAvisos.setAdapter(avisoAdaptador);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NotNull DatabaseError databaseError) {
                        Log.e(databaseError.getMessage(), databaseError.getDetails());
                    }
                });
            }
        }else{
            bttnNuevoAviso.setVisibility(View.INVISIBLE);

            FirebaseDatabase.getInstance().getReference().child(Aviso.AVISOS).child("A").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NotNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        Aviso aviso = postSnapshot.getValue(Aviso.class);
                        if (aviso != null) {
                            aviso.key = postSnapshot.getKey();
                            avisos.add(aviso);
                            AvisoAdaptador avisoAdaptador = new AvisoAdaptador(context, avisos);
                            rvAvisos.setAdapter(avisoAdaptador);
                        }
                    }
                }

                @Override
                public void onCancelled(@NotNull DatabaseError databaseError) {
                    Log.e(databaseError.getMessage(), databaseError.getDetails());
                }
            });
        }

        bttnNuevoAviso.setOnClickListener(v -> startActivity(new Intent(context, ActivityNuevoAviso.class)));

        return view;
    }
}