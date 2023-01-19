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
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.Arrays;

import es.parroquiasanleandro.Menu;
import es.parroquiasanleandro.R;
import es.parroquiasanleandro.Usuario;
import es.parroquiasanleandro.activitys.ActivityCambiarCorreo;
import es.parroquiasanleandro.adaptadores.GrupoSencilloAdaptador;
import es.parroquiasanleandro.utils.ItemViewModel;
import es.renedogj.fecha.Fecha;

public class FragmentPerfil extends Fragment {
	private Context context;
	private ItemViewModel viewModel;

	private ImageView ivFotoPerfil;
	private LinearLayout linearLayoutNombre;
	private TextView tvNombreUsuario;
	private TextView tvEmail;
	private LinearLayout linearLayoutEmail;
	private LinearLayout linearLayoutGrupos;
	private RecyclerView rvGruposUsuario;
	private LinearLayout linearLayoutFechaNacimiento;
	private TextView tvFechaNacimiento;

	private FragmentManager fragmentManager;

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
		linearLayoutNombre = view.findViewById(R.id.linearLayoutNombre);
		tvNombreUsuario = view.findViewById(R.id.tvNombreUsuario);
		tvEmail = view.findViewById(R.id.tvEmail);
		linearLayoutEmail = view.findViewById(R.id.linearLayoutEmail);
		linearLayoutGrupos = view.findViewById(R.id.linearLayoutGrupos);
		linearLayoutFechaNacimiento = view.findViewById(R.id.linearLayoutFechaNacimiento);
		rvGruposUsuario = view.findViewById(R.id.rvGruposUsuario);
		tvFechaNacimiento = view.findViewById(R.id.tvFechaNacimiento);

		fragmentManager = getParentFragmentManager();

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

		linearLayoutNombre.setOnClickListener(v -> {
			Toast.makeText(context, "Modificar nombre", Toast.LENGTH_SHORT).show();
		});

		linearLayoutEmail.setOnClickListener(v -> {
			startActivity(new Intent(context, ActivityCambiarCorreo.class));
			requireActivity().finish();
		});

		linearLayoutGrupos.setOnClickListener(v -> {
			Menu.iniciarFragmentGrupos(fragmentManager, viewModel.getActionBar());
		});

		linearLayoutFechaNacimiento.setOnClickListener(v -> {
			Toast.makeText(context, "Modificar fecha de nacimineto", Toast.LENGTH_SHORT).show();
		});

		rvGruposUsuario.setHasFixedSize(true);
		LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
		linearLayoutManager.setOrientation(RecyclerView.HORIZONTAL);
		rvGruposUsuario.setLayoutManager(linearLayoutManager);

		GrupoSencilloAdaptador grupoSencilloAdaptador = new GrupoSencilloAdaptador(context, Arrays.asList(usuario.getGruposSeguidos()));
		rvGruposUsuario.setAdapter(grupoSencilloAdaptador);

		return view;
	}

	@Override
	public void onResume() {
		super.onResume();
		viewModel.setIdFragmentActual(Menu.FRAGMENT_PERFIL);
		viewModel.addIdFragmentActual();
	}
}