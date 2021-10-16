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
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;


public class FragmentAvisosParroquiales extends Fragment {
    private Context context;

    private RecyclerView rvAvisos;
    private FloatingActionButton bttnNuevoAviso;

    List<Aviso> avisos;

    public FragmentAvisosParroquiales() {}

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

        bttnNuevoAviso.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(context, ActivityNuevoAviso.class));
            }
        });

        FirebaseDatabase.getInstance().getReference().child("Avisos").addChildEventListener(new ChildEventListener() {

            @Override
            public void onChildAdded(@NotNull DataSnapshot dataSnapshot, String prevChildKey) {
                Aviso aviso = dataSnapshot.getValue(Aviso.class);
                if (aviso != null) {
                    aviso.key = dataSnapshot.getKey();
                }
                avisos.add(aviso);

                AvisoAdaptador avisoAdaptador = new AvisoAdaptador(context, avisos);
                rvAvisos.setAdapter(avisoAdaptador);
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
        });
        return view;
    }
}