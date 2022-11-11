package es.parroquiasanleandro.activitys;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

import es.parroquiasanleandro.R;
import es.parroquiasanleandro.Usuario;
import es.parroquiasanleandro.utils.Comprobaciones;

;

public class ActivityRegistro extends AppCompatActivity {
	private final Context context = ActivityRegistro.this;

	private EditText etNombre;
	private EditText etCorreoElectronico;
	private EditText etPassword;
	private EditText etComprobarPassword;
	private Button bttnRegistrarse;
	private LinearLayout linearLayoutIniciarSesion;

	private ActionBar actionBar;

	private FirebaseAuth mAuth;

	private String nombre;
	private String email;
	private String password;
	private String comprobarPassword;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_registro);

		etNombre = findViewById(R.id.etNombre);
		etCorreoElectronico = findViewById(R.id.etCorreoElectronico);
		etPassword = findViewById(R.id.etContraseña);
		etComprobarPassword = findViewById(R.id.etComprobarContraseña);
		bttnRegistrarse = findViewById(R.id.bttnRegistrarse);
		linearLayoutIniciarSesion = findViewById(R.id.linearLayoutIniciarSesion);

		actionBar = getSupportActionBar();
		if (actionBar != null) {
			actionBar.setDisplayHomeAsUpEnabled(true);
			actionBar.setTitle("Registrarse");
		}

		mAuth = FirebaseAuth.getInstance();

		bttnRegistrarse.setOnClickListener(v -> {
			//etPassword.setInputType(0x00000001);
			nombre = etNombre.getText().toString().trim();
			email = etCorreoElectronico.getText().toString().trim();
			password = etPassword.getText().toString().trim();
			comprobarPassword = etComprobarPassword.getText().toString().trim();

			if (Comprobaciones.comprobarNombre(context, nombre) &&
					Comprobaciones.comprobarCorreo(context, email) &&
					Comprobaciones.comprobarPassword(context, password, comprobarPassword)) {
				registrarUsuario();
			}
		});

		linearLayoutIniciarSesion.setOnClickListener(v -> {
			startActivity(new Intent(getApplicationContext(), ActivityInicarSesion.class));
			finish();
		});
	}

	private void registrarUsuario() {
		mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, task -> {
			if (task.isSuccessful()) {
				FirebaseUser user = mAuth.getCurrentUser();
				if (user != null) {
					String nombre = etNombre.getText().toString().trim();
					Usuario usuario = new Usuario(nombre, email);
					FirebaseDatabase.getInstance().getReference("Usuarios").child(user.getUid()).setValue(usuario);
					FirebaseDatabase.getInstance().getReference("Usuarios").child(user.getUid()).child("suscripciones").child("A").setValue("General");

					user.updateProfile(new UserProfileChangeRequest.Builder().setDisplayName(nombre).build()).addOnCompleteListener(task1 -> {
						if (task1.isSuccessful()) {
							startActivity(new Intent(context, ActivityNavigation.class));
							finish();
						}
					});
				}
			} else {
				Toast.makeText(context, Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
			}
		});
	}
}