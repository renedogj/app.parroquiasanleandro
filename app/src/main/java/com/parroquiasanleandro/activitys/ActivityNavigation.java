package com.parroquiasanleandro.activitys;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.parroquiasanleandro.Categoria;
import com.parroquiasanleandro.ItemViewModel;
import com.parroquiasanleandro.Menu;
import com.parroquiasanleandro.R;
import com.parroquiasanleandro.Usuario;
import com.parroquiasanleandro.adaptadores.CategoriaAdaptador;
import com.parroquiasanleandro.fragments.FragmentCategorias;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class ActivityNavigation extends AppCompatActivity {
    private final Activity activity = ActivityNavigation.this;
    private final Context context = ActivityNavigation.this;

    private LinearLayout linearLayoutInicio;
    private LinearLayout linearLayoutAvisos;
    private LinearLayout linearLayoutInformacion;
    private LinearLayout linearLayoutPerfil;
    public static ImageView imgInicio;
    public static ImageView imgAvisos;
    public static ImageView imgInformacion;
    public static ImageView imgPerfil;

    private DrawerLayout drawerLayout;
    private NavigationView navView;
    private ActionBar actionBar;
    private ActionBarDrawerToggle toggle;

    private ItemViewModel vmIds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);

        linearLayoutInicio = findViewById(R.id.linearLayoutInicio);
        linearLayoutAvisos = findViewById(R.id.linearLayoutAvisos);
        linearLayoutInformacion = findViewById(R.id.linearLayoutInformacion);
        linearLayoutPerfil = findViewById(R.id.linearLayoutPerfil);

        imgInicio = findViewById(R.id.imgInicio);
        imgAvisos = findViewById(R.id.imgAvisos);
        imgInformacion = findViewById(R.id.imgInformacion);
        imgPerfil = findViewById(R.id.imgPerfil);

        navView = findViewById(R.id.navView);
        drawerLayout = findViewById(R.id.drawerLayout);

        toggle = new ActionBarDrawerToggle(activity, drawerLayout, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        actionBar = getSupportActionBar();
        if (actionBar != null) {

            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("Parroquia San Leandro");
        }

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            MenuItem item = Menu.addCerrarSesion(navView);

            Usuario.actualizarUsuarioLocal(context, user);
        }

        FragmentManager fragmentManager = getSupportFragmentManager();

        vmIds = new ViewModelProvider(this).get(ItemViewModel.class);
        vmIds.setIdFragmentActual(Menu.FRAGMENT_INICIO);
        vmIds.addIdFragmentActual();

        linearLayoutInicio.setOnClickListener(v -> {
            if (vmIds.getIdFragmentActual() != Menu.FRAGMENT_INICIO) {
                Menu.iniciarFragmentInicio(fragmentManager, actionBar);
                Menu.asignarIconosMenu(navView,Menu.FRAGMENT_INICIO);
            }
        });

        linearLayoutAvisos.setOnClickListener(v -> {
            if (vmIds.getIdFragmentActual() != Menu.FRAGMENT_AVISOS) {
                Menu.iniciarFragmentAvisos(fragmentManager, actionBar);
                Menu.asignarIconosMenu(navView,Menu.FRAGMENT_AVISOS);
            }
        });

        linearLayoutInformacion.setOnClickListener(v -> {
            if (vmIds.getIdFragmentActual() != Menu.FRAGMENT_INFORMACION) {
                Menu.iniciarFragmentInformacion(fragmentManager, actionBar);
                Menu.asignarIconosMenu(navView,Menu.FRAGMENT_INFORMACION);
            }
        });

        linearLayoutPerfil.setOnClickListener(v -> {
            if (vmIds.getIdFragmentActual() != Menu.FRAGMENT_PERFIL) {
                Menu.iniciarFragmentPerfil(user, activity, context, fragmentManager, actionBar);
                Menu.asignarIconosMenu(navView,Menu.FRAGMENT_PERFIL);
            }
        });

        navView.setNavigationItemSelectedListener(item -> {
            vmIds.setIdFragmentActual(Menu.selecionarItemMenu(item, vmIds.getIdFragmentActual(), user, activity, context, fragmentManager, actionBar, navView));
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (toggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            int posUltimoFragment = vmIds.getIdsFragment().size() - 1;
            int ultimoFragment = vmIds.getIdsFragment().get(posUltimoFragment);
            if(ultimoFragment == Menu.FRAGMENT_CATEGORIAS) {
                if(!vmIds.getIdsCategorias().isEmpty()) {
                    if (vmIds.getCategoriaActual().equals("A")) {
                        super.onBackPressed();
                    } else {
                        int posUltimaCategoria = vmIds.getIdsCategorias().size() - 1;
                        vmIds.getIdsCategorias().remove(posUltimaCategoria);
                        vmIds.setCategoriaActual(vmIds.getIdsCategorias().get(posUltimaCategoria - 1));

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

                                CategoriaAdaptador categoriaAdaptador = new CategoriaAdaptador(context, categorias, vmIds.getCategoriaActual(), FragmentCategorias.rvCategorias, vmIds);
                                FragmentCategorias.rvCategorias.setAdapter(categoriaAdaptador);
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
                    }
                }else{
                    super.onBackPressed();
                }
            }else{
                super.onBackPressed();
                vmIds.getIdsFragment().remove(posUltimoFragment);
                if (!vmIds.getIdsFragment().isEmpty()) {
                    vmIds.setIdFragmentActual(vmIds.getIdsFragment().get(posUltimoFragment - 1));
                    Menu.asignarIconosMenu(navView, vmIds.getIdFragmentActual());
                    if (vmIds.getIdFragmentActual() == Menu.FRAGMENT_CATEGORIAS) {
                        vmIds.getIdsCategorias().clear();
                        onBackPressed();
                    }
                }
            }
        }
    }
}
