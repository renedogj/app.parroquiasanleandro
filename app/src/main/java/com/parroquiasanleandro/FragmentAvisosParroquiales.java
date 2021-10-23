package com.parroquiasanleandro;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
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

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_avisos_parroquiales, container, false);
        rvAvisos = view.findViewById(R.id.rvAvisos);
        bttnNuevoAviso = view.findViewById(R.id.bttnNuevoAviso);

        rvAvisos.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        rvAvisos.setLayoutManager(linearLayoutManager);

        avisos = new ArrayList<>();
        Usuario usuario = Usuario.recuperarUsuarioLocal(context);

        for (Categoria categoria : usuario.categorias) {
            FirebaseDatabase.getInstance().getReference().child(Aviso.AVISOS).child(categoria.key).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NotNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                        Aviso aviso = postSnapshot.getValue(Aviso.class);
                        if (aviso != null) {
                            aviso.key = dataSnapshot.getKey();
                            avisos.add(aviso);
                            AvisoAdaptador avisoAdaptador = new AvisoAdaptador(context, avisos);
                            rvAvisos.setAdapter(avisoAdaptador);
                        }
                    }
                }

                @Override
                public void onCancelled(@NotNull DatabaseError databaseError) {
                    Log.e(databaseError.getMessage(),databaseError.getDetails());
                }
            });
        }
        /*FirebaseDatabase.getInstance().getReference().child(Aviso.AVISOS).addChildEventListener(new ChildEventListener() {

            @Override
            public void onChildAdded(@NotNull DataSnapshot dataSnapshot, String prevChildKey) {
                Aviso aviso = dataSnapshot.getValue(Aviso.class);
                if (aviso != null) {
                    aviso.key = dataSnapshot.getKey();

                    //if (Arrays.asList(usuario.categorias).contains(aviso.categoria)) {
                        avisos.add(aviso);

                        AvisoAdaptador avisoAdaptador = new AvisoAdaptador(context, avisos);
                        rvAvisos.setAdapter(avisoAdaptador);
                    //}
                }
            }

            @Override
            public void onChildChanged(@NotNull DataSnapshot dataSnapshot, String prevChildKey) {

            }

            @Override
            public void onChildRemoved(@NotNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NotNull DataSnapshot dataSnapshot, String prevChildKey) {

            }

            @Override
            public void onCancelled(@NotNull DatabaseError databaseError) {
                Log.d("DATABASE ERROR", databaseError.getMessage());
            }
        });*/

        bttnNuevoAviso.setOnClickListener(v -> startActivity(new Intent(context, ActivityNuevoAviso.class)));

        return view;
    }
}