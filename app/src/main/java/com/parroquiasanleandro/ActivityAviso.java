package com.parroquiasanleandro;

import android.content.Context;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;
import com.parroquiasanleandro.fecha.Fecha;

public class ActivityAviso extends AppCompatActivity {

    Context context = ActivityAviso.this;

    private ImageView ivImagenAviso;
    //private FragmentContainerView fragment_aviso;

    private TextView tvTituloAviso;
    private TextView tvFechaInicio;
    private TextView tvFechaFinal;
    private TextView tvDescripcion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aviso);

        ivImagenAviso = findViewById(R.id.ivImagenAviso);
        //fragment_aviso = findViewById(R.id.fragment_aviso);
        tvTituloAviso = findViewById(R.id.tvTituloAviso);
        tvFechaInicio = findViewById(R.id.tvFechaInicio);
        tvFechaFinal = findViewById(R.id.tvFechaFinal);
        tvDescripcion = findViewById(R.id.tvDescripcion);

        /*
        View decorView = getWindow().getDecorView();
        // Hide the status bar.
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
        // Remember that you should never show the action bar if the
        // status bar is hidden, so hide that too if necessary.
        ActionBar actionBar = getActionBar();
        actionBar.setCustomView(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        //actionBar.hide();
        */


        /*getSupportFragmentManager().beginTransaction()
                .setReorderingAllowed(true)
                .add(R.id.fragment_aviso, FragmentAviso.class, null)
                .commit();*/

        String keyPlato = getIntent().getStringExtra("avisoKey");
        FirebaseDatabase.getInstance().getReference().child("Avisos").child(keyPlato).get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
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

                    tvTituloAviso.setText(aviso.titulo);
                    //tvFechaInicio.setText(aviso.fechaInicio.toString(Fecha.FormatosFecha.EE_d_MMM_aaaa) + "  " + aviso.fechaInicio.toString(Fecha.FormatosFecha.HH_mm));
                    tvFechaInicio.setText(aviso.fechaInicio.toString(Fecha.FormatosFecha.dd_MM_aaaa) + "  " + aviso.fechaInicio.toString(Fecha.FormatosFecha.HH_mm));
                    if (aviso.fechaFin != null){
                        //tvFechaFinal.setText(aviso.fechaFin.toString(Fecha.FormatosFecha.EE_d_MMM_aaaa) + "  " + aviso.fechaFin.toString(Fecha.FormatosFecha.HH_mm));
                        tvFechaFinal.setText(aviso.fechaFin.toString(Fecha.FormatosFecha.dd_MM_aaaa) + "  " + aviso.fechaFin.toString(Fecha.FormatosFecha.HH_mm));
                    }
                    tvDescripcion.setText(aviso.descripcion);
                }
            }
        });
    }
}