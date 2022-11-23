package es.parroquiasanleandro.activitys;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import es.parroquiasanleandro.R;

public class ActivityAviso extends AppCompatActivity {

    Context context = ActivityAviso.this;

    private ImageView ivImagenAviso;
    private LinearLayout linearLayoutContenedorAviso;
    private TextView tvTituloAviso;
    private TextView tvFechaInicio;
    private TextView tvFechaFinal;
    private TextView tvDescripcion;

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

        String avisoKey = getIntent().getStringExtra("avisoKey");
        String avisoCategoria = getIntent().getStringExtra("avisoCategoria");

        Log.d("TAG", avisoCategoria + " " + avisoKey);

        /*FirebaseDatabase.getInstance().getReference().child("Avisos").child(avisoCategoria).child(avisoKey).get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
            @Override
            public void onSuccess(DataSnapshot dataSnapshot) {
                Aviso aviso = dataSnapshot.getValue(Aviso.class);
                if(aviso != null){
                    aviso.asignarImagen(context,ivImagenAviso);

                    /*Fragment fragment = new FragmentAviso(aviso);
                    getSupportFragmentManager()
                            .beginTransaction()
                            .add(R.id.fragment_aviso,fragment).addToBackStack(null)
                            .commit();*/

                    /*tvTituloAviso.setText(aviso.titulo);
                    //tvFechaInicio.setText(aviso.fechaInicio.toString(Fecha.FormatosFecha.EE_d_MMM_aaaa) + "  " + aviso.fechaInicio.toString(Fecha.FormatosFecha.HH_mm));
                    tvFechaInicio.setText(aviso.getFechaInicio().toString(Fecha.FormatosFecha.dd_MM_aaaa) + "  " + aviso.getFechaInicio().toString(Fecha.FormatosFecha.HH_mm));
                    if (aviso.getFechaFin() != null){
                        //tvFechaFinal.setText(aviso.fechaFin.toString(Fecha.FormatosFecha.EE_d_MMM_aaaa) + "  " + aviso.fechaFin.toString(Fecha.FormatosFecha.HH_mm));
                        tvFechaFinal.setText(aviso.getFechaFin().toString(Fecha.FormatosFecha.dd_MM_aaaa) + "  " + aviso.getFechaFin().toString(Fecha.FormatosFecha.HH_mm));
                    }
                    tvDescripcion.setText(aviso.descripcion);
                    aviso.asignarColor(context,linearLayoutContenedorAviso);
                }
            }
        });*/
    }
}