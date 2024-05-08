package es.parroquiasanleandro.activitys;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;

import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.navigation.NavigationView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import es.parroquiasanleandro.Grupo;
import es.parroquiasanleandro.Menu;
import es.parroquiasanleandro.NotificacionSL;
import es.parroquiasanleandro.R;
import es.parroquiasanleandro.Url;
import es.parroquiasanleandro.Usuario;
import es.parroquiasanleandro.adaptadores.GrupoAdaptador;
import es.parroquiasanleandro.fragments.FragmentGrupos;
import es.parroquiasanleandro.utils.ItemViewModel;

public class ActivityNavigation extends AppCompatActivity {
    private final Context context = ActivityNavigation.this;
    private final Activity activity = ActivityNavigation.this;

    private LinearLayout linearLayoutInicio;
    private LinearLayout linearLayoutAvisos;
    private LinearLayout linearLayoutInformacion;
    private LinearLayout linearLayoutPerfil;
    public static ImageView imgInicio;
    public static ImageView imgAvisos;
    public static ImageView imgHorario;
    public static ImageView imgPerfil;

    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle toggle;

    public static NavigationView navView;
    public static ActionBar actionBar;
    public static FragmentManager fragmentManager;
    private ItemViewModel viewModel;

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

        if(ContextCompat.checkSelfPermission(context, Manifest.permission.SCHEDULE_EXACT_ALARM) != PackageManager.PERMISSION_GRANTED){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.SCHEDULE_EXACT_ALARM},999);
            }else{
                Log.d("PermisoNotificaciones", "No se han pedido permisos");
                crearCanalYProgramarNotificacion();
            }
        }else{
            crearCanalYProgramarNotificacion();
        }

        viewModel = new ViewModelProvider(this).get(ItemViewModel.class);

        actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("Parroquia San Leandro");
        }

        toggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        fragmentManager = getSupportFragmentManager();

        Usuario usuario = Usuario.actualiarDatosUsuarioServidorToLocal(context, this);

        if (usuario.getId() != null) {
            Menu.addCerrarSesion(navView);
        }

        linearLayoutInicio.setOnClickListener(v -> {
            if (viewModel.getIdFragmentActual() != Menu.FRAGMENT_INICIO) {
                Menu.iniciarFragmentInicio();
                Menu.asignarIconosMenu(Menu.FRAGMENT_INICIO);
            }
        });

        linearLayoutAvisos.setOnClickListener(v -> {
            if (viewModel.getIdFragmentActual() != Menu.FRAGMENT_AVISOS) {
                Menu.iniciarFragmentAvisos();
                Menu.asignarIconosMenu(Menu.FRAGMENT_AVISOS);
            }
        });

        linearLayoutInformacion.setOnClickListener(v -> {
            if (viewModel.getIdFragmentActual() != Menu.FRAGMENT_HORARIO) {
                Menu.iniciarFragmentHorario();
                Menu.asignarIconosMenu(Menu.FRAGMENT_HORARIO);
            }
        });

        linearLayoutPerfil.setOnClickListener(v -> {
            if (viewModel.getIdFragmentActual() != Menu.FRAGMENT_PERFIL) {
                Menu.iniciarFragmentPerfil(usuario, this, context);
                Menu.asignarIconosMenu(Menu.FRAGMENT_PERFIL);
            }
        });

        navView.setNavigationItemSelectedListener(item -> {
            viewModel.setIdFragmentActual(Menu.selecionarFragmentMenuItem(item, viewModel.getIdFragmentActual(), usuario, this, context));
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });
    }

    //Función que se ejecuta al selecionar una opción del menú
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
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
                            Menu.asignarIconosMenu(viewModel.getIdFragmentActual());
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
                    Menu.asignarIconosMenu(viewModel.getIdFragmentActual());
                    if (viewModel.getIdFragmentActual() == Menu.FRAGMENT_GRUPOS) {
                        //Si el nuevo grupo actual es el FRAGMENT_GRUPOS guardamos el grupo actual como el ID_PADRE
                        viewModel.getIdsGrupos().clear();
                        viewModel.setGrupoActual(Grupo.ID_PADRE);
                        viewModel.addIdGrupoActual();
                    }
                }
                super.onBackPressed();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == 999 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            crearCanalYProgramarNotificacion();
        }
    }

    public void crearCanalYProgramarNotificacion() {
        NotificacionSL.crearCanal(context, NotificacionSL.CANAL_GENERAL);
        NotificacionSL.programarNotificacion(context, NotificacionSL.CANAL_GENERAL,259200000L , true);//259200000L // --> 3d //86400000L --> 1d //3600000L --> 1H //432000000L --> 5d
    }
}
