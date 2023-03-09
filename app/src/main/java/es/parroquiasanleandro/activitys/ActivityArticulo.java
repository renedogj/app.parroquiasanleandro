package es.parroquiasanleandro.activitys;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
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
import es.parroquiasanleandro.adaptadores.CategoriaAdaptador;
import es.parroquiasanleandro.adaptadores.ImagenMercadilloAdaptador;

public class ActivityArticulo extends AppCompatActivity {
    Context context = ActivityArticulo.this;

    private RecyclerView rvImagenesArticulo;
    private TextView tvNombreArticulo;
    private TextView tvId;
    private TextView tvPrecio;
    private TextView tvDescripcion;
    private RecyclerView rvCategoriasArticulo;
    private LinearLayout irAlMercadilloWeb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_articulo);

        rvImagenesArticulo = findViewById(R.id.rvImagenesArticulo);
        tvNombreArticulo = findViewById(R.id.tvNombreArticulo);
        tvId = findViewById(R.id.tvId);
        tvPrecio = findViewById(R.id.tvPrecio);
        tvDescripcion = findViewById(R.id.tvDescripcion);
        rvCategoriasArticulo = findViewById(R.id.rvCategoriasArticulo);
        irAlMercadilloWeb = findViewById(R.id.irAlMercadilloWeb);

        String idArticulo = getIntent().getStringExtra("idArticulo");

        LinearLayoutManager lnlytManageImagenes = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
        /*rvImagenesArticulo.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                /*CardView cardImagenArticulo = view.findViewById(R.id.cardImagenArticulo);

                int rvImagenWidth = rvImagenesArticulo.getLayoutParams().width;
                //cardImagenArticulo.getLayoutParams().width = (rvImagenWidth * 60) / 100;
                //cardImagenArticulo.requestLayout();
                int cardWidth = (rvImagenWidth * 60) / 100;
                int cardMargin = ((rvImagenWidth * 5) / 100) / 2;*/

                /*ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(
                        cardWidth,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                );*/
                //params.setMargins(cardMargin, 6, cardMargin, 0);
                //cardImagenArticulo.setLayoutParams(params);

                /*ViewGroup.MarginLayoutParams layoutParams =
                        (ViewGroup.MarginLayoutParams) cardImagenArticulo.getLayoutParams();
                layoutParams.width = -(rvImagenWidth * 900);
                layoutParams.setMargins(cardMargin, 6, cardMargin, 0);
                cardImagenArticulo.requestLayout();*/

                //cardImagenArticulo = (rvImagenWidth * 60) / 100;
                /*if (parent.getChildLayoutPosition(view) % 2 != 0) {
                    outRect.top = 50;
                    outRect.bottom = -50;
                } else {
                    outRect.top = 0;
                }*/
        //    }
        //});
        rvImagenesArticulo.setLayoutManager(lnlytManageImagenes);

        LinearLayoutManager lnlytManageCategorias = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
        rvCategoriasArticulo.setLayoutManager(lnlytManageCategorias);

        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(new StringRequest(Request.Method.POST, Url.obtenerArticulo, result -> {
            Log.e("RESULT",result);
            try {
                JSONObject jsonArrayArticulo = new JSONObject(result);
                Articulo articulo = Articulo.JSONToArticulo(jsonArrayArticulo);
                if(!articulo.mostrar) {
                    finish();
                }

                ImagenMercadilloAdaptador imagenMercadilloAdaptador = new ImagenMercadilloAdaptador(context, articulo.id, articulo.imagenes, false);
                rvImagenesArticulo.setAdapter(imagenMercadilloAdaptador);

                //if(rvImagenesArticulo.getChildCount() > 2){
                    //rvImagenesArticulo.scrollTo();
                    //int height = rvImagenesArticulo.getLayoutParams().height;
                //}

                tvNombreArticulo.setText(articulo.nombre);
                tvId.setText(articulo.id);
                tvPrecio.setText(articulo.precio + " €");
                tvDescripcion.setText(articulo.descripcion);

                CategoriaAdaptador categoriaAdaptador = new CategoriaAdaptador(context, articulo.categorias);
                rvCategoriasArticulo.setAdapter(categoriaAdaptador);

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

        irAlMercadilloWeb.setOnClickListener(v -> {
            Uri uri = Uri.parse("https://www.mercadillo.parroquiasanleandro.es/");
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
        });
    }
}