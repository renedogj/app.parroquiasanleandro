package es.parroquiasanleandro.activitys;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Base64;
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
import java.util.Objects;

import es.parroquiasanleandro.Grupo;
import es.parroquiasanleandro.Menu;
import es.parroquiasanleandro.NotificacionSL;
import es.parroquiasanleandro.R;
import es.parroquiasanleandro.RSA;
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

        try {
            RSA rsa = new RSA(context);

            RSA localRSA = new RSA(context);
            localRSA.genKeyPair(2048);
            String userPublicKey = Base64.encodeToString(localRSA.PublicKey.getEncoded(), Base64.DEFAULT);
            Log.d("RSA PublicKey", userPublicKey);

            rsa.setPublicKeyString(Url.serverRSApublickey);

            String payload = "{\"idUsuario\":4,\"modGrupos\":20240427,\"totp\":null,\"PbK\":\"" + userPublicKey.substring(0,20) + "\"}";
//            Log.d("RSA clear text", payload);
//            Map<String, String> encryptedTextObject = rsa.encryptLongText(payload);
            String encryptedData = rsa.encrypt(payload);

//            Map<String, String> parametros = new HashMap<>();
//            parametros.put("encryptedData", encryptedData);
//            parametros.put("publicKey", userPublicKey.substring(20));
//            Log.d("RSA crypt text", parametros.toString());

//            String a = localRSA.decrypt("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");
//            Log.d("RSA a", a);
            
//            Volley.newRequestQueue(context).add(new StringRequest(Request.Method.POST, Url.actualizarDatos, result -> {
//                try {
//                    JSONObject jsonResult = new JSONObject(result);
//                    Log.d("RSA RESULT", jsonResult.toString());
//
//                    localRSA.decrypt(result);
//                } catch (JSONException | NoSuchAlgorithmException | NoSuchPaddingException |
//                         InvalidKeyException | IllegalBlockSizeException | BadPaddingException e) {
//                    e.printStackTrace();
//                }
//            }, error -> Toast.makeText(context, "Se ha producido un error al conectar con el servidor", Toast.LENGTH_SHORT).show()) {
//                @Override
//                protected Map<String, String> getParams() {
//                    Map<String, String> parametros = new HashMap<>();
//                    parametros.put("encryptedData", encryptedData);
//                    parametros.put("publicKey", userPublicKey.substring(20));
//                    return parametros;
//                }
//            });
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("ERROR RSA", e.toString());
            Log.e("ERROR RSA", Objects.requireNonNull(e.getMessage()));
        }



        Grupo.actualizarGruposServidorToLocal(context);

        Usuario usuario = Usuario.actualizarUsuarioDeServidorToLocal(context, this);

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
        NotificacionSL.programarNotificacion(context, NotificacionSL.CANAL_GENERAL,432000000L , true);//259200000L // --> 3d //86400000L --> 1d //3600000L --> 1H //432000000L --> 5d
    }
}
