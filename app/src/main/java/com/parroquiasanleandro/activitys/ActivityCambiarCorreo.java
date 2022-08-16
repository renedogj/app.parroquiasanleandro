package com.parroquiasanleandro.activitys;

import android.content.Context;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.parroquiasanleandro.R;

public class ActivityCambiarCorreo extends AppCompatActivity {
	private final Context context = ActivityCambiarCorreo.this;

	private EditText etnuevoCorreoElectronico;
	private Button bttnGuardarNuevoCorreo;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_cambiar_correo);

		/*getSupportFragmentManager().beginTransaction()
				.setReorderingAllowed(true)
				.add(R.id.fragment_container_view, FragmentConfirmarPassword.class, null)
				.commit();*/

		//etnuevoCorreoElectronico = findViewById(R.id.etnuevoCorreoElectronico);
		//bttnGuardarNuevoCorreo = findViewById(R.id.bttnGuardarNuevoCorreo);

		FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

		/*bttnGuardarNuevoCorreo.setOnClickListener(view1 -> {
			String nuevoCorreo = etnuevoCorreoElectronico.getText().toString().trim();
			/*if(Comprobaciones.comprobarCorreo(context,nuevoCorreo)) {
				if(!Usuario.cambiarCorreoElectronico(context, user, nuevoCorreo)){
					startActivity(new Intent(context, ActivityNavigation.class));
					Toast.makeText(context,"Correo actualizado con exito",Toast.LENGTH_SHORT).show();
					finish();
				}
			}*/
			/*Toast.makeText(context,nuevoCorreo, Toast.LENGTH_SHORT).show();
		});*/
	}
}