package es.parroquiasanleandro.activitys;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import es.parroquiasanleandro.Aviso;
import es.parroquiasanleandro.Grupo;
import es.parroquiasanleandro.R;
import es.parroquiasanleandro.Url;
import es.parroquiasanleandro.Usuario;
import es.renedogj.fecha.Fecha;
import es.renedogj.fecha.Mes;

public class ActivityNuevoAviso extends AppCompatActivity {
    Context context = ActivityNuevoAviso.this;

    private EditText etTitulo;
    private ImageView ivImagenAviso;
    private ConstraintLayout cntrtlytAñadirImagen;
    private ImageButton btnEliminarImagen;
    private TextView tvAñadirImagen;
    private EditText etDescripcion;
    private TextView etUrl;
    private TextView tvFechaInicio;
    private TextView tvHoraInicio;
    private LinearLayout lnlytFechaFinal;
    private TextView tvFechaFinal;
    private TextView tvHoraFinal;
    private LinearLayout lnlytAñadirFechaFinal;
    private TextView tvSimboloAñadirFechaFinal;
    private TextView tvAñadirFechaFinal;
    private Spinner spinnerGrupo;

    private Button bttnNuevoAviso;
    private Button bttnCancelar;

    private String imagenStringByte = null;
    private String nombreImagen = null;

    private Fecha fechaInicio;
    private Fecha fechaFin;

    private Usuario usuario;

