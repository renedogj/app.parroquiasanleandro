package es.parroquiasanleandro.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import es.parroquiasanleandro.Menu;
import es.parroquiasanleandro.R;
import es.parroquiasanleandro.Url;
import es.parroquiasanleandro.Usuario;
import es.parroquiasanleandro.activitys.ActivityCambiarInfoUsuario;
import es.parroquiasanleandro.activitys.ActivityConfiguracion;
import es.parroquiasanleandro.activitys.ActivityNavigation;
import es.parroquiasanleandro.adaptadores.GrupoSencilloAdaptador;
import es.parroquiasanleandro.utils.ItemViewModel;
import es.renedogj.fecha.Fecha;

public class FragmentPerfil extends Fragment {
    private Context context;
    private ItemViewModel viewModel;

    private ImageView ivFotoPerfil;
    private LinearLayout lnlytNombre;
    private TextView tvNombreUsuario;
    private TextView tvEmail;
    private LinearLayout lnlytEmail;
    private LinearLayout lnlytVerificarEmail;
    private LinearLayout lnlytGrupos;
    private RecyclerView rvGruposUsuario;
    private LinearLayout lnlytFechaNacimiento;
    private TextView tvFechaNacimiento;
    private LinearLayout lnlytConfiguaracionYPrivacidad;

    private Usuario usuario;

    public FragmentPerfil() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getContext();
        viewModel = new ViewModelProvider(requireActivity()).get(ItemViewModel.class);
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_perfil, container, false);

        ivFotoPerfil = view.findViewById(R.id.ivFotoPerfil);
        lnlytNombre = view.findViewById(R.id.linearLayoutNombre);
        tvNombreUsuario = view.findViewById(R.id.tvNombreUsuario);
        tvEmail = view.findViewById(R.id.tvEmail);
        lnlytEmail = view.findViewById(R.id.linearLayoutEmail);
        lnlytGrupos = view.findViewById(R.id.linearLayoutGrupos);
        lnlytFechaNacimiento = view.findViewById(R.id.lnlytFechaNacimiento);
        rvGruposUsuario = view.findViewById(R.id.rvGruposUsuario);
        tvFechaNacimiento = view.findViewById(R.id.tvFechaNacimiento);
        lnlytConfiguaracionYPrivacidad = view.findViewById(R.id.lnlytConfiguaracionYPrivacidad);
        lnlytVerificarEmail = view.findViewById(R.id.lnlytVerificarEmail);

        usuario = Usuario.recuperarUsuarioLocal(context);

        rvGruposUsuario.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        linearLayoutManager.setOrientation(RecyclerView.HORIZONTAL);
        rvGruposUsuario.setLayoutManager(linearLayoutManager);

        lnlytNombre.setOnClickListener(v -> {
            Intent intent = new Intent(context, ActivityCambiarInfoUsuario.class);
            intent.putExtra("tipoCambio", ActivityCambiarInfoUsuario.CAMBIAR_NOMBRE);
            startActivity(intent);
        });

        lnlytEmail.setOnClickListener(v -> {
            Intent intent = new Intent(context, ActivityCambiarInfoUsuario.class);
            intent.putExtra("tipoCambio", ActivityCambiarInfoUsuario.CAMBIAR_EMAIL);
            startActivity(intent);
        });

        if (!usuario.emailVerificado) {
            lnlytVerificarEmail.setVisibility(View.VISIBLE);
            lnlytVerificarEmail.setOnClickListener(v -> {
                Volley.newRequestQueue(context).add(new StringRequest(Request.Method.POST, Url.reenviarEmailConfirmacion, result -> {
                    Toast.makeText(context, "Se ha enviado un correo de verificación", Toast.LENGTH_SHORT).show();
                }, error -> {
                    Toast.makeText(context, "Se ha producido un error al conectar con el servidor", Toast.LENGTH_SHORT).show();
                    error.printStackTrace();
                }) {
                    @Override
                    protected Map<String, String> getParams() {
                        Map<String, String> parametros = new HashMap<>();
                        parametros.put("idUsuario", usuario.getId());
                        parametros.put("email", usuario.email);
                        return parametros;
                    }
                });
            });
        }

        lnlytGrupos.setOnClickListener(v -> {
            Menu.iniciarFragmentGrupos();
        });

        lnlytFechaNacimiento.setOnClickListener(v -> {
            Intent intent = new Intent(context, ActivityCambiarInfoUsuario.class);
            intent.putExtra("tipoCambio", ActivityCambiarInfoUsuario.CAMBIAR_FECHA);
            startActivity(intent);
        });

        lnlytConfiguaracionYPrivacidad.setOnClickListener(v -> {
            Intent intent = new Intent(context, ActivityConfiguracion.class);
            startActivity(intent);
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        ActivityNavigation.actionBar.setTitle(Menu.PERFIL);
        viewModel.setIdFragmentActual(Menu.FRAGMENT_PERFIL);
        viewModel.addIdFragmentActual();

        usuario = Usuario.recuperarUsuarioLocal(context);

        tvNombreUsuario.setText(usuario.nombre);
        tvEmail.setText(usuario.email);

        if (usuario.fechaNacimiento != null) {
            tvFechaNacimiento.setText(usuario.fechaNacimiento.toString(Fecha.FormatosFecha.dd_MMMM_aaaa));
        } else {
            tvFechaNacimiento.setText("No tienes guardada una fecha de nacimiento");
        }

        if (usuario.fotoPerfil != null) {
            Glide.with(context).load(usuario.fotoPerfil).into(ivFotoPerfil);
        }

        GrupoSencilloAdaptador grupoSencilloAdaptador = new GrupoSencilloAdaptador(context, Arrays.asList(usuario.getGruposSeguidos()));
        rvGruposUsuario.setAdapter(grupoSencilloAdaptador);
    }
}