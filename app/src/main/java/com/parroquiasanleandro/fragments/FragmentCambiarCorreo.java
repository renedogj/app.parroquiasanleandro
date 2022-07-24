package com.parroquiasanleandro.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.parroquiasanleandro.R;
import com.parroquiasanleandro.Usuario;
import com.parroquiasanleandro.activitys.ActivityNavigation;
import com.parroquiasanleandro.utils.Comprobaciones;

public class FragmentCambiarCorreo extends Fragment {
	private Context context;
	private Activity activity;

	private EditText etnuevoCorreoElectronico;
	private Button bttnGuardarNuevoCorreo;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		context = getContext();
		activity = getActivity();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_cambiar_correo, container, false);

		etnuevoCorreoElectronico = view.findViewById(R.id.etnuevoCorreoElectronico);
		bttnGuardarNuevoCorreo = view.findViewById(R.id.bttnGuardarNuevoCorreo);

		FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

		bttnGuardarNuevoCorreo.setOnClickListener(view1 -> {
			String nuevoCorreo = etnuevoCorreoElectronico.getText().toString().trim();
			if(Comprobaciones.comprobarCorreo(context,nuevoCorreo)) {
				if(!Usuario.cambiarCorreoElectronico(context, user, nuevoCorreo,activity)){
					startActivity(new Intent(context, ActivityNavigation.class));
					activity.finish();
				}
			}
		});
		return view;
	}
}