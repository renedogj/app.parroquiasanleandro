package com.parroquiasanleandro;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.Toast;

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

public class ActivityNavigation extends AppCompatActivity {
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

        /*final View content = findViewById(android.R.id.content);
        content.getViewTreeObserver().addOnPreDrawListener(
                new ViewTreeObserver.OnPreDrawListener() {
                    @Override
                    public boolean onPreDraw() {
                        // Check if the initial data is ready.
                        if (mViewModel.isReady()) {
                            // The content is ready; start drawing.
                            content.getViewTreeObserver().removeOnPreDrawListener(this);
                            return true;
                        } else {
                            // The content is not ready; suspend.
                            return false;
                        }
                    }
                });*/

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

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            MenuItem item = navView.getMenu().add(0, 1, 0, "Cerrar Sesion");

            Usuario.actualizarUsuarioLocal(context, user);
        }

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .setReorderingAllowed(true)
                    .add(R.id.fragment_container, FragmentInicio.class, null)
                    .commit();
        }

        linearLayoutInicio.setOnClickListener(v -> {
            getSupportFragmentManager().beginTransaction()
                    .setReorderingAllowed(true)
                    .replace(R.id.fragment_container, FragmentInicio.class, null)
                    .addToBackStack(null)
                    .commit();

            actionBar.setTitle("Parroquia San Leandro");
        });

        linearLayoutAvisos.setOnClickListener(v -> {
            getSupportFragmentManager().beginTransaction()
                    .setReorderingAllowed(true)
                    .replace(R.id.fragment_container, FragmentAvisosParroquiales.class, null)
                    .addToBackStack(null)
                    .commit();

            actionBar.setTitle("Avisos");

        });

        linearLayoutInformacion.setOnClickListener(v -> {
            getSupportFragmentManager().beginTransaction()
                    .setReorderingAllowed(true)
                    .replace(R.id.fragment_container, FragmentInformacion.class, null)
                    .addToBackStack(null)
                    .commit();

            actionBar.setTitle("Información");
        });

        linearLayoutPerfil.setOnClickListener(v -> {
            if (user != null) {
                getSupportFragmentManager().beginTransaction()
                        .setReorderingAllowed(true)
                        .replace(R.id.fragment_container, FragmentPerfil.class, null)
                        .addToBackStack(null)
                        .commit();


                actionBar.setTitle("Perfil");
            } else {
                startActivity(new Intent(context, ActivityInicarSesion.class));
                activity.finish();
            }
        });

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
                                .replace(R.id.fragment_container, new FragmentAvisosParroquiales())
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
                    case 1:
                        FirebaseAuth.getInstance().signOut();
                        item.setVisible(false);
                        Usuario.borrarUsuarioLocal(context);
                        Toast.makeText(context, "Se ha cerrado sesión", Toast.LENGTH_SHORT).show();
                        break;
                }
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
    }
}
