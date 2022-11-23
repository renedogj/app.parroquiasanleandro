package es.parroquiasanleandro.activitys;

import android.content.Context;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import es.parroquiasanleandro.R;

public class ActivityCambiarCorreo extends AppCompatActivity {
	private final Context context = ActivityCambiarCorreo.this;

	private EditText etnuevoCorreoElectronico;
	private Button bttnGuardarNuevoCorreo;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_cambiar_correo);

		etnuevoCorreoElectronico = findViewById(R.id.etnuevoCorreoElectronico);
		bttnGuardarNuevoCorreo = findViewById(R.id.bttnGuardarNuevoCorreo);

		/*FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

		bttnGuardarNuevoCorreo.setOnClickListener(view1 -> {
			String nuevoCorreo = etnuevoCorreoElectronico.getText().toString().trim();
			if(Comprobaciones.comprobarCorreo(context,nuevoCorreo)) {
				user.updateEmail(nuevoCorreo).addOnSuccessListener(unused -> {
					SharedPreferences sharedPreferences = context.getSharedPreferences(Usuario.USUARIO, Context.MODE_PRIVATE);
					SharedPreferences.Editor editor = sharedPreferences.edit();
					editor.putString(Usuario.EMAIL, user.getEmail());
					editor.apply();
					FirebaseDatabase.getInstance().getReference().child(Usuario.USUARIOS).child(user.getUid()).child(Usuario.EMAIL).setValue(nuevoCorreo);
					startActivity(new Intent(context, ActivityNavigation.class));
					Toast.makeText(context,"Correo actualizado con exito",Toast.LENGTH_SHORT).show();
					finish();
				}).addOnFailureListener(e -> {
					Log.e("UPDATE EMAIL ERROR", e.getMessage());
					Toast.makeText(context, "Se ha producido un error al actualizar el correo electronico", Toast.LENGTH_SHORT).show();
					Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
				});
			}
		});*/
	}
}