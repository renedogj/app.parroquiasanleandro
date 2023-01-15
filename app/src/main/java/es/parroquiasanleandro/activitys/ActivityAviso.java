package es.parroquiasanleandro.activitys;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import es.parroquiasanleandro.Aviso;
import es.parroquiasanleandro.R;
import es.parroquiasanleandro.Url;
import es.renedogj.fecha.Fecha;

public class ActivityAviso extends AppCompatActivity {
    Context context = ActivityAviso.this;

    private ImageView ivImagenAviso;
    private LinearLayout linearLayoutContenedorAviso;
    private LinearLayout linearLayoutAviso;
    private TextView tvTituloAviso;
    private TextView tvFechaInicio;
    private TextView tvFechaFinal;
    private TextView tvDescripcion;
    private Button bttnUrl;
    private Button bttnArchivos;

    private Aviso aviso;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aviso);

        ivImagenAviso = findViewById(R.id.ivImagenAviso);
        linearLayoutContenedorAviso = findViewById(R.id.linearLayoutContenedorAviso);
        linearLayoutAviso = findViewById(R.id.linearLayoutAviso);
        tvTituloAviso = findViewById(R.id.tvTituloAviso);
        tvFechaInicio = findViewById(R.id.tvFechaInicio);
        tvFechaFinal = findViewById(R.id.tvFechaFinal);
        tvDescripcion = findViewById(R.id.tvDescripcion);

        bttnUrl = findViewById(R.id.bttnUrl);
        bttnArchivos = findViewById(R.id.bttnArchivos);

        String idAviso = getIntent().getStringExtra("idAviso");

        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(new StringRequest(Request.Method.POST, Url.obtenerAviso, result -> {
            Log.e("RESULT", result);
            try {
                JSONObject jsonResult = new JSONObject(result);
                if (!jsonResult.getBoolean("error")) {
                    JSONObject jsonAviso = jsonResult.getJSONObject("aviso");
                    aviso = Aviso.JSONObjectToAviso(jsonAviso);
                    aviso.asignarImagen(context, ivImagenAviso);
                    tvTituloAviso.setText(aviso.titulo);
                    tvFechaInicio.setText(aviso.getFechaInicio().toString(Fecha.FormatosFecha.dd_MM_aaaa) + "  " + aviso.getFechaInicio().toString(Fecha.FormatosFecha.HH_mm));
                    if (aviso.getFechaFin() != null) {
                        tvFechaFinal.setText(aviso.getFechaFin().toString(Fecha.FormatosFecha.dd_MM_aaaa) + "  " + aviso.getFechaFin().toString(Fecha.FormatosFecha.HH_mm));
                    }
                    tvDescripcion.setText(aviso.descripcion);

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
                    Toast.makeText(context, jsonResult.getString("El aviso solicitado no existe"), Toast.LENGTH_SHORT).show();
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
                parametros.put("idAviso", idAviso);
                return parametros;
            }
        });


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
}