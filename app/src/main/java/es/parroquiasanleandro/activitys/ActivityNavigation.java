package es.parroquiasanleandro.activitys;

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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import es.parroquiasanleandro.Categoria;
import es.parroquiasanleandro.Menu;
import es.parroquiasanleandro.R;
import es.parroquiasanleandro.Url;
import es.parroquiasanleandro.Usuario;
import es.parroquiasanleandro.adaptadores.CategoriaAdaptador;
import es.parroquiasanleandro.fragments.FragmentCategorias;
import es.parroquiasanleandro.utils.ItemViewModel;

;

public class ActivityNavigation extends AppCompatActivity {
    private final Activity activity = ActivityNavigation.this;
    private final Context context = ActivityNavigation.this;

    private LinearLayout linearLayoutInicio;
    private LinearLayout linearLayoutAvisos;
    private LinearLayout linearLayoutInformacion;
    private LinearLayout linearLayoutPerfil;
    public static ImageView imgInicio;
    public static ImageView imgAvisos;
    public static ImageView imgHorario;
    public static ImageView imgPerfil;

    private DrawerLayout drawerLayout;
    private NavigationView navView;
    private ActionBar actionBar;
    private ActionBarDrawerToggle toggle;

    private FragmentManager fragmentManager;
    private ItemViewModel viewModel;
    private RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);

        linearLayoutInicio = findViewById(R.id.linearLayoutInicio);
        linearLayoutAvisos = findViewById(R.id.linearLayoutAvisos);
        linearLayoutInformacion = findViewById(R.id.linearLayoutHorario);
        linearLayoutPerfil = findViewById(R.id.linearLayoutPerfil);

        imgInicio = findViewById(R.id.imgInicio);
        imgAvisos = findViewById(R.id.imgAvisos);
        imgHorario = findViewById(R.id.imgHorario);
        imgPerfil = findViewById(R.id.imgPerfil);

        navView = findViewById(R.id.navView);
        drawerLayout = findViewById(R.id.drawerLayout);

        viewModel = new ViewModelProvider(this).get(ItemViewModel.class);
        viewModel.setIdFragmentActual(Menu.FRAGMENT_INICIO);
        viewModel.addIdFragmentActual();
        viewModel.setNavView(navView);

        actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("Parroquia San Leandro");
            viewModel.setActionBar(actionBar);
        }

        toggle = new ActionBarDrawerToggle(activity, drawerLayout, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        fragmentManager = getSupportFragmentManager();

        Categoria.actualizarCategoriasServidorToLocal(context);
        Usuario usuario = Usuario.recuperarUsuarioLocal(context);

        /*FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            Menu.addCerrarSesion(navView);
            Usuario.actualizarUsuarioLocal(context, user);
        }

        FirebaseDatabase.getInstance().getReference().child("infoGeneral").child("fechaModCategorias").get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult().getValue() != null) {
                long fechaModCategorias = task.getResult().getValue(long.class);
                if(fechaModCategorias > Categoria.getMillisUltimaActualizacion(context)){
                    Categoria.actualizarCategoriasServidorToLocal(context);
                    Categoria.setMillisUltimaActualizacion(context,fechaModCategorias);
                }
            }
        });*/

        if(usuario.getId() != null){
            Menu.addCerrarSesion(navView);
        }

        linearLayoutInicio.setOnClickListener(v -> {
            if (viewModel.getIdFragmentActual() != Menu.FRAGMENT_INICIO) {
                Menu.iniciarFragmentInicio(fragmentManager, actionBar);
                Menu.asignarIconosMenu(navView,Menu.FRAGMENT_INICIO);
            }
        });

        linearLayoutAvisos.setOnClickListener(v -> {
            if (viewModel.getIdFragmentActual() != Menu.FRAGMENT_AVISOS) {
                Menu.iniciarFragmentAvisos(fragmentManager, actionBar);
                Menu.asignarIconosMenu(navView,Menu.FRAGMENT_AVISOS);
            }
        });

        linearLayoutInformacion.setOnClickListener(v -> {
            if (viewModel.getIdFragmentActual() != Menu.FRAGMENT_HORARIO) {
                Menu.iniciarFragmentHorario(fragmentManager, actionBar);
                Menu.asignarIconosMenu(navView,Menu.FRAGMENT_HORARIO);
            }
        });

        linearLayoutPerfil.setOnClickListener(v -> {
            if (viewModel.getIdFragmentActual() != Menu.FRAGMENT_PERFIL) {
                Menu.iniciarFragmentPerfil(usuario, activity, context, fragmentManager, actionBar);
                Menu.asignarIconosMenu(navView,Menu.FRAGMENT_PERFIL);
            }
        });

        navView.setNavigationItemSelectedListener(item -> {
            viewModel.setIdFragmentActual(Menu.selecionarItemMenu(item, viewModel.getIdFragmentActual(), usuario, activity, context, fragmentManager, actionBar, navView));
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });
    }

    /*@Override
    protected void onRestart() {
        super.onRestart();
        Menu.asignarIconosMenu(navView,viewModel.getIdFragmentActual());
    }

    @Override
    protected void onResume() {
        super.onResume();
        Menu.asignarIconosMenu(navView,viewModel.getIdFragmentActual());
    }*/

    //Función que se ejecuta al selecionar una opción del menú
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
            int posUltimoFragment = viewModel.getIdsFragment().size() - 1;
            int ultimoFragment = viewModel.getIdsFragment().get(posUltimoFragment);
            if(ultimoFragment == Menu.FRAGMENT_CATEGORIAS) {
                if(!viewModel.getIdsCategorias().isEmpty()) {
                    if (viewModel.getCategoriaActual().equals(Categoria.ID_PADRE)) {
                        super.onBackPressed();
                    } else {
                        int posUltimaCategoria = viewModel.getIdsCategorias().size() - 1;
                        viewModel.getIdsCategorias().remove(posUltimaCategoria);
                        viewModel.setCategoriaActual(viewModel.getIdsCategorias().get(posUltimaCategoria - 1));
                        List<Categoria> categorias = new ArrayList<>();

                        //Obtener categorias del servidor y asignarselas a rvCategorias de FragmentCategorias
                        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Url.obtenerCategorias, response -> {
                            JSONObject jsonObject;
                            categorias.clear();
                            for (int i = 0; i < response.length(); i++) {
                                try {
                                    jsonObject = response.getJSONObject(i);
                                    Categoria categoria = new Categoria(
                                            jsonObject.getString(Categoria.ID),
                                            jsonObject.getString(Categoria.NOMBRE),
                                            jsonObject.getString(Categoria.COLOR),
                                            jsonObject.getString(Categoria.IMAGEN)
                                    );
                                    categorias.add(categoria);
                                } catch (JSONException e) {
                                    Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                            CategoriaAdaptador categoriaAdaptador = new CategoriaAdaptador(context, categorias, viewModel.getCategoriaActual(), FragmentCategorias.rvCategorias, viewModel);
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
                viewModel.getIdsFragment().remove(posUltimoFragment);
                if (!viewModel.getIdsFragment().isEmpty()) {
                    viewModel.setIdFragmentActual(viewModel.getIdsFragment().get(posUltimoFragment - 1));
                    Menu.asignarIconosMenu(navView, viewModel.getIdFragmentActual());
                    if (viewModel.getIdFragmentActual() == Menu.FRAGMENT_CATEGORIAS) {
                        viewModel.getIdsCategorias().clear();
                        onBackPressed();
                    }
                }
            }
        }
    }
}
