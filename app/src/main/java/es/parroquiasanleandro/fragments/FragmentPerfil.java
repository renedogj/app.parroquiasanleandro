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
import es.parroquiasanleandro.adaptadores.CategoriaSencillaAdaptador;
import es.parroquiasanleandro.fecha.Fecha;
import es.parroquiasanleandro.utils.ItemViewModel;

public class FragmentPerfil extends Fragment {
	private Context context;

	private ImageView ivFotoPerfil;
	private LinearLayout linearLayoutNombre;
	private TextView tvNombreUsuario;
	private TextView tvEmail;
	private LinearLayout linearLayoutEmail;
	private LinearLayout linearLayoutCategorias;
	private RecyclerView rvCategoriasUsuario;
	private LinearLayout linearLayoutFechaNacimiento;
	private TextView tvFechaNacimiento;

	private FragmentManager fragmentManager;
	private ItemViewModel vmIds;

	public FragmentPerfil() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		context = getContext();

		vmIds = new ViewModelProvider(requireActivity()).get(ItemViewModel.class);
		vmIds.setIdFragmentActual(Menu.FRAGMENT_PERFIL);
		vmIds.addIdFragmentActual();
	}

	public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_perfil, container, false);

		ivFotoPerfil = view.findViewById(R.id.ivFotoPerfil);
		linearLayoutNombre = view.findViewById(R.id.linearLayoutNombre);
		tvNombreUsuario = view.findViewById(R.id.tvNombreUsuario);
		tvEmail = view.findViewById(R.id.tvEmail);
		linearLayoutEmail = view.findViewById(R.id.linearLayoutEmail);
		linearLayoutCategorias = view.findViewById(R.id.linearLayoutCategorias);
		linearLayoutFechaNacimiento = view.findViewById(R.id.linearLayoutFechaNacimiento);
		rvCategoriasUsuario = view.findViewById(R.id.rvCategoriasUsuario);
		tvFechaNacimiento = view.findViewById(R.id.tvFechaNacimiento);

		vmIds = new ViewModelProvider(requireActivity()).get(ItemViewModel.class);

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
		fragmentManager = getParentFragmentManager();
		linearLayoutNombre.setOnClickListener(v -> {
			Toast.makeText(context, "Modificar nombre", Toast.LENGTH_SHORT).show();
		});

		linearLayoutEmail.setOnClickListener(v -> {
			/*Menu.inicarFragmentCambiarCorreo(fragmentManager);*/
			startActivity(new Intent(context, ActivityCambiarCorreo.class));
		});

		linearLayoutCategorias.setOnClickListener(v -> {
			Menu.iniciarFragmentCategorias(fragmentManager);
		});

		linearLayoutFechaNacimiento.setOnClickListener(v -> {
			Toast.makeText(context, "Modificar fecha de nacimineto", Toast.LENGTH_SHORT).show();
		});

		rvCategoriasUsuario.setHasFixedSize(true);
		LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
		linearLayoutManager.setOrientation(RecyclerView.HORIZONTAL);
		rvCategoriasUsuario.setLayoutManager(linearLayoutManager);

		CategoriaSencillaAdaptador categoriaSencillaAdaptador = new CategoriaSencillaAdaptador(context, Arrays.asList(usuario.getCategorias()));
		rvCategoriasUsuario.setAdapter(categoriaSencillaAdaptador);

		return view;
	}
}