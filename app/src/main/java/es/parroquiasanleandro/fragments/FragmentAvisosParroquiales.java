package es.parroquiasanleandro.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import es.parroquiasanleandro.Aviso;
import es.parroquiasanleandro.Menu;
import es.parroquiasanleandro.R;
import es.parroquiasanleandro.activitys.ActivityNuevoAviso;
import es.parroquiasanleandro.utils.ItemViewModel;


public class FragmentAvisosParroquiales extends Fragment {
    private Context context;

    private RecyclerView rvAvisos;
    private FloatingActionButton bttnNuevoAviso;

    List<Aviso> avisos;

    private ItemViewModel vmIds;

    public FragmentAvisosParroquiales() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        context = getContext();

        vmIds = new ViewModelProvider(requireActivity()).get(ItemViewModel.class);
        vmIds.setIdFragmentActual(Menu.FRAGMENT_AVISOS);
        vmIds.addIdFragmentActual();
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

        /*FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            Usuario usuario = Usuario.recuperarUsuarioLocal(context);

            if (!usuario.esAdministrador) {
                bttnNuevoAviso.setVisibility(View.INVISIBLE);
            }

            for (Categoria categoria : usuario.getCategorias()) {
                FirebaseDatabase.getInstance().getReference().child(Aviso.AVISOS).child(categoria.key).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NotNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                            Aviso aviso = postSnapshot.getValue(Aviso.class);
                            if (aviso != null) {
                                aviso.key = postSnapshot.getKey();
                                avisos.add(aviso);
                            }
                        }
                        if(!avisos.isEmpty()) {
                            AvisoAdaptador avisoAdaptador = new AvisoAdaptador(context, avisos);
                            rvAvisos.setAdapter(avisoAdaptador);
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

            FirebaseDatabase.getInstance().getReference().child(Aviso.AVISOS).child(Categoria.ID_PADRE).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NotNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        Aviso aviso = postSnapshot.getValue(Aviso.class);
                        if (aviso != null) {
                            aviso.key = postSnapshot.getKey();
                            avisos.add(aviso);
                        }
                    }
                    if(!avisos.isEmpty()) {
                        AvisoAdaptador avisoAdaptador = new AvisoAdaptador(context, avisos);
                        rvAvisos.setAdapter(avisoAdaptador);
                    }
                }

                @Override
                public void onCancelled(@NotNull DatabaseError databaseError) {
                    Log.e(databaseError.getMessage(), databaseError.getDetails());
                }
            });
        }*/

        bttnNuevoAviso.setOnClickListener(v -> startActivity(new Intent(context, ActivityNuevoAviso.class)));

        return view;
    }
}