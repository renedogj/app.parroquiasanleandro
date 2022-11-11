package es.parroquiasanleandro.activitys;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

import es.parroquiasanleandro.R;
import es.parroquiasanleandro.Usuario;

public class ActivityInicarSesion extends AppCompatActivity {
    private static final int RC_SIGN_IN = 9001;

    private final Context context = ActivityInicarSesion.this;
    private static final int INPUTTYPE_TEXT = 0x00000091;
    private static final int INPUTTYPE_PWD = 0x00000081;

    private Button bttnIniciarSesionGoogle;
    private EditText etCorreoElectronico;
    private EditText etContraseña;
    private ImageButton imgButtonShowPassword;
    private Button bttnIniciarSesion;
    private LinearLayout linearLayoutRegistrarse;

    private ActionBar actionBar;

    private FirebaseAuth mAuth;
    //private GoogleSignInClient mGoogleSignInClient;

    //ActivityResultLauncher<Intent> activityResultLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_iniciar_sesion);

        bttnIniciarSesionGoogle = findViewById(R.id.bttnIniciarSesionGoogle);
        etCorreoElectronico = findViewById(R.id.etCorreoElectronico);
        etContraseña = findViewById(R.id.etContraseña);
        imgButtonShowPassword = findViewById(R.id.imgButtonShowPassword);
        bttnIniciarSesion = findViewById(R.id.bttnIniciarSesion);
        linearLayoutRegistrarse = findViewById(R.id.linearLayoutRegistrarse);

        actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("Iniciar sesión");
        }

        imgButtonShowPassword.setOnClickListener(view -> {
            changeShowPassword(etContraseña,imgButtonShowPassword);
            /*if(etContraseña.getInputType() == INPUTTYPE_PWD){
                etContraseña.setInputType(INPUTTYPE_TEXT);
                imgButtonShowPassword.setImageResource(R.drawable.eye_24);
            }else{
                etContraseña.setInputType(INPUTTYPE_PWD);
                imgButtonShowPassword.setImageResource(R.drawable.eye_crossed_24);
            }*/
        });

        // Configure Google Sign In
        /*GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                //.requestIdToken("578961151024-ro7gnluudfbcngsn1apa4isopdiobktm.apps.googleusercontent.com")
                .requestIdToken("578961151024-ahthr4btnt2ek450vgat0bue80r2860o.apps.googleusercontent.com")
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);*/

        /*activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                //if (requestCode == RC_SIGN_IN)
                Log.e("FIREBASE","ASDFPBA");

                Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(result.getData());
                Log.e("FIREBASE",task.isSuccessful()+"");
                try {
                    GoogleSignInAccount account = task.getResult(ApiException.class);
                    firebaseAuthWithGoogle(account.getIdToken());
                } catch (ApiException e) {
                    Toast.makeText(context, "ERROR:" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e("ERROR FIREBASE",e.toString());
                    Log.e("ERROR FIREBASE",e.getMessage());
                }
                //}
            }
        });*/

        bttnIniciarSesionGoogle.setOnClickListener(v -> {
            //Intent signInIntent = mGoogleSignInClient.getSignInIntent();
            //startActivityForResult(signInIntent, RC_SIGN_IN);//El startActivityForResult no causa el error
            //activityResultLauncher.launch(signInIntent);
            Toast.makeText(context,"Inicio de sesion con google desactivado",Toast.LENGTH_SHORT).show();
        });

        mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() != null) {
            startActivity(new Intent(context, ActivityNavigation.class));
            finish();
        }

        bttnIniciarSesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iniciarSesion();
            }
        });

        linearLayoutRegistrarse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(context, ActivityRegistro.class));
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            Log.e("FIREBASE",task.isSuccessful()+"");
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                //firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                Toast.makeText(context, "ERROR:" + e.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("ERROR FIREBASE",e.toString());
                Log.e("ERROR FIREBASE",e.getMessage());
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
        if(!email.equals("") && !password.equals("")){
            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, task -> {
                if (task.isSuccessful()) {
                    startActivity(new Intent(context, ActivityNavigation.class));
                    finish();
                } else {
                    Toast.makeText(context, "Correo o contraseña incorrecta", Toast.LENGTH_SHORT).show();
                }
            });
        }else{
            Toast.makeText(context, "Es necesario completar todos los campos", Toast.LENGTH_SHORT).show();
        }
    }

    public static void changeShowPassword(EditText etContraseña, ImageButton imgButtonShowPassword){
        if(etContraseña.getInputType() == INPUTTYPE_PWD){
            etContraseña.setInputType(INPUTTYPE_TEXT);
            imgButtonShowPassword.setImageResource(R.drawable.eye_24);
        }else{
            etContraseña.setInputType(INPUTTYPE_PWD);
            imgButtonShowPassword.setImageResource(R.drawable.eye_crossed_24);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(context, ActivityNavigation.class));
    }
}