package es.parroquiasanleandro.activitys;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import es.parroquiasanleandro.R;

public class ActivityArticulo extends AppCompatActivity {

    private RecyclerView rvImagenesArticulo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_articulo);

        rvImagenesArticulo = findViewById(R.id.rvImagenesArticulo);

        String idArticulo = getIntent().getStringExtra("idArticulo");
    }
}