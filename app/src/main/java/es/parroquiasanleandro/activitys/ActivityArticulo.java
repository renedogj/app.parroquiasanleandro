package es.parroquiasanleandro.activitys;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import es.parroquiasanleandro.Articulo;
import es.parroquiasanleandro.R;
import es.parroquiasanleandro.Url;

public class ActivityArticulo extends AppCompatActivity {
    Context context = ActivityArticulo.this;

    private RecyclerView rvImagenesArticulo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_articulo);

        rvImagenesArticulo = findViewById(R.id.rvImagenesArticulo);

        String idArticulo = getIntent().getStringExtra("idArticulo");

        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(new StringRequest(Request.Method.POST, Url.obtenerArticulo, result -> {
            Log.e("RESULT",result);
            try {
                JSONObject jsonArrayArticulo = new JSONObject(result);
                Articulo articulo = Articulo.JSONToArticulo(jsonArrayArticulo);
                if(!articulo.mostrar) {
                    finish();
                }
            } catch (JSONException e) {
                Toast.makeText(context, "Se ha producido un error en el servidor al obtener el articulo", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
                finish();
            }
        }, error -> {
            Toast.makeText(context, "Se ha producido un error al conectar con el servidor", Toast.LENGTH_SHORT).show();
            error.printStackTrace();
            finish();
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> parametros = new HashMap<>();
                parametros.put("idArticulo", idArticulo);
                return parametros;
            }
        });
    }
}