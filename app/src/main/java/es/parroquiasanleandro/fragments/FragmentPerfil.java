package es.parroquiasanleandro.fragments;

import android.app.AlertDialog;
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

import com.bumptech.glide.Glide;

import java.util.Arrays;

import es.parroquiasanleandro.Menu;
import es.parroquiasanleandro.R;
import es.parroquiasanleandro.Usuario;
import es.parroquiasanleandro.activitys.ActivityCambiarInfoUsuario;
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
    private LinearLayout lnlytGrupos;
    private RecyclerView rvGruposUsuario;
    private LinearLayout lnlytFechaNacimiento;
    private TextView tvFechaNacimiento;
    private LinearLayout lnlytCambiarContraseña;
    private LinearLayout lnlytCerrarSesion;
    private LinearLayout lnlytEliminarUsuario;


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
        lnlytCambiarContraseña = view.findViewById(R.id.lnlytCambiarContraseña);
        lnlytCerrarSesion = view.findViewById(R.id.lnlytCerrarSesion);
        lnlytEliminarUsuario = view.findViewById(R.id.lnlytEliminarUsuario);

        Usuario usuario = Usuario.recuperarUsuarioLocal(context);
        tvNombreUsuario.setText(usuario.nombre);
        tvEmail.setText(usuario.email);

        if (usuario.fechaNacimiento != 0) {
            Fecha fechaNacimiento = Fecha.toFecha(usuario.fechaNacimiento);
            tvFechaNacimiento.setText(fechaNacimiento.toString(Fecha.FormatosFecha.dd_MMMM_aaaa));
        } else {
            tvFechaNacimiento.setText("No tienes guardada una fecha de nacimiento");
        }

        if (usuario.fotoPerfil != null) {
            Glide.with(context).load(usuario.fotoPerfil).into(ivFotoPerfil);
        }

        rvGruposUsuario.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        linearLayoutManager.setOrientation(RecyclerView.HORIZONTAL);
        rvGruposUsuario.setLayoutManager(linearLayoutManager);

        GrupoSencilloAdaptador grupoSencilloAdaptador = new GrupoSencilloAdaptador(context, Arrays.asList(usuario.getGruposSeguidos()));
        rvGruposUsuario.setAdapter(grupoSencilloAdaptador);

        lnlytNombre.setOnClickListener(v -> {
            Intent intent = new Intent(context, ActivityCambiarInfoUsuario.class);
            intent.putExtra("tipoCambio", ActivityCambiarInfoUsuario.CAMBIAR_NOMBRE);
            startActivity(intent);
            requireActivity().finish();
        });

        lnlytEmail.setOnClickListener(v -> {
            Intent intent = new Intent(context, ActivityCambiarInfoUsuario.class);
            intent.putExtra("tipoCambio", ActivityCambiarInfoUsuario.CAMBIAR_EMAIL);
            startActivity(intent);
            requireActivity().finish();
        });

        lnlytGrupos.setOnClickListener(v -> {
            Menu.iniciarFragmentGrupos();
        });

        lnlytFechaNacimiento.setOnClickListener(v -> {
            Intent intent = new Intent(context, ActivityCambiarInfoUsuario.class);
            intent.putExtra("tipoCambio", ActivityCambiarInfoUsuario.CAMBIAR_FECHA);
            startActivity(intent);
            requireActivity().finish();
        });

        lnlytCambiarContraseña.setOnClickListener(v -> {
            Intent intent = new Intent(context, ActivityCambiarInfoUsuario.class);
            intent.putExtra("tipoCambio", ActivityCambiarInfoUsuario.CAMBIAR_PASSWORD);
            startActivity(intent);
            requireActivity().finish();
        });

        lnlytCerrarSesion.setOnClickListener(v -> {
            ActivityNavigation.navView.getMenu().getItem(Menu.CERRAR_SESION).setVisible(false);
            Usuario.borrarUsuarioLocal(context);
            Toast.makeText(context, "Se ha cerrado sesión", Toast.LENGTH_SHORT).show();
            context.startActivity(new Intent(context, ActivityNavigation.class));
            requireActivity().finish();
        });

        lnlytEliminarUsuario.setOnClickListener(v -> {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
            alertDialog.setTitle("Eliminar Usuario");
            alertDialog.setMessage("¿Estás seguro de que quieres eliminar tu usuario?");
            alertDialog.setIcon(android.R.drawable.ic_dialog_alert);
            alertDialog.setPositiveButton("Eliminar", (dialog, whichButton) -> {
                Intent intent = new Intent(context, ActivityCambiarInfoUsuario.class);
                intent.putExtra("tipoCambio", ActivityCambiarInfoUsuario.ELIMINAR_USUARIO);
                startActivity(intent);
                requireActivity().finish();
            });
            alertDialog.setNegativeButton(android.R.string.no, null).show();
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        ActivityNavigation.actionBar.setTitle(Menu.PERFIL);
        viewModel.setIdFragmentActual(Menu.FRAGMENT_PERFIL);
        viewModel.addIdFragmentActual();
    }
}