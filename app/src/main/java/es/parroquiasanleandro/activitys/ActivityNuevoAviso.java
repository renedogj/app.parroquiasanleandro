package es.parroquiasanleandro.activitys;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.pdf.PdfRenderer;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
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

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
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

    private static final int SELECT_FILE_REQUEST_CODE = 100;

    private EditText etTitulo;
    private ImageView ivImagenAviso;
    private ConstraintLayout cntrtlytAñadirImagen;
    private ImageButton btnEliminarImagen;
    private TextView tvAñadirImagen;
    private EditText etDescripcion;
    private EditText etUrl;
    //private TextView tvArchivo;
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
    private Button bttnEliminarAviso;

    private String imagenStringByte = null;
    private String nombreImagen = null;

    //private String filePath = null;
    //List<Bitmap> bitmaps = null;

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
        //tvArchivo = findViewById(R.id.tvArchivo);
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
        bttnEliminarAviso = findViewById(R.id.bttnEliminarAviso);

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
        spinnerGrupo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                eliminarImagen();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                eliminarImagen();
            }
        });

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
                fechaInicio.actualizarDiaSemana();
                tvFechaInicio.setText(fechaInicio.toString(Fecha.FormatosFecha.EE_d_MMM_aaaa));
                actualizarFechaFin();
            }, fechaInicio.año, fechaInicio.mes.getNumeroMes() - 1, fechaInicio.dia);
            datePickerDialog.show();
        });

        tvHoraInicio.setOnClickListener(v -> {
            TimePickerDialog timePickerDialog = new TimePickerDialog(context, (view, hourOfDay, minute) -> {
                fechaInicio.hora = hourOfDay;
                fechaInicio.minuto = minute;
                fechaInicio.actualizarDiaSemana();
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
                fechaFin.actualizarDiaSemana();
                tvFechaFinal.setText(fechaFin.toString(Fecha.FormatosFecha.EE_d_MMM_aaaa));
            }, fechaFin.año, fechaFin.mes.getNumeroMes() - 1, fechaFin.dia);
            datePickerDialog.show();
        });

        tvHoraFinal.setOnClickListener(v -> {
            TimePickerDialog timePickerDialog = new TimePickerDialog(context, (view, hourOfDay, minute) -> {
                fechaFin.hora = hourOfDay;
                fechaFin.minuto = minute;
                fechaFin.actualizarDiaSemana();
                tvHoraFinal.setText(fechaFin.toString(Fecha.FormatosFecha.HH_mm));
            }, fechaFin.hora, fechaFin.minuto, true);
            timePickerDialog.show();
        });


        bttnNuevoAviso.setOnClickListener(v -> guardarAviso());

        bttnCancelar.setOnClickListener(v -> finish());

        cntrtlytAñadirImagen.setOnClickListener(v -> {
            Intent intent = new Intent(context, ActivitySeleccionarImagen.class);
            intent.putExtra("idGrupo", usuario.getGruposAdministrados()[spinnerGrupo.getSelectedItemPosition()].id);
            startActivityForResult(intent, 0);
        });


        btnEliminarImagen.setOnClickListener(v -> {
            /*imagenStringByte = null;
            nombreImagen = null;
            Glide.with(context).clear(ivImagenAviso);
            btnEliminarImagen.setVisibility(View.GONE);*/
            eliminarImagen();
        });

        /*tvArchivo.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("application/pdf");
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            startActivityForResult(Intent.createChooser(intent, "Select a PDF"), SELECT_FILE_REQUEST_CODE);
        });*/

        if (idAviso != null) {
            Volley.newRequestQueue(context).add(new StringRequest(Request.Method.POST, Url.obtenerAviso, result -> {
                try {
                    JSONObject jsonResult = new JSONObject(result);
                    if (!jsonResult.getBoolean("error")) {
                        JSONObject jsonAviso = jsonResult.getJSONObject("aviso");
                        Aviso aviso = Aviso.JSONObjectToAviso(jsonAviso);
                        etTitulo.setText(aviso.titulo);
                        etDescripcion.setText(aviso.descripcion);
                        aviso.asignarImagen(context, ivImagenAviso);
                        btnEliminarImagen.setVisibility(View.VISIBLE);
                        if (!aviso.nombreImagen.equals("null")) {
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

                        spinnerGrupo.setSelection(Arrays.asList(Grupo.getIdsGrupos(usuario.getGruposAdministrados())).indexOf(aviso.idGrupo));
                        if (aviso.url != null) {
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

            bttnEliminarAviso.setVisibility(View.VISIBLE);
            bttnEliminarAviso.setOnClickListener(v -> {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
                alertDialog.setTitle("Eliminar Aviso");
                alertDialog.setMessage("¿Estás seguro de que quieres eliminar este aviso? \nEsta acción no se puede deshacer.");
                alertDialog.setIcon(android.R.drawable.ic_dialog_alert);
                alertDialog.setPositiveButton(android.R.string.yes, (dialog, whichButton) -> {
                    eliminarAviso();
                    finish();
                });
                alertDialog.setNegativeButton(android.R.string.no, null).show();
            });
        }
    }

    public void actualizarFechaFin() {
        fechaFin = fechaInicio.clone();
        if (lnlytFechaFinal.getVisibility() == View.VISIBLE) {
            fechaFin.sumMinutos(60);
        }
        fechaFin.actualizarDiaSemana();
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
            if (aviso.getFechaInicio().esIgualA(aviso.getFechaFin()) || Fecha.isFecha1MayorQueFecha2(aviso.getFechaFin(), aviso.getFechaInicio())) {
                if (idAviso == null) {
                    guardarNuevoAviso(aviso);
                } else {
                    editarAviso(aviso);
                }
            } else {
                Toast.makeText(context, "La fecha final no puede ser anterior a la fecha inicial", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(context, "El campo de titulo no puede estar vacio", Toast.LENGTH_SHORT).show();
        }
    }

    public void guardarNuevoAviso(Aviso aviso) {
        /*if(filePath != null){
            // Use the file path to start the upload task
            UploadFileTask uploadFileTask = new UploadFileTask();
            uploadFileTask.setIdGrupo(aviso.idGrupo);
            uploadFileTask.execute(filePath);
        }*/
        ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Guardando el aviso, por favor espera...");
        progressDialog.show();

        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(new StringRequest(Request.Method.POST, Url.crearNuevoAviso, result -> {
            progressDialog.dismiss();
            Log.e("RESULT", result);
            try {
                JSONObject jsonResult = new JSONObject(result);
                if (!jsonResult.getBoolean("error")) {
                    Toast.makeText(context, "Aviso creado con exito", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(context, "Se ha producido un error al crear el aviso, por favor intentalo más tarde", Toast.LENGTH_SHORT).show();
                }
                finish();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> {
            progressDialog.dismiss();
            Toast.makeText(context, "Se ha producido un error al conectar con el servidor", Toast.LENGTH_SHORT).show();
            error.printStackTrace();
        }) {
            protected Map<String, String> getParams() {
                Map<String, String> parametros = aviso.toMap(context);
                if (imagenStringByte != null) {
                    parametros.put(Aviso.IMAGEN, imagenStringByte);
                }
                /*if (bitmaps != null){
                    for (int i = 0; i < bitmaps.size(); i++) {
                        Bitmap bitmap = bitmaps.get(i);
                        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
                        byte[] imageBytes = byteArrayOutputStream.toByteArray();
                        String imageString = Base64.encodeToString(imageBytes, Base64.DEFAULT);
                        parametros.put("image_" + i, imageString);
                    }
                    parametros.put("file_name",System.currentTimeMillis() +".pdf");
                }*/
                return parametros;
            }
        }.setRetryPolicy(new DefaultRetryPolicy(20 * 1000 * 10, 1, 1.0f)));
    }

    public void editarAviso(Aviso aviso) {
        ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Guardando el aviso, por favor espera...");
        progressDialog.show();

        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(new StringRequest(Request.Method.POST, Url.editarAviso, result -> {
            progressDialog.dismiss();
            Log.e("RESULT", result);
            try {
                JSONObject jsonResult = new JSONObject(result);
                if (!jsonResult.getBoolean("error")) {
                    Toast.makeText(context, "Aviso editado con exito", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(context, "Se ha producido un error al editar el aviso, por favor intentalo más tarde", Toast.LENGTH_SHORT).show();
                }
                finish();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> {
            progressDialog.dismiss();
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

    public void eliminarAviso() {
        ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Eliminando el aviso, por favor espera...");
        progressDialog.show();

        Volley.newRequestQueue(context).add(new StringRequest(Request.Method.POST, Url.eliminarAviso, result -> {
            progressDialog.dismiss();
            Log.e("RESULT",result);
            try {
                JSONObject jsonResult = new JSONObject(result);
                if (!jsonResult.getBoolean("error")) {
                    Toast.makeText(context, "Aviso eliminado con éxito", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, "Se ha producido un error al eliminar el aviso", Toast.LENGTH_SHORT).show();
                }
                finish();
            } catch (JSONException e) {
                Toast.makeText(context, "Se ha producido un error en el servidor al eliminar el aviso", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
                onBackPressed();
            }
        }, error -> {
            progressDialog.dismiss();
            Toast.makeText(context, "Se ha producido un error al conectar con el servidor", Toast.LENGTH_SHORT).show();
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> parametros = new HashMap<>();
                parametros.put("idUsuario", usuario.getId());
                parametros.put("idAviso", idAviso);
                return parametros;
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //if (requestCode == SELECT_FILE_REQUEST_CODE) {
            /*Uri uriFile = data.getData();
            // Get the file path from the URI
            //filePath = getFilePathFromUri(uriFile);
            //tvArchivo.setText(filePath);

            bitmaps = getBitmapsFromPdf(uriFile);
            tvArchivo.setText("Archivo selecionado con exito");
            Log.e("BIT MAPS",bitmaps.toString());
            Log.e("BIT MAPS",bitmaps.size() + "");*/
        //} else {
            if (resultCode == ActivitySeleccionarImagen.SELECION_IMAGEN_GALERIA) {
                if (data != null && data.getData() != null) {
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
        //}
    }

    public List<Bitmap> getBitmapsFromPdf(Uri pdfUri) {
        List<Bitmap> bitmaps = new ArrayList<>();
        PdfRenderer pdfRenderer = null;
        try {
            pdfRenderer = new PdfRenderer(getContentResolver().openFileDescriptor(pdfUri, "r"));
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        try {
            //ParcelFileDescriptor fileDescriptor = ParcelFileDescriptor.open(pdfFile, ParcelFileDescriptor.MODE_READ_ONLY);
            //PdfRenderer pdfRenderer = new PdfRenderer(fileDescriptor);
            for (int i = 0; i < pdfRenderer.getPageCount(); i++) {
                PdfRenderer.Page page = pdfRenderer.openPage(i);
                int width = context.getResources().getDisplayMetrics().densityDpi / 72 * page.getWidth();
                int height = context.getResources().getDisplayMetrics().densityDpi / 72 * page.getHeight();
                Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
                page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);
                page.close();
                bitmaps.add(bitmap);
            }
            pdfRenderer.close();
            //fileDescriptor.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return  bitmaps;
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

    /*private String getFilePathFromUri(Uri uri) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            return getFilePathFromUriKitKat(uri);
        } else {
            return uri.getPath();
        }
    }*/

    /*@TargetApi(Build.VERSION_CODES.KITKAT)
    private String getFilePathFromUriKitKat(Uri uri) {
        String filePath = null;
        if (DocumentsContract.isDocumentUri(this, uri)) {
            String documentId = DocumentsContract.getDocumentId(uri);
            String[] split = documentId.split(":");
            String type = split[0];
            if ("primary".equalsIgnoreCase(type)) {
                filePath = Environment.getExternalStorageDirectory() + "/" + split[1];
            }
        }
        return filePath;
    }*/

    private void eliminarImagen(){
        imagenStringByte = null;
        nombreImagen = null;
        Glide.with(context).clear(ivImagenAviso);
        btnEliminarImagen.setVisibility(View.GONE);
    }
}