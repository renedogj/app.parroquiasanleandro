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
        //viewModel.setIdFragmentActual(Menu.FRAGMENT_INICIO);
        //viewModel.addIdFragmentActual();
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

        Usuario usuario = Usuario.recuperarUsuarioLocal(context);
        //Log.d("MENU",Menu.menuOptionMap.size()+"");
        //Usuario usuario = new Usuario();

        /*FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            Menu.addCerrarSesion(navView);
            Usuario.actualizarUsuarioLocal(context, user);
        }

        FirebaseDatabase.getInstance().getReference().child("infoGeneral").child("fechaModCategorias").get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult().getValue() != null) {
                long fechaModCategorias = task.getResult().getValue(long.class);
                if(fechaModCategorias > Grupo.getMillisUltimaActualizacion(context)){
                    Grupo.actualizarCategoriasServidorToLocal(context);
                    Grupo.setMillisUltimaActualizacion(context,fechaModCategorias);
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
            viewModel.setIdFragmentActual(Menu.selecionarFragmentMenuItem(item, viewModel.getIdFragmentActual(), usuario, activity, context, fragmentManager, actionBar, navView));
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        //Log.d("ON RESTART","VM "+viewModel.getIdsFragment());
        //Menu.asignarIconosMenu(navView,viewModel.getIdFragmentActual());
    }

    @Override
    protected void onResume() {
        super.onResume();
        /*Log.d("ON RESUME","VM "+viewModel.getIdsFragment());
        //viewModel.getIdsFragment().clear();
        int listSize = viewModel.getIdsFragment().size();
        if(listSize > 1){
            for (int i = listSize/2; i < listSize-1; i++){
                viewModel.getIdsFragment().remove(i);
                //Log.d("ON RESUME","REMOVE "+viewModel.getIdsFragment().get(i));
            }
        }

        Log.d("ON RESUME","VM "+viewModel.getIdsFragment());*/
        Menu.asignarIconosMenu(navView,viewModel.getIdFragmentActual());
    }

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
            if(ultimoFragment == Menu.FRAGMENT_GRUPOS) {
                if(!viewModel.getIdsGrupos().isEmpty()) {
                    if (viewModel.getGrupoActual().equals(Grupo.ID_PADRE)) {
                        super.onBackPressed();
                    } else {
                        int posUltimaCategoria = viewModel.getIdsGrupos().size() - 1;
                        viewModel.getIdsGrupos().remove(posUltimaCategoria);
                        viewModel.setGrupoActual(viewModel.getIdsGrupos().get(posUltimaCategoria - 1));
                        List<Grupo> grupos = new ArrayList<>();

                        //Obtener grupos del servidor y asignarselas a rvCategorias de FragmentGrupos
                        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Url.obtenerGrupos, response -> {
                            JSONObject jsonObject;
                            grupos.clear();
                            for (int i = 0; i < response.length(); i++) {
                                try {
                                    jsonObject = response.getJSONObject(i);

                                    Grupo grupo = new Grupo(
                                            jsonObject.getString(Grupo.ID),
                                            jsonObject.getString(Grupo.NOMBRE),
                                            jsonObject.getString(Grupo.COLOR),
                                            jsonObject.getString(Grupo.IMAGEN)
                                    );
                                    grupos.add(grupo);
                                } catch (JSONException e) {
                                    Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                            GrupoAdaptador grupoAdaptador = new GrupoAdaptador(context, grupos, viewModel.getGrupoActual(), FragmentGrupos.rvGrupos, viewModel);
                            FragmentGrupos.rvGrupos.setAdapter(grupoAdaptador);
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
                //Controlar pila de fragmentos y de grupos
                super.onBackPressed();
                viewModel.getIdsFragment().remove(posUltimoFragment);
                if (!viewModel.getIdsFragment().isEmpty()) {
                    viewModel.setIdFragmentActual(viewModel.getIdsFragment().get(posUltimoFragment - 1));
                    Menu.asignarIconosMenu(navView, viewModel.getIdFragmentActual());
                    if (viewModel.getIdFragmentActual() == Menu.FRAGMENT_GRUPOS) {
                        viewModel.getIdsGrupos().clear();
                        onBackPressed();
                    }
                }
            }
        }
    }
}
