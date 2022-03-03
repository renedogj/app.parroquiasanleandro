package com.parroquiasanleandro.activitys;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.parroquiasanleandro.Categoria;
import com.parroquiasanleandro.ItemViewModel;
import com.parroquiasanleandro.Menu;
import com.parroquiasanleandro.R;
import com.parroquiasanleandro.Url;
import com.parroquiasanleandro.Usuario;
import com.parroquiasanleandro.adaptadores.CategoriaAdaptador;
import com.parroquiasanleandro.fragments.FragmentCategorias;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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

    RequestQueue requestQueue;

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
            Usuario usuario = Usuario.recuperarUsuarioLocal(context);
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

                        //Obtener categorias del servidor y asignarselas a rvCategorias de FragmentCategorias
                        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Url.obtenerCategorias, response -> {
                            JSONObject jsonObject;
                            categorias.clear();
                            for (int i = 0; i < response.length(); i++) {
                                try {
                                    jsonObject = response.getJSONObject(i);
                                    Categoria categoria = new Categoria(jsonObject.getString("id"),jsonObject.getString("nombre"),jsonObject.getString("color"));
                                    categorias.add(categoria);
                                } catch (JSONException e) {
                                    Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                            CategoriaAdaptador categoriaAdaptador = new CategoriaAdaptador(context, categorias, vmIds.getCategoriaActual(), FragmentCategorias.rvCategorias, vmIds);
                            FragmentCategorias.rvCategorias.setAdapter(categoriaAdaptador);
                        }, error -> {
                            Toast.makeText(context, "Se ha producido un error al conectar con el servidor", Toast.LENGTH_SHORT).show();
                        }) {
                            @Override
                            protected Map<String, String> getParams() throws AuthFailureError {
                                return super.getParams();
                            }
                        };

                        requestQueue = Volley.newRequestQueue(context);
                        requestQueue.add(jsonArrayRequest);
                    }
                }else{
                    super.onBackPressed();
                }
            }else{
                //Controlar pila de fragmentos y de categorias
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
