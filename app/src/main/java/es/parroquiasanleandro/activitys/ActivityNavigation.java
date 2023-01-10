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

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.navigation.NavigationView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import es.parroquiasanleandro.Grupo;
import es.parroquiasanleandro.Menu;
import es.parroquiasanleandro.R;
import es.parroquiasanleandro.Url;
import es.parroquiasanleandro.Usuario;
import es.parroquiasanleandro.adaptadores.GrupoAdaptador;
import es.parroquiasanleandro.fragments.FragmentGrupos;
import es.parroquiasanleandro.utils.ItemViewModel;

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
        Grupo.actualizarGruposServidorToLocal(context);

        Usuario usuario = Usuario.actualizarUsuarioDeServidorToLocal(context);

        if (usuario.getId() != null) {
            Menu.addCerrarSesion(navView);
        }

        linearLayoutInicio.setOnClickListener(v -> {
            if (viewModel.getIdFragmentActual() != Menu.FRAGMENT_INICIO) {
                Menu.iniciarFragmentInicio(fragmentManager, actionBar);
                Menu.asignarIconosMenu(navView, Menu.FRAGMENT_INICIO);
            }
        });

        linearLayoutAvisos.setOnClickListener(v -> {
            if (viewModel.getIdFragmentActual() != Menu.FRAGMENT_AVISOS) {
                Menu.iniciarFragmentAvisos(fragmentManager, actionBar);
                Menu.asignarIconosMenu(navView, Menu.FRAGMENT_AVISOS);
            }
        });

        linearLayoutInformacion.setOnClickListener(v -> {
            if (viewModel.getIdFragmentActual() != Menu.FRAGMENT_HORARIO) {
                Menu.iniciarFragmentHorario(fragmentManager, actionBar);
                Menu.asignarIconosMenu(navView, Menu.FRAGMENT_HORARIO);
            }
        });

        linearLayoutPerfil.setOnClickListener(v -> {
            if (viewModel.getIdFragmentActual() != Menu.FRAGMENT_PERFIL) {
                Menu.iniciarFragmentPerfil(usuario, activity, context, fragmentManager, actionBar);
                Menu.asignarIconosMenu(navView, Menu.FRAGMENT_PERFIL);
            }
        });

        navView.setNavigationItemSelectedListener(item -> {
            viewModel.setIdFragmentActual(Menu.selecionarFragmentMenuItem(item, viewModel.getIdFragmentActual(), usuario, activity, context, fragmentManager, actionBar, navView));
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });
    }

    //Función que se ejecuta al selecionar una opción del menú
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (toggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Los super.onBackPressed() deben ir al final para navegar al fragment anterior solo una vez borrado la información del fragment actual
     */
    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            int posUltimoFragment = viewModel.getIdsFragment().size() - 1;
            int ultimoFragment = viewModel.getIdsFragment().get(posUltimoFragment);
            if (ultimoFragment == Menu.FRAGMENT_GRUPOS) {
                if (!viewModel.getIdsGrupos().isEmpty()) {
                    if (viewModel.getGrupoActual().equals(Grupo.ID_PADRE)) {
                        //Si el grupo actual es el ID_PADRE al volver atras deber cerrar el fragment actual (FRAGMENT_GRUPOS)
                        //Por eso borramos la información del fragment actual y la información de los grupos
                        viewModel.getIdsFragment().remove(posUltimoFragment);
                        if (!viewModel.getIdsFragment().isEmpty()) {
                            //Si no es el último fragment (no debería serlo) asignamos los valores del nuevo fragment actual
                            viewModel.setIdFragmentActual(viewModel.getIdsFragment().get(posUltimoFragment - 1));
                            Menu.asignarIconosMenu(navView, viewModel.getIdFragmentActual());
                        }
                        viewModel.getIdsGrupos().clear();
                        viewModel.setGrupoActual(null);
                        super.onBackPressed();
                    } else {
                        //Si el grupoActual no es el ID_PADRE obtenemos el nuevo grupoActual y obtenemos sus subgrupos
                        int posUltimoGrupo = viewModel.getIdsGrupos().size() - 1;
                        viewModel.getIdsGrupos().remove(posUltimoGrupo);
                        viewModel.setGrupoActual(viewModel.getIdsGrupos().get(posUltimoGrupo - 1));
                        List<Grupo> grupos = new ArrayList<>();

                        //Obtener grupos del servidor y asignarselas a rvCategorias de FragmentGrupos
                        Volley.newRequestQueue(context).add(new JsonArrayRequest(Url.obtenerGrupos, response -> {
                            JSONObject jsonObject;
                            grupos.clear();
                            for (int i = 0; i < response.length(); i++) {
                                try {
                                    jsonObject = response.getJSONObject(i);
                                    grupos.add(new Grupo(jsonObject.getString(Grupo.ID), jsonObject.getString(Grupo.NOMBRE), jsonObject.getString(Grupo.COLOR), jsonObject.getString(Grupo.IMAGEN)));
                                } catch (JSONException e) {
                                    Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                            GrupoAdaptador grupoAdaptador = new GrupoAdaptador(context, grupos, viewModel.getGrupoActual(), FragmentGrupos.rvGrupos, viewModel);
                            FragmentGrupos.rvGrupos.setAdapter(grupoAdaptador);
                        }, error -> {
                            Toast.makeText(context, "Se ha producido un error al conectar con el servidor", Toast.LENGTH_SHORT).show();
                        }));
                    }
                } else {
                    //Quitamos el último fragmento (FRAGMENT_GRUPOS) pues si idsGrupos está vacio tiene que salir del fragment
                    viewModel.getIdsFragment().remove(posUltimoFragment);
                    super.onBackPressed();
                }
            } else {
                //Controlar pila de fragmentos y de grupos
                viewModel.getIdsFragment().remove(posUltimoFragment);
                if (!viewModel.getIdsFragment().isEmpty()) {
                    viewModel.setIdFragmentActual(viewModel.getIdsFragment().get(posUltimoFragment - 1));
                    Menu.asignarIconosMenu(navView, viewModel.getIdFragmentActual());
                    if (viewModel.getIdFragmentActual() == Menu.FRAGMENT_GRUPOS) {
                        //Si el nuevo grupo actual el FRAGMENT_GRUPOS guardamos el grupo actual como el ID_PADRE
                        viewModel.getIdsGrupos().clear();
                        viewModel.setGrupoActual(Grupo.ID_PADRE);
                        viewModel.addIdGrupo();
                    }
                }
                super.onBackPressed();
            }
        }
    }
}