    private String idAviso;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nuevo_aviso);

        etTitulo = findViewById(R.id.etTitulo);
        ivImagenAviso = findViewById(R.id.ivImagenAviso);
        cntrtlytAñadirImagen = findViewById(R.id.cntrtlytAñadirImagen);
        btnEliminarImagen = findViewById(R.id.btnEliminarImagen);
        tvAñadirImagen = findViewById(R.id.tvAñadirImagen);
        etDescripcion = findViewById(R.id.etDescripcion);
        etUrl = findViewById(R.id.etUrl);
        tvFechaInicio = findViewById(R.id.tvFechaInicio);
        tvHoraInicio = findViewById(R.id.tvHoraInicio);
        lnlytFechaFinal = findViewById(R.id.lnlytFechaFinal);
        tvFechaFinal = findViewById(R.id.tvFechaFinal);
        tvHoraFinal = findViewById(R.id.tvHoraFinal);
        spinnerGrupo = findViewById(R.id.spinnerGrupo);
        lnlytAñadirFechaFinal = findViewById(R.id.lnlytAñadirFechaFinal);
        tvSimboloAñadirFechaFinal = findViewById(R.id.tvSimboloAñadirFechaFinal);
        tvAñadirFechaFinal = findViewById(R.id.tvAñadirFechaFinal);
        bttnNuevoAviso = findViewById(R.id.bttnNuevoAviso);
        bttnCancelar = findViewById(R.id.bttnCancelar);

        usuario = Usuario.recuperarUsuarioLocal(context);

        if (!usuario.esAdministrador) {
            finish();
        }

        idAviso = getIntent().getStringExtra("idAviso");

        String[] nombreGruposAdministrados;
        if (usuario.getGruposAdministrados() != null) {
            nombreGruposAdministrados = Grupo.getNombreGrupos(usuario.getGruposAdministrados());
        } else {
            nombreGruposAdministrados = new String[0];
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(context, R.layout.item_spinner_grupo, nombreGruposAdministrados);
        spinnerGrupo.setAdapter(adapter);

        fechaInicio = Fecha.FechaActual();
        fechaFin = Fecha.FechaActual();

        lnlytAñadirFechaFinal.setOnClickListener(v -> {
            if (lnlytFechaFinal.getVisibility() == View.GONE) {
                lnlytFechaFinal.setVisibility(View.VISIBLE);
                tvSimboloAñadirFechaFinal.setText("-");
                tvAñadirFechaFinal.setText("Quitar fecha de fin");
                actualizarFechaFin();
            } else if (lnlytFechaFinal.getVisibility() == View.VISIBLE) {
                lnlytFechaFinal.setVisibility(View.GONE);
                tvSimboloAñadirFechaFinal.setText("+");
                tvAñadirFechaFinal.setText("Añade fecha de fin");
                fechaFin = fechaInicio;
            }
        });

        tvFechaInicio.setText(fechaInicio.toString(Fecha.FormatosFecha.EE_d_MMM_aaaa));
        tvHoraInicio.setText(fechaInicio.toString(Fecha.FormatosFecha.HH_mm));
        tvFechaFinal.setText(fechaFin.toString(Fecha.FormatosFecha.EE_d_MMM_aaaa));
        tvHoraFinal.setText(fechaFin.toString(Fecha.FormatosFecha.HH_mm));

        tvFechaInicio.setOnClickListener(v -> {
            DatePickerDialog datePickerDialog = new DatePickerDialog(context, (view, year, month, dayOfMonth) -> {
                fechaInicio.dia = dayOfMonth;
                fechaInicio.mes = Mes.values()[month];
                fechaInicio.año = year;
                tvFechaInicio.setText(fechaInicio.toString(Fecha.FormatosFecha.EE_d_MMM_aaaa));
                actualizarFechaFin();
            }, fechaInicio.año, fechaInicio.mes.getNumeroMes() - 1, fechaInicio.dia);
            datePickerDialog.show();
        });

        tvHoraInicio.setOnClickListener(v -> {
            TimePickerDialog timePickerDialog = new TimePickerDialog(context, (view, hourOfDay, minute) -> {
                fechaInicio.hora = hourOfDay;
                fechaInicio.minuto = minute;
                tvHoraInicio.setText(fechaInicio.toString(Fecha.FormatosFecha.HH_mm));
                actualizarFechaFin();
            }, fechaInicio.hora, fechaInicio.minuto, true);
            timePickerDialog.show();
        });

        tvFechaFinal.setOnClickListener(v -> {
            DatePickerDialog datePickerDialog = new DatePickerDialog(context, (view, year, month, dayOfMonth) -> {
                fechaFin.dia = dayOfMonth;
                fechaFin.mes = Mes.values()[month];
                fechaFin.año = year;
                tvFechaFinal.setText(fechaFin.toString(Fecha.FormatosFecha.EE_d_MMM_aaaa));
            }, fechaFin.año, fechaFin.mes.getNumeroMes() - 1, fechaFin.dia);
            datePickerDialog.show();
        });

        tvHoraFinal.setOnClickListener(v -> {
            TimePickerDialog timePickerDialog = new TimePickerDialog(context, (view, hourOfDay, minute) -> {
                fechaFin.hora = hourOfDay;
                fechaFin.minuto = minute;
                tvHoraFinal.setText(fechaFin.toString(Fecha.FormatosFecha.HH_mm));
                Log.d("FECHA FIN", fechaFin.toString(Fecha.FormatosFecha.dd_MM_aaaa_HH_mm));
            }, fechaFin.hora, fechaFin.minuto, true);
            timePickerDialog.show();
        });


        bttnNuevoAviso.setOnClickListener(v -> guardarAviso());

        bttnCancelar.setOnClickListener(v -> finish());

        cntrtlytAñadirImagen.setOnClickListener(v -> {
            Intent intent = new Intent(context, ActivitySeleccionarImagen.class);
            intent.putExtra("idGrupo", usuario.getGruposAdministrados()[spinnerGrupo.getSelectedItemPosition()].id);
            startActivityForResult(intent, 1);
        });

        btnEliminarImagen.setOnClickListener(v -> {
            imagenStringByte = null;
            nombreImagen = null;
            Glide.with(context).clear(ivImagenAviso);
            btnEliminarImagen.setVisibility(View.GONE);
        });

        if(idAviso != null){
            RequestQueue requestQueue = Volley.newRequestQueue(context);
            requestQueue.add(new StringRequest(Request.Method.POST, Url.obtenerAviso, result -> {
                Log.e("RESULT", result);
                try {
                    JSONObject jsonResult = new JSONObject(result);
                    if (!jsonResult.getBoolean("error")) {
                        JSONObject jsonAviso = jsonResult.getJSONObject("aviso");
                        Aviso aviso = Aviso.JSONObjectToAviso(jsonAviso);
                        etTitulo.setText(aviso.titulo);
                        etDescripcion.setText(aviso.descripcion);
                        aviso.asignarImagen(context,ivImagenAviso);
                        btnEliminarImagen.setVisibility(View.VISIBLE);
                        if (!aviso.nombreImagen.equals("null")){
                            nombreImagen = aviso.nombreImagen;
                        }
                        fechaInicio = aviso.getFechaInicio();
                        fechaFin = aviso.getFechaFin();
                        tvFechaInicio.setText(aviso.getFechaInicio().toString(Fecha.FormatosFecha.EE_d_MMM_aaaa));
                        tvHoraInicio.setText(aviso.getFechaInicio().toString(Fecha.FormatosFecha.HH_mm));
                        tvFechaFinal.setText(aviso.getFechaFin().toString(Fecha.FormatosFecha.EE_d_MMM_aaaa));
                        tvHoraFinal.setText(aviso.getFechaFin().toString(Fecha.FormatosFecha.HH_mm));
                        lnlytFechaFinal.setVisibility(View.VISIBLE);
                        tvSimboloAñadirFechaFinal.setText("-");
                        tvAñadirFechaFinal.setText("Quitar fecha de fin");
                        spinnerGrupo.setSelection(0);
                        if(aviso.url != null){
                            etUrl.setText(aviso.url);
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
        }
    }

    public void actualizarFechaFin(){
        fechaFin = fechaInicio.clone();
        if(lnlytFechaFinal.getVisibility() == View.VISIBLE){
            fechaFin.sumMinutos(60);
        }
        tvFechaFinal.setText(fechaFin.toString(Fecha.FormatosFecha.EE_d_MMM_aaaa));
        tvHoraFinal.setText(fechaFin.toString(Fecha.FormatosFecha.HH_mm));
    }

    private void guardarAviso() {
        Aviso aviso;
        String titulo = etTitulo.getText().toString().trim();
        String descripcion = etDescripcion.getText().toString().trim();
        String idGrupo = usuario.getGruposAdministrados()[spinnerGrupo.getSelectedItemPosition()].id;
        String url = etUrl.getText().toString().trim();
        url = (!url.equals("") ? url : null);

        if (fechaInicio.esIgualA(fechaFin)) {
            aviso = new Aviso(titulo, descripcion, idGrupo, fechaInicio, nombreImagen, url, usuario.getId());
        } else {
            aviso = new Aviso(titulo, descripcion, idGrupo, fechaInicio, fechaFin, nombreImagen, url, usuario.getId());
        }

        if (aviso.titulo.length() > 0) {
            if (aviso.descripcion.length() > 0) {
                if(aviso.getFechaInicio().esIgualA(aviso.getFechaFin()) || Fecha.isFecha1MayorQueFecha2(aviso.getFechaFin(), aviso.getFechaInicio())){
                    if(idAviso == null){
                        guardarNuevoAviso(aviso);
                    }else{
                        editarAviso(aviso);
                    }
                    finish();
                }else{
                    Toast.makeText(context, "La fecha final no puede ser anterior a la fecha inicial", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(context, "El campo de descripción no puede estar vacio", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(context, "El campo de titulo no puede estar vacio", Toast.LENGTH_SHORT).show();
        }
    }

    public void guardarNuevoAviso(Aviso aviso){
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(new StringRequest(Request.Method.POST, Url.crearNuevoAviso, result -> {
            Log.e("RESULT", result);
            try {
                JSONObject jsonResult = new JSONObject(result);
                if (!jsonResult.getBoolean("error")) {
                    Toast.makeText(context, "Aviso creado con exito", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(context, "Se ha producido un error al crear el aviso, por favor intentalo más tarde", Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> {
            Toast.makeText(context, "Se ha producido un error al conectar con el servidor", Toast.LENGTH_SHORT).show();
            error.printStackTrace();
        }) {
            protected Map<String, String> getParams() {
                Map<String, String> parametros = aviso.toMap(context);
                if (imagenStringByte != null) {
                    parametros.put(Aviso.IMAGEN, imagenStringByte);
                }
                return parametros;
            }
        });
    }

    public void editarAviso(Aviso aviso){
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(new StringRequest(Request.Method.POST, Url.editarAviso, result -> {
            Log.e("RESULT", result);
            try {
                JSONObject jsonResult = new JSONObject(result);
                if (!jsonResult.getBoolean("error")) {
                    Toast.makeText(context, "Aviso editado con exito", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(context, "Se ha producido un error al editar el aviso, por favor intentalo más tarde", Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> {
            Toast.makeText(context, "Se ha producido un error al conectar con el servidor", Toast.LENGTH_SHORT).show();
            error.printStackTrace();
        }) {
            protected Map<String, String> getParams() {
                Map<String, String> parametros = aviso.toMap(context);
                if (imagenStringByte != null) {
                    parametros.put(Aviso.IMAGEN, imagenStringByte);
                }
                parametros.put("idAviso", idAviso);
                return parametros;
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == ActivitySeleccionarImagen.SELECION_IMAGEN_GALERIA) {
            if (requestCode == 1 && data != null && data.getData() != null) {
                Uri uriImagen = data.getData();
                try {
                    Bitmap bitmapImagen = MediaStore.Images.Media.getBitmap(context.getContentResolver(), uriImagen);
                    imagenStringByte = bitmapToString(bitmapImagen);
                    Glide.with(context).load(uriImagen.toString()).into(ivImagenAviso);
                    btnEliminarImagen.setVisibility(View.VISIBLE);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                nombreImagen = generarNombreImagen();// + "." + getFileExtension(uriImagen);
            }
        }
        if (resultCode == ActivitySeleccionarImagen.SELECION_IMAGEN_SERVIDOR) {
            nombreImagen = data.getStringExtra("nombreImagen");
            String idGrupo = usuario.getGruposAdministrados()[spinnerGrupo.getSelectedItemPosition()].id;
            Glide.with(context).load(Url.obtenerImagenAviso + idGrupo + "/img/" + nombreImagen).into(ivImagenAviso);
            btnEliminarImagen.setVisibility(View.VISIBLE);
        }

        tvAñadirImagen.setText("Cambiar imagen");
        ivImagenAviso.setPaddingRelative(25, 5, 25, 5);
    }

    private String bitmapToString(Bitmap bitmap) {
        ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, arrayOutputStream);
        byte[] imagenByte = arrayOutputStream.toByteArray();
        return Base64.encodeToString(imagenByte, Base64.DEFAULT);
    }

    private String getFileExtension(Uri uri) {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    private String generarNombreImagen() {
        return System.currentTimeMillis() + "_" + usuario.getGruposAdministrados()[spinnerGrupo.getSelectedItemPosition()].id + "_" + usuario.getId() + ".jpeg";
    }
}