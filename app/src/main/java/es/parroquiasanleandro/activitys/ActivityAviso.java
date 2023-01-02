package es.parroquiasanleandro.activitys;

import android.content.Context;
import android.os.Bundle;
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
import es.parroquiasanleandro.fecha.Fecha;

public class ActivityAviso extends AppCompatActivity {
    Context context = ActivityAviso.this;

    private ImageView ivImagenAviso;
    private LinearLayout linearLayoutContenedorAviso;
    private TextView tvTituloAviso;
    private TextView tvFechaInicio;
    private TextView tvFechaFinal;
    private TextView tvDescripcion;

    private Aviso aviso;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aviso);

        ivImagenAviso = findViewById(R.id.ivImagenAviso);
        linearLayoutContenedorAviso = findViewById(R.id.linearLayoutContenedorAviso);
        tvTituloAviso = findViewById(R.id.tvTituloAviso);
        tvFechaInicio = findViewById(R.id.tvFechaInicio);
        tvFechaFinal = findViewById(R.id.tvFechaFinal);
        tvDescripcion = findViewById(R.id.tvDescripcion);

        String idAviso = getIntent().getStringExtra("idAviso");

        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(new StringRequest(Request.Method.POST, Url.obtenerAviso, result -> {
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
                    aviso.asignarColor(context, linearLayoutContenedorAviso);
                } else {
                    Toast.makeText(context, jsonResult.getString("errorMensaje"), Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                Toast.makeText(context, "Se ha producido un error en el servidor al recuperar el aviso", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        }, error -> {
            Toast.makeText(context, "Se ha producido un error al conectar con el servidor", Toast.LENGTH_SHORT).show();
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String,String> parametros = new HashMap<>();
                parametros.put("idAviso",idAviso);
                return parametros;
            }
        });
    }
}