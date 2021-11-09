package com.parroquiasanleandro;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class ActivityCategorias extends AppCompatActivity {
    private final Activity activity = ActivityCategorias.this;
    private final Context context = ActivityCategorias.this;

    private RecyclerView rvCategorias;

    private DrawerLayout drawerLayout;
    private NavigationView navView;
    private ActionBar actionBar;
    private ActionBarDrawerToggle toggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categorias);

        rvCategorias = findViewById(R.id.rvCategorias);

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

        MenuItem item = navView.getMenu().add(0, 1, 0, "Cerrar Sesion");

        Usuario usuario = Usuario.recuperarUsuarioLocal(context);

        rvCategorias.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        rvCategorias.setLayoutManager(linearLayoutManager);

        List<Categoria> categorias = new ArrayList<>();

        FirebaseDatabase.getInstance().getReference().child(Categoria.CATEGORIAS).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull @NotNull DataSnapshot snapshot, @Nullable String previousChildName) {
                String nombreCategoria = snapshot.getValue(String.class);
                String key = snapshot.getKey();
                categorias.add(new Categoria(key, nombreCategoria));

                CategoriaAdaptador categoriaAdaptador = new CategoriaAdaptador(context, categorias, usuario);
                rvCategorias.setAdapter(categoriaAdaptador);
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
        navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @SuppressLint("NonConstantResourceId")
            @Override
            public boolean onNavigationItemSelected(@NonNull @NotNull MenuItem item) {
                Menu.selecionarItemMenu(item,context,getSupportFragmentManager());
                /*switch (item.getItemId()) {
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
                }*/
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