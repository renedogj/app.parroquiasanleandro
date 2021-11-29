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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.parroquiasanleandro.Aviso;
import com.parroquiasanleandro.ItemViewModel;
import com.parroquiasanleandro.Menu;
import com.parroquiasanleandro.R;
import com.parroquiasanleandro.Usuario;
import com.parroquiasanleandro.adaptadores.DiaAdaptador;
import com.parroquiasanleandro.fecha.Fecha;

import java.util.ArrayList;
import java.util.List;

public class FragmentCalendario extends Fragment {
    private Context context;

    private RecyclerView rvCalendario;
    private TextView tvMes;

    private Fecha fechaReferencia = Fecha.FechaActual();

    private ItemViewModel vmIds;

    public FragmentCalendario() {
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
        tvMes.setText(fechaReferencia.mes.name());

        int numDiasMes = fechaReferencia.mes.getNumDiasMes();
        for (int i = 1; i <= numDiasMes; i++) {
            dias.add(i);
        }

        List<Aviso> avisos = new ArrayList<>();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            Usuario usuario = Usuario.recuperarUsuarioLocal(context);
            DiaAdaptador diaAdaptador = new DiaAdaptador(context, dias,fechaReferencia,usuario);
            rvCalendario.setAdapter(diaAdaptador);
            /*FirebaseDatabase.getInstance().getReference().child("Calendario")
                    .child(fechaReferencia.toString(Fecha.FormatosFecha.aaaaMM)).addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull @NotNull DataSnapshot snapshot, @Nullable String previousChildName) {
                    for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                        String key = postSnapshot.getKey();
                        String categoria = postSnapshot.getValue(String.class);
                        //Log.d("FRAGMENTCALENDARIO","titulo");
                        if (key != null && categoria != null && Arrays.asList(Categoria.getKeysCategorias(usuario.getCategorias())).contains(categoria)) {
                            FirebaseDatabase.getInstance().getReference().child(Aviso.AVISOS)
                                    .child(categoria).child(key).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                                    Aviso aviso = snapshot.getValue(Aviso.class);
                                    if (aviso != null) {
                                        aviso.key = key;
                                        avisos.add(aviso);
                                        Log.d("FRAGMENTCALENDARIO",aviso.titulo);
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull @NotNull DatabaseError databaseError) {
                                    Log.e(databaseError.getMessage(), databaseError.getDetails());
                                }
                            });
                        }
                    }
                    DiaAdaptador diaAdaptador = new DiaAdaptador(context, dias, avisos);
                    rvCalendario.setAdapter(diaAdaptador);
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
            });*/

            /*FirebaseDatabase.getInstance().getReference().child("Calendario")
                    .child(fechaReferencia.toString(Fecha.FormatosFecha.aaaaMM)).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NotNull DataSnapshot dataSnapshot) {
                    //Bucle que recorre los días del mes que tienen avisos
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        HashMap avisosDia= postSnapshot.getValue(HashMap.class);
                        if (avisosDia != null) {
                            for (Categoria categoria :avisosDia){

                            }
                            String key = postSnapshot.getKey();
                        }
                        //String categoria = "";


                        /*if (key != null && categoria != null && Arrays.asList(Categoria.getKeysCategorias(usuario.getCategorias())).contains(categoria)) {
                            FirebaseDatabase.getInstance().getReference().child(Aviso.AVISOS)
                                    .child(categoria).child(key).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                                    Aviso aviso = snapshot.getValue(Aviso.class);
                                    if (aviso != null) {
                                        aviso.key = key;
                                        avisos.add(aviso);
                                        Log.d("FRAGMENTCALENDARIO",aviso.titulo);
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull @NotNull DatabaseError databaseError) {
                                    Log.e(databaseError.getMessage(), databaseError.getDetails());
                                }
                            });
                        }
                    }
                    DiaAdaptador diaAdaptador = new DiaAdaptador(context, dias, avisos);
                    rvCalendario.setAdapter(diaAdaptador);
                }

                @Override
                public void onCancelled(@NotNull DatabaseError databaseError) {
                    Log.e(databaseError.getMessage(), databaseError.getDetails());
                }
            });*/
        } else {
            /*FirebaseDatabase.getInstance().getReference().child("Calendario")
                    .child(fechaReferencia.toString(Fecha.FormatosFecha.aaaaMM)).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NotNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        String key = postSnapshot.getKey();
                        String categoria = postSnapshot.getValue(String.class);

                        if (key != null && categoria != null && categoria.equals("A")) {
                            FirebaseDatabase.getInstance().getReference().child(Aviso.AVISOS).child(categoria).child(key).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                                    Aviso aviso = snapshot.getValue(Aviso.class);
                                    if (aviso != null) {
                                        aviso.key = key;
                                        avisos.add(aviso);
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull @NotNull DatabaseError databaseError) {
                                    Log.e(databaseError.getMessage(), databaseError.getDetails());
                                }
                            });
                        }
                    }
                    DiaAdaptador diaAdaptador = new DiaAdaptador(context, dias,fechaReferencia);
                    rvCalendario.setAdapter(diaAdaptador);
                }

                @Override
                public void onCancelled(@NotNull DatabaseError databaseError) {
                    Log.e(databaseError.getMessage(), databaseError.getDetails());
                }
            });*/
        }
        return view;
    }
}