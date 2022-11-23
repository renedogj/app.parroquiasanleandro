package es.parroquiasanleandro.activitys;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import es.parroquiasanleandro.R;

public class ActivitySeleccionarImagen extends AppCompatActivity {

    private static final int SELECION_IMAGEN_GALERIA = 1;

    Context context = ActivitySeleccionarImagen.this;

    private RecyclerView recyclerView;
    private FloatingActionButton bttnSelecionarImagenGaleria;

    //List<StorageReference> imagenes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seleccionar_imagen);

        recyclerView = findViewById(R.id.recyclerView);
        bttnSelecionarImagenGaleria = findViewById(R.id.bttnSelecionarImagenGaleria);

        String categoria = getIntent().getStringExtra("categoria");

        GridLayoutManager gridLayoutManager = new GridLayoutManager(context, 2);
        recyclerView.setLayoutManager(gridLayoutManager);

        //imagenes = new ArrayList<>();

        /*FirebaseStorage.getInstance().getReference().child("ImagenesAvisos").child(categoria).listAll()
                .addOnSuccessListener(listResult -> {
                    imagenes.addAll(listResult.getItems());
                    ImagenesAvisoAdaptador imagenesAvisoAdaptador = new ImagenesAvisoAdaptador(context, (Activity) context,imagenes);
                    recyclerView.setAdapter(imagenesAvisoAdaptador);
                })
                .addOnFailureListener(e -> Log.e("STORAGE ERROR", e.getMessage()));*/

        bttnSelecionarImagenGaleria.setOnClickListener(v -> selecionarImagenGaleria());
    }

    public void selecionarImagenGaleria() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,SELECION_IMAGEN_GALERIA,null);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            setResult(SELECION_IMAGEN_GALERIA,data);
            finish();
        }
    }
}