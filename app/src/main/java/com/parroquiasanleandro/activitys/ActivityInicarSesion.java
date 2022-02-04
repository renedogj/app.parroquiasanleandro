package com.parroquiasanleandro.activitys;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.FirebaseDatabase;
import com.parroquiasanleandro.R;
import com.parroquiasanleandro.Usuario;

import java.util.Objects;

public class ActivityInicarSesion extends AppCompatActivity {
    private static final int RC_SIGN_IN = 9001;

    private final Context context = ActivityInicarSesion.this;

    private Button bttnIniciarSesionGoogle;
    private EditText etCorreoElectronico;
    private EditText etContraseña;
    private Button bttnIniciarSesion;
    private TextView tvRegistrarse;

    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bttnIniciarSesionGoogle = findViewById(R.id.bttnIniciarSesionGoogle);
        etCorreoElectronico = findViewById(R.id.etCorreoElectronico);
        etContraseña = findViewById(R.id.etContraseña);
        bttnIniciarSesion = findViewById(R.id.bttnIniciarSesion);
        tvRegistrarse = findViewById(R.id.tvRegistrarse);

        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("578961151024-ro7gnluudfbcngsn1apa4isopdiobktm.apps.googleusercontent.com")
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        bttnIniciarSesionGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, RC_SIGN_IN);
            }
        });

        mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() != null) {
            startActivity(new Intent(context, ActivityNavigation.class));
        }

        bttnIniciarSesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iniciarSesion();
            }
        });

        tvRegistrarse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(context, ActivityNavigation.class));
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("ERROR", e.getMessage());
                Log.e("ERROR", e.toString());
                Log.e("ERROR", e.getStatus().toString());
            }
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential).addOnCompleteListener(this, task -> {
            if (task.isSuccessful()) {
                FirebaseUser user = mAuth.getCurrentUser();
                if (user != null) {
                    FirebaseDatabase.getInstance().getReference("Usuarios").child(user.getUid()).get().addOnCompleteListener(task1 -> {
                        if (task1.isSuccessful()) {
                            Log.d("TASK SUCCESSFUL", task1.getResult().toString());
                            if (task1.getResult().getValue() == null) {
                                Log.d("TASK NULL", task1.getResult().toString());
                                Usuario usuarioActual = new Usuario(user.getDisplayName(), user.getEmail(), user.getPhoneNumber());
                                FirebaseDatabase.getInstance().getReference("Usuarios").child(user.getUid()).setValue(usuarioActual);
                            } else {
                                Log.d("TASK NOT NULL", task1.getResult().toString());
                            }
                        } else {
                            Log.d("TASK UNSUCCESSFUL", task1.getResult().toString());
                        }
                    });

                    startActivity(new Intent(context, ActivityNavigation.class));
                    finish();
                }
            } else {
                Toast.makeText(context, Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void iniciarSesion() {
        String email = etCorreoElectronico.getText().toString().trim();
        String password = etContraseña.getText().toString().trim();
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, task -> {
            if (task.isSuccessful()) {
                startActivity(new Intent(context, ActivityNavigation.class));
                finish();
            } else {
                Toast.makeText(context, "Correo o contraseña incorrecta", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(context, ActivityNavigation.class));
    }
}