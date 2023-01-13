package es.parroquiasanleandro.activitys;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import es.parroquiasanleandro.R;
import es.parroquiasanleandro.Url;
import es.parroquiasanleandro.adaptadores.ImagenServidorAdaptador;

public class ActivitySeleccionarImagen extends AppCompatActivity {
    final Context context = ActivitySeleccionarImagen.this;
    final Activity activity = ActivitySeleccionarImagen.this;

    public static final int SELECION_IMAGEN_GALERIA = 1;
    public static final int SELECION_IMAGEN_SERVIDOR = 2;

    private RecyclerView recyclerView;
    private FloatingActionButton bttnSelecionarImagenGaleria;

    List<String> imagenes = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seleccionar_imagen);

        recyclerView = findViewById(R.id.recyclerView);
        bttnSelecionarImagenGaleria = findViewById(R.id.bttnSelecionarImagenGaleria);

        String idGrupo = getIntent().getStringExtra("idGrupo");

        GridLayoutManager gridLayoutManager = new GridLayoutManager(context, 2);
        recyclerView.setLayoutManager(gridLayoutManager);

        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(new StringRequest(Request.Method.POST, Url.obtenerImagenesGrupo, result -> {
            try {
                JSONArray jsonResult = new JSONArray(result);
                for (int i = 0; i < jsonResult.length(); i++) {
                    imagenes.add(jsonResult.getString(i));
                }
                ImagenServidorAdaptador imagenesAvisosAdaptador = new ImagenServidorAdaptador(context, activity, idGrupo, imagenes);
                recyclerView.setAdapter(imagenesAvisosAdaptador);
            } catch (JSONException e) {
                Toast.makeText(context, "Se ha producido un error al recuperar las imagenes del servidor", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        }, error -> {
            Toast.makeText(context, "Se ha producido un error al conectar con el servidor", Toast.LENGTH_SHORT).show();
            error.printStackTrace();
        }) {
            protected Map<String, String> getParams() {
                Map<String, String> parametros = new HashMap<>();
                parametros.put("idGrupo", idGrupo);
                return parametros;
            }
        });

        bttnSelecionarImagenGaleria.setOnClickListener(v -> selecionarImagenGaleria());
    }

    public void selecionarImagenGaleria() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, SELECION_IMAGEN_GALERIA, null);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SELECION_IMAGEN_GALERIA && resultCode == RESULT_OK && data != null && data.getData() != null) {
            setResult(SELECION_IMAGEN_GALERIA, data);
            finish();
        }
    }
}