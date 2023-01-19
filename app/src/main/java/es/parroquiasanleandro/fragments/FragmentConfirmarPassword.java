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

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import es.parroquiasanleandro.R;
import es.parroquiasanleandro.Url;
import es.parroquiasanleandro.Usuario;
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
        imgButtonShowPassword = view.findViewById(R.id.imgBtnShowPassword);
        bttnConfirmarContraseña = view.findViewById(R.id.bttnConfirmarContraseña);

        Usuario usuario = Usuario.recuperarUsuarioLocal(context);

        imgButtonShowPassword.setOnClickListener(view1 -> {
            ActivityInicarSesion.changeShowPassword(etConfirmacionPassword, imgButtonShowPassword);
        });

        bttnConfirmarContraseña.setOnClickListener(view1 -> {
            String password = etConfirmacionPassword.getText().toString().trim();
            if (!password.equals("")) {
                Volley.newRequestQueue(context).add(new StringRequest(Request.Method.POST, Url.iniciarSesion, result -> {
                    try {
                        JSONObject jsonResult = new JSONObject(result);
                        if (!jsonResult.getBoolean("error")) {
                            FragmentManager fragmentManager = getParentFragmentManager();
                            fragmentManager.beginTransaction().remove(FragmentConfirmarPassword.this).commit();
                        } else {
                            Toast.makeText(context, "Correo o contraseña incorrecta", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        Toast.makeText(context, "Se ha producido un error en el servidor al confirmar la contraseña", Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                }, error -> {
                    Toast.makeText(context, "Se ha producido un error al conectar con el servidor", Toast.LENGTH_SHORT).show();
                    error.printStackTrace();
                }) {
                    @Override
                    protected Map<String, String> getParams() {
                        Map<String, String> parametros = new HashMap<>();
                        parametros.put("email", usuario.email);
                        parametros.put("password", password);
                        return parametros;
                    }
                });
            } else {
                Toast.makeText(context, "Introduce tu contraseña", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }
}