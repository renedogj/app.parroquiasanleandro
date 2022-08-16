package com.parroquiasanleandro.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.parroquiasanleandro.R;
import com.parroquiasanleandro.activitys.ActivityInicarSesion;

public class FragmentConfirmarPassword extends Fragment {
	private Context context;
	private Activity activity;

	private EditText etConfirmacionPassword;
	private ImageButton imgButtonShowPassword;
	private Button bttnConfirmarContraseña;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		context = getContext();
		activity = getActivity();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_confirmar_password, container, false);

		etConfirmacionPassword = view.findViewById(R.id.etConfirmacionPassword);
		imgButtonShowPassword = view.findViewById(R.id.imgButtonShowPassword);
		bttnConfirmarContraseña = view.findViewById(R.id.bttnConfirmarContraseña);

		FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

		imgButtonShowPassword.setOnClickListener(view1 -> {
			ActivityInicarSesion.changeShowPassword(etConfirmacionPassword,imgButtonShowPassword);
		});

		bttnConfirmarContraseña.setOnClickListener(view1 -> {
			//String password = etConfirmacionPassword.getText().toString().trim();
			/*if(Comprobaciones.comprobarCorreo(context,password)) {
				if(!Usuario.cambiarCorreoElectronico(context, user, password)){
					startActivity(new Intent(context, ActivityNavigation.class));
					activity.finish();
				}
			}*/;
			FragmentManager fragmentManager = getParentFragmentManager();
			fragmentManager.beginTransaction().remove(FragmentConfirmarPassword.this).commit();
		});
		return view;
	}
}