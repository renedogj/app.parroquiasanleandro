package com.parroquiasanleandro;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
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

import org.jetbrains.annotations.NotNull;

public class ActivityNavigation extends AppCompatActivity {
    private final Activity activity = ActivityNavigation.this;
    private final Context context = ActivityNavigation.this;

    //private FragmentContainerView fragment_container_view;
    private LinearLayout linearLayoutInicio;
    private LinearLayout linearLayoutAvisos;
    private LinearLayout linearLayoutInformacion;
    private LinearLayout linearLayoutPerfil;

    private DrawerLayout drawerLayout;
    private NavigationView navView;
    private ActionBar actionBar;
    private ActionBarDrawerToggle toggle;

    private ItemViewModel vmIds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);

        //fragment_container_view = findViewById(R.id.fragment_container);
        linearLayoutInicio = findViewById(R.id.linearLayoutInicio);
        linearLayoutAvisos = findViewById(R.id.linearLayoutAvisos);
        linearLayoutInformacion = findViewById(R.id.linearLayoutInformacion);
        linearLayoutPerfil = findViewById(R.id.linearLayoutPerfil);

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

        vmIds = new ViewModelProvider(this).get(ItemViewModel.class);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            MenuItem item = Menu.addCerrarSesion(navView);

            Usuario.actualizarUsuarioLocal(context, user);
        }

        FragmentManager fragmentManager = getSupportFragmentManager();

        if (savedInstanceState == null) {
            fragmentManager.beginTransaction()
                    .setReorderingAllowed(true)
                    .add(R.id.fragment_container, FragmentInicio.class, null)
                    .commit();

            vmIds.idFragmentActual = Menu.FRAGMENT_INICIO;
            vmIds.addIdFragmentActual();
        }

        linearLayoutInicio.setOnClickListener(v -> {
            if(vmIds.idFragmentActual != Menu.FRAGMENT_INICIO){
                vmIds.idFragmentActual = Menu.iniciarFragmentInicio(fragmentManager,actionBar);
                vmIds.addIdFragmentActual();
            }
        });

        linearLayoutAvisos.setOnClickListener(v -> {
            if(vmIds.idFragmentActual != Menu.FRAGMENT_AVISOS) {
                vmIds.idFragmentActual = Menu.iniciarFragmentAvisos(fragmentManager, actionBar);
                vmIds.addIdFragmentActual();
            }
        });

        linearLayoutInformacion.setOnClickListener(v -> {
            if(vmIds.idFragmentActual != Menu.FRAGMENT_INFORMACION){
                vmIds.idFragmentActual = Menu.iniciarFragmentInformacion(fragmentManager,actionBar);
                vmIds.addIdFragmentActual();
            }
        });

        linearLayoutPerfil.setOnClickListener(v -> {
            if (vmIds.idFragmentActual != Menu.FRAGMENT_PERFIL) {
                vmIds.idFragmentActual = Menu.iniciarFragmentPerfil(user, activity, context, fragmentManager, actionBar);
                vmIds.addIdFragmentActual();
            }
        });

        navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @SuppressLint("NonConstantResourceId")
            @Override
            public boolean onNavigationItemSelected(@NonNull @NotNull MenuItem item) {
                vmIds.idFragmentActual = Menu.selecionarItemMenu(item, vmIds.idFragmentActual,user,activity,context,fragmentManager,actionBar);
                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            }
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
            super.onBackPressed();
        }

        int ultimoFragment = vmIds.idsFragment.size()-1;
        vmIds.idsFragment.remove(ultimoFragment);
        if(!vmIds.idsFragment.isEmpty()) {
            vmIds.idFragmentActual = vmIds.idsFragment.get(ultimoFragment - 1);
            if(vmIds.idFragmentActual == Menu.FRAGMENT_CATEGORIAS){
                onBackPressed();
            }
        }
    }
}
