package es.parroquiasanleandro.activitys;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

import es.parroquiasanleandro.Aviso;
import es.parroquiasanleandro.Grupo;
import es.parroquiasanleandro.R;
import es.parroquiasanleandro.Usuario;
import es.parroquiasanleandro.fecha.Fecha;
import es.parroquiasanleandro.fecha.Mes;

;

public class ActivityNuevoAviso extends AppCompatActivity {
	Context context = ActivityNuevoAviso.this;

	private EditText etTitulo;
	private ImageView ivImagenAviso;
	private LinearLayout lnlytAñadirImagen;
	private TextView tvAñadirImagen;
	private EditText etDescripcion;
	private Switch switchTodoElDia;
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

	private boolean todoElDia;
	private Uri uriImagen = null;
	private String nombreImagen = null;

	private Fecha fechaInicio;
	private Fecha fechaFin;

	private Usuario usuario;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_nuevo_aviso);

		etTitulo = findViewById(R.id.etTitulo);
		ivImagenAviso = findViewById(R.id.ivImagenAviso);
		lnlytAñadirImagen = findViewById(R.id.lnlytAñadirImagen);
		tvAñadirImagen = findViewById(R.id.tvAñadirImagen);
		etDescripcion = findViewById(R.id.etDescripcion);
		switchTodoElDia = findViewById(R.id.switchTodoElDia);
		tvFechaInicio = findViewById(R.id.tvFechaInicio);
		tvHoraInicio = findViewById(R.id.tvHoraInicio);
		lnlytFechaFinal = findViewById(R.id.lnlytFechaFinal);
		tvFechaFinal = findViewById(R.id.tvFechaFinal);
		tvHoraFinal = findViewById(R.id.tvHoraFinal);
		lnlytAñadirFechaFinal = findViewById(R.id.lnlytAñadirFechaFinal);
		tvSimboloAñadirFechaFinal = findViewById(R.id.tvSimboloAñadirFechaFinal);
		tvAñadirFechaFinal = findViewById(R.id.tvAñadirFechaFinal);
		spinnerGrupo = findViewById(R.id.spinnerGrupo);
		bttnNuevoAviso = findViewById(R.id.bttnNuevoAviso);
		bttnCancelar = findViewById(R.id.bttnCancelar);

		usuario = Usuario.recuperarUsuarioLocal(context);
		String[] nombreGruposAdministrados;
		if(usuario.getGruposAdministrados() != null){
			nombreGruposAdministrados = Grupo.getNombreGrupos(usuario.getGruposAdministrados());
		}else{
			nombreGruposAdministrados = new String[0];
		}
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, R.layout.spinner_grupo_item, nombreGruposAdministrados);
		spinnerGrupo.setAdapter(adapter);

		switchTodoElDia.setOnCheckedChangeListener((buttonView, isChecked) -> {
			todoElDia = isChecked;
			if (todoElDia) {
				tvHoraInicio.setVisibility(View.GONE);
				tvHoraFinal.setVisibility(View.GONE);
			} else {
				tvHoraInicio.setVisibility(View.VISIBLE);
				tvHoraFinal.setVisibility(View.VISIBLE);
			}
		});

		lnlytAñadirFechaFinal.setOnClickListener(v -> {
			if (lnlytFechaFinal.getVisibility() == View.GONE) {
				lnlytFechaFinal.setVisibility(View.VISIBLE);
				tvSimboloAñadirFechaFinal.setText("-");
				tvAñadirFechaFinal.setText("Quitar fecha de fin");
			} else if (lnlytFechaFinal.getVisibility() == View.VISIBLE) {
				lnlytFechaFinal.setVisibility(View.GONE);
				tvSimboloAñadirFechaFinal.setText("+");
				tvAñadirFechaFinal.setText("Añade fecha de fin");
			}
		});

		fechaInicio = Fecha.FechaActual();
		fechaFin = Fecha.FechaActual();
		//fechaFin.sumMinutos(60);

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
			}, fechaInicio.año, fechaInicio.mes.getNumeroMes() - 1, fechaInicio.dia);
			datePickerDialog.show();
		});

		tvHoraInicio.setOnClickListener(v -> {
			TimePickerDialog timePickerDialog = new TimePickerDialog(context, (view, hourOfDay, minute) -> {
				fechaInicio.hora = hourOfDay;
				fechaInicio.minuto = minute;
				tvHoraInicio.setText(fechaInicio.toString(Fecha.FormatosFecha.HH_mm));
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
			}, fechaFin.hora, fechaFin.minuto, true);
			timePickerDialog.show();
		});


		bttnNuevoAviso.setOnClickListener(v -> guardarNuevoAviso(nuevoAviso()));

		bttnCancelar.setOnClickListener(v -> finish());

		lnlytAñadirImagen.setOnClickListener(v -> {
			Intent intent = new Intent(context, ActivitySeleccionarImagen.class);
			intent.putExtra("grupo", usuario.getGruposAdministrados()[spinnerGrupo.getSelectedItemPosition()].id);
			startActivityForResult(intent, 1);
		});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == 1 && data != null && data.getData() != null) {
			uriImagen = data.getData();
			Glide.with(context)
					.load(data.getData().toString())
					.into(ivImagenAviso);
			nombreImagen = data.getStringExtra("NombreImagen");
			tvAñadirImagen.setText("Cambiar imagen");
			ivImagenAviso.setPaddingRelative(25, 5, 25, 5);
		}
	}

	private String getFileExtension(Uri uri) {
		ContentResolver cR = getContentResolver();
		MimeTypeMap mime = MimeTypeMap.getSingleton();
		return mime.getExtensionFromMimeType(cR.getType(uri));
	}

	private void guardarNuevoAviso(Aviso aviso) {
		if (aviso.titulo.length() > 0) {
			if (aviso.descripcion.length() > 0) {
				/*DatabaseReference refAviso = FirebaseDatabase.getInstance().getReference().child("Avisos").child(aviso.grupo).push();
				refAviso.setValue(aviso);
				aviso.key = refAviso.getKey();
				aviso.setFechaInicio(Fecha.toFecha(aviso.longInicio));;
				FirebaseDatabase.getInstance().getReference().child("Calendario").child(fechaInicio.toString(Fecha.FormatosFecha.aaaaMM))
						.child(aviso.getFechaInicio().dia+"").child(aviso.key).setValue(aviso.grupo);*/

				Toast.makeText(context, "Aviso creado con exito", Toast.LENGTH_LONG).show();
				finish();
			} else {
				Toast.makeText(context, "El campo de descripción no puede estar vacio", Toast.LENGTH_SHORT).show();
			}
		} else {
			Toast.makeText(context, "El campo de titulo no puede estar vacio", Toast.LENGTH_SHORT).show();
		}
	}

	private Aviso nuevoAviso() {
		String imagen;
		String userUid = "FirebaseAuth.getInstance().getUid()";
		String titulo = etTitulo.getText().toString().trim();
		String descripcion = etDescripcion.getText().toString().trim();
		String grupoKey = usuario.getGruposAdministrados()[spinnerGrupo.getSelectedItemPosition()].id;
		if (uriImagen == null) {
			imagen = "imagenPredeterminada";
		} else {
			if (nombreImagen == null) {
				imagen = subirImagen(grupoKey);
			} else {
				imagen = nombreImagen;
			}
		}
		if (fechaInicio.esIgualA(fechaFin)) {
			return new Aviso(titulo, descripcion, grupoKey, fechaInicio, todoElDia, imagen, userUid);
		} else {
			return new Aviso(titulo, descripcion, grupoKey, fechaInicio, fechaFin, todoElDia, imagen, userUid);
		}
	}

	private String subirImagen(String grupoKey) {
		String nombreImagen = System.currentTimeMillis() + "." + getFileExtension(uriImagen);
		//FirebaseStorage.getInstance().getReference("ImagenesAvisos").child(grupoKey).child(nombreImagen).putFile(uriImagen)
		//		.addOnFailureListener(e -> Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show());
		return nombreImagen;
	}
}