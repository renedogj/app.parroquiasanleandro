package es.parroquiasanleandro.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import es.parroquiasanleandro.R;
import es.parroquiasanleandro.activitys.ActivityInicarSesion;


public class FragmentConfirmarPassword extends Fragment {
	private Context context;

	private EditText etConfirmacionPassword;
	private ImageButton imgButtonShowPassword;
	private Button bttnConfirmarContraseña;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		context = getContext();
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
			String password = etConfirmacionPassword.getText().toString().trim();
			if(!password.equals("")){
				AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(), password);

				user.reauthenticate(credential).addOnSuccessListener(unused -> {
					FragmentManager fragmentManager = getParentFragmentManager();
					fragmentManager.beginTransaction().remove(FragmentConfirmarPassword.this).commit();
				}).addOnFailureListener(e -> {
					Toast.makeText(context,"Contraseña incorrecta",Toast.LENGTH_SHORT).show();
				});
			}else{
				Toast.makeText(context,"Introduce tu contraseña",Toast.LENGTH_SHORT).show();
			}
		});
		return view;
	}
}