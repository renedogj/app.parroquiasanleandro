package es.parroquiasanleandro.activitys;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import es.parroquiasanleandro.Aviso;
import es.parroquiasanleandro.R;
import es.parroquiasanleandro.Url;
import es.parroquiasanleandro.Usuario;
import es.renedogj.fecha.Fecha;

public class ActivityAviso extends AppCompatActivity {
    Context context = ActivityAviso.this;

    private ImageView ivImagenAviso;
    private FloatingActionButton btnEditar;
    private LinearLayout linearLayoutContenedorAviso;
    private LinearLayout linearLayoutAviso;
    private TextView tvTituloAviso;
    private TextView tvFecha;
    private LinearLayout lnlytFechaInicio;
    private TextView tvFechaInicio;
    private LinearLayout lnlytFechaFin;
    private TextView tvFechaFinal;
    private TextView tvDescripcion;
    private Button bttnUrl;
    private Button bttnArchivos;

    private Aviso aviso = new Aviso();
    private Usuario usuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aviso);

        ivImagenAviso = findViewById(R.id.ivImagenAviso);
        btnEditar = findViewById(R.id.btnEditar);
        linearLayoutContenedorAviso = findViewById(R.id.linearLayoutContenedorAviso);
        linearLayoutAviso = findViewById(R.id.linearLayoutAviso);
        tvTituloAviso = findViewById(R.id.tvTituloAviso);
        tvFecha = findViewById(R.id.tvFecha);
        lnlytFechaInicio = findViewById(R.id.lnlytFechaInicio);
        tvFechaInicio = findViewById(R.id.tvFechaInicio);
        lnlytFechaFin = findViewById(R.id.lnlytFechaFin);
        tvFechaFinal = findViewById(R.id.tvFechaFinal);
        tvDescripcion = findViewById(R.id.tvDescripcion);

        bttnUrl = findViewById(R.id.bttnUrl);
        bttnArchivos = findViewById(R.id.bttnArchivos);

        usuario = Usuario.recuperarUsuarioLocal(context);

        aviso.id = getIntent().getStringExtra("idAviso");
        aviso.idGrupo = getIntent().getStringExtra("idGrupo");

        if (usuario.getId() != null) {
            if (usuario.esAdministrador) {
                if (usuario.esAdminGrupo(aviso.idGrupo)) {
                    btnEditar.setVisibility(View.VISIBLE);
                    btnEditar.setOnClickListener(v -> {
                        Intent intent = new Intent(context, ActivityNuevoAviso.class);
                        intent.putExtra("idAviso", aviso.id);
                        startActivity(intent);
                        finish();
                    });
                }
            }
        }

        bttnUrl.setOnClickListener(v -> {
            Intent intent = new Intent(context, ActivityWebView.class);
            intent.putExtra("url", aviso.url);
            startActivity(intent);
        });

        bttnArchivos.setOnClickListener(v -> {
            Uri uriArchivo = Uri.parse(aviso.archivo);
            if (uriArchivo.getLastPathSegment().endsWith(".pdf")) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(uriArchivo, "application/pdf");
                startActivity(intent);
            }

            if (uriArchivo.getLastPathSegment().endsWith(".docx") || uriArchivo.getLastPathSegment().endsWith(".doc")) {
                Intent intent = new Intent(Intent.ACTION_QUICK_VIEW);
                intent.setData(uriArchivo);
                try {
                    startActivity(intent);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(new StringRequest(Request.Method.POST, Url.obtenerAviso, result -> {
            try {
                JSONObject jsonResult = new JSONObject(result);
                if (!jsonResult.getBoolean("error")) {
                    JSONObject jsonAviso = jsonResult.getJSONObject("aviso");
                    aviso = Aviso.JSONObjectToAviso(jsonAviso);
                    aviso.asignarImagen(context, ivImagenAviso);
                    tvTituloAviso.setText(aviso.titulo);
                    tvDescripcion.setText(aviso.descripcion);

                    asignarFechas();

                    int colorGrupo = aviso.obtenerColor(context);
                    linearLayoutContenedorAviso.setBackgroundColor(colorGrupo);
                    ivImagenAviso.setBackgroundColor(colorGrupo);
                    getWindow().setNavigationBarColor(colorGrupo);

                    if (aviso.url != null && !aviso.url.equals("")) {
                        bttnUrl.setVisibility(View.VISIBLE);
                    }

                    if (aviso.archivo != null && !aviso.archivo.equals("")) {
                        bttnArchivos.setVisibility(View.VISIBLE);
                    }
                } else {
                    Toast.makeText(context, "El aviso solicitado no existe", Toast.LENGTH_SHORT).show();
                    onBackPressed();
                }
            } catch (JSONException e) {
                Toast.makeText(context, "Se ha producido un error en el servidor al recuperar el aviso", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
                onBackPressed();
            }
        }, error -> {
            Toast.makeText(context, "Se ha producido un error al conectar con el servidor", Toast.LENGTH_SHORT).show();
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> parametros = new HashMap<>();
                parametros.put("idAviso", aviso.id);
                return parametros;
            }
        });
    }

    @SuppressLint("SetTextI18n")
    public void asignarFechas() {
        if (aviso.getFechaFin() != null) {
            if (aviso.getFechaInicio().esIgualA(aviso.getFechaFin())) {
                tvFecha.setText(aviso.getFechaInicio().toString(Fecha.FormatosFecha.EE_d_MMMM) + "  " + aviso.getFechaInicio().toString(Fecha.FormatosFecha.HH_mm));
            } else {
                if (Fecha.isFechasDelMismoDia(aviso.getFechaInicio(), aviso.getFechaFin())) {
                    tvFecha.setText(aviso.getFechaInicio().toString(Fecha.FormatosFecha.EE_d_MMMM)
                            + "  " + aviso.getFechaInicio().toString(Fecha.FormatosFecha.HH_mm)
                            + " - " + aviso.getFechaFin().toString(Fecha.FormatosFecha.HH_mm));
                } else {
                    tvFechaInicio.setText(aviso.getFechaInicio().toString(Fecha.FormatosFecha.EE_d_MMMM) + "  " + aviso.getFechaInicio().toString(Fecha.FormatosFecha.HH_mm));
                    tvFechaFinal.setText(aviso.getFechaFin().toString(Fecha.FormatosFecha.EE_d_MMMM) + "  " + aviso.getFechaFin().toString(Fecha.FormatosFecha.HH_mm));
                    lnlytFechaInicio.setVisibility(View.VISIBLE);
                    lnlytFechaFin.setVisibility(View.VISIBLE);
                    tvFecha.setVisibility(View.GONE);
                }
            }
        } else {
            tvFecha.setText(aviso.getFechaInicio().toString(Fecha.FormatosFecha.EE_d_MMMM) + "  " + aviso.getFechaInicio().toString(Fecha.FormatosFecha.HH_mm));
        }
    }
}