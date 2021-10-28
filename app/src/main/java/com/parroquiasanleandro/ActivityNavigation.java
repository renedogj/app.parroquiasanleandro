package com.parroquiasanleandro;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentContainerView;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.jetbrains.annotations.NotNull;

public class ActivityNavigation extends AppCompatActivity{
    private final Activity activity = ActivityNavigation.this;
    private final Context context = ActivityNavigation.this;

    private FragmentContainerView fragment_container_view;
    private LinearLayout linearLayoutInicio;
    private LinearLayout linearLayoutAvisos;
    private LinearLayout linearLayoutInformacion;
    private LinearLayout linearLayoutPerfil;

    private DrawerLayout drawerLayout;
    private NavigationView navView;

    private ActionBar actionBar;
    private ActionBarDrawerToggle toggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);

        fragment_container_view = findViewById(R.id.fragment_container);
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

        navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @SuppressLint("NonConstantResourceId")
            @Override
            public boolean onNavigationItemSelected(@NonNull @NotNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.nav_fragment_inicio:
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.fragment_container, new FragmentInicio())
                                .addToBackStack(null)
                                .commit();
                        break;
                    case R.id.nav_fragment_avisos:
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.fragment_container,new FragmentAvisosParroquiales())
                                .addToBackStack(null)
                                .commit();
                        break;
                    case R.id.nav_fragment_informacion:
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.fragment_container, new FragmentInformacion())
                                .addToBackStack(null)
                                .commit();
                        break;
                    case R.id.nav_fragment_perfil:
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.fragment_container, new FragmentPerfil())
                                .addToBackStack(null)
                                .commit();
                        break;
                }
                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            }
        });

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .setReorderingAllowed(true)
                    .add(R.id.fragment_container, FragmentInicio.class, null)
                    .commit();
        }

        linearLayoutInicio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getSupportFragmentManager().beginTransaction()
                        .setReorderingAllowed(true)
                        .replace(R.id.fragment_container, FragmentInicio.class, null)
                        .addToBackStack(null)
                        .commit();
            }
        });

        linearLayoutAvisos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getSupportFragmentManager().beginTransaction()
                        .setReorderingAllowed(true)
                        .replace(R.id.fragment_container, FragmentAvisosParroquiales.class, null)
                        .addToBackStack(null)
                        .commit();
            }
        });

        linearLayoutInformacion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getSupportFragmentManager().beginTransaction()
                        .setReorderingAllowed(true)
                        .replace(R.id.fragment_container, FragmentInformacion.class, null)
                        .addToBackStack(null)
                        .commit();
            }
        });

        linearLayoutPerfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getSupportFragmentManager().beginTransaction()
                        .setReorderingAllowed(true)
                        .replace(R.id.fragment_container, FragmentPerfil.class, null)
                        .addToBackStack(null)
                        .commit();
            }
        });

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            Usuario.actualizarUsuarioLocal(context, user);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(toggle.onOptionsItemSelected(item))
            return true;

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}
