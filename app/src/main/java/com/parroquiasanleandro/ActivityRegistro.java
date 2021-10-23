package com.parroquiasanleandro;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

public class ActivityRegistro extends AppCompatActivity {

    private final Context context = ActivityRegistro.this;

    private EditText etNombre;
    private EditText etCorreoElectronico;
    private EditText etContraseña;
    private EditText etComprobarContraseña;
    private Button bttnRegistrarse;
    private TextView tvIniciarSesion;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        etNombre = findViewById(R.id.etNombre);
        etCorreoElectronico = findViewById(R.id.etCorreoElectronico);
        etContraseña = findViewById(R.id.etContraseña);
        etComprobarContraseña = findViewById(R.id.etComprobarContraseña);
        bttnRegistrarse = findViewById(R.id.bttnRegistrarse);
        tvIniciarSesion = findViewById(R.id.tvIniciarSesion);

        mAuth = FirebaseAuth.getInstance();


        bttnRegistrarse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (comprobarNombre() && comprobarCorreo() && comprobarContraseña()) {
                    registrarUsuario();
                }
            }
        });

        tvIniciarSesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), ActivityInicarSesion.class));
            }
        });
    }

    public boolean comprobarNombre() {
        if (!etNombre.getText().toString().trim().equals("")) {
            return true;
        }
        Toast.makeText(context, "El nombre no puede estar vacio", Toast.LENGTH_SHORT).show();
        return false;
    }

    public boolean comprobarCorreo() {
        String correo = etCorreoElectronico.getText().toString().trim();
        if (!correo.equals("")) {
            if (correo.length() >= 5) {
                if (correo.contains("@") && correo.contains(".")) {
                    if (correo.indexOf("@") == correo.lastIndexOf("@")
                            && correo.indexOf("@") < correo.lastIndexOf(".")
                            && correo.lastIndexOf(".") < correo.length()) {
                        return true;
                    }
                    Toast.makeText(this, "Es necesario introducir un correo electronico valido", Toast.LENGTH_SHORT).show();
                    return false;
                }
                Toast.makeText(this, "Es necesario introducir un correo electronico valido", Toast.LENGTH_SHORT).show();
                return false;
            }
            Toast.makeText(context, "El correo electronico debe contener al menos 5 caracteres", Toast.LENGTH_SHORT).show();
            return false;
        }
        Toast.makeText(context, "Es necesario introducir un correo electronico", Toast.LENGTH_SHORT).show();
        return false;
    }

    private boolean comprobarContraseña() {
        String contraseña = etContraseña.getText().toString().trim();
        String comprobacion = etComprobarContraseña.getText().toString().trim();
        if (!contraseña.equals("") && contraseña.equals(comprobacion)) {
            return true;
        } else {
            Toast.makeText(context, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    private void registrarUsuario() {
        String email = etCorreoElectronico.getText().toString().trim();
        String contraseña = etContraseña.getText().toString().trim();
        mAuth.createUserWithEmailAndPassword(email, contraseña)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            if (user != null) {
                                Usuario usuario = new Usuario(etNombre.getText().toString().trim(), email);
                                FirebaseDatabase.getInstance().getReference("Usuarios").child(user.getUid()).setValue(usuario);

                                user.updateProfile(new UserProfileChangeRequest.Builder()
                                        .setDisplayName(etNombre.getText().toString().trim())
                                        .build()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @RequiresApi(api = Build.VERSION_CODES.M)
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    /*Set<String> categoriasKey = new ArraySet<>();
                                                    Set<String> categoriasNombre = new ArraySet<>();
                                                    categoriasKey.addAll(usuario.suscripciones.values());
                                                    categoriasNombre.addAll(usuario.suscripciones.values());*/

                                                    /*usuario.categorias = Categoria.convertirCategoria(usuario.suscripciones.keySet().toArray(new String[0]),usuario.suscripciones.values().toArray(new String[0]));
                                                    for(Categoria categoria: usuario.categorias){
                                                        categoria.guardarCategoriaLocal(context);
                                                    }*/

                                                    SharedPreferences sharedPreferences = context.getSharedPreferences("usuario", Context.MODE_PRIVATE);
                                                    SharedPreferences.Editor editor = sharedPreferences.edit();
                                                    editor.putString(Usuario.UID, user.getUid());
                                                    editor.putString(Usuario.NOMBRE, user.getDisplayName());
                                                    editor.putString(Usuario.EMAIL, user.getEmail());
                                                    //editor.putString("fotoPerfil",user.getPhotoUrl());
                                                    editor.putString(Usuario.NUMERO_TELEFONO, user.getPhoneNumber());
                                                    editor.putBoolean(Usuario.EMAIL_VERIFIED, user.isEmailVerified());
                                                    //editor.putStringSet(Usuario.CATEGORIAS_KEY, categoriasKey);
                                                    //editor.putStringSet(Usuario.CATEGORIAS_NOMBRE, categoriasNombre);
                                                    editor.apply();

                                                    //startActivity(new Intent(context,ActivityNavigation.class));
                                                    //finish();
                                                }
                                            }
                                        });
                                //Fcm.guardarToken(user,context);
                            }
                        } else {
                            Toast.makeText(context, Objects.requireNonNull(task.getException()).getMessage(),Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }


}