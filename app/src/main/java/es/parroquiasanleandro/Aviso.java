package es.parroquiasanleandro;

import android.content.Context;
import android.graphics.Color;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import es.renedogj.fecha.Fecha;

public class Aviso {
    public static final String AVISOS = "Avisos";
    public static final String TITULO = "titulo";
    public static final String ID_CREADOR = "idCreador";
    public static final String ID_GRUPO = "idGrupo";
    public static final String DESCRIPCION = "descripcion";
    public static final String IMAGEN = "imagen";
    public static final String NOMBRE_IMAGEN = "nombreImagen";
    public static final String URL = "url";
    public static final String ARCHIVO = "archivo";
    public static final String FECHA_INICIO = "fechaInicio";
    public static final String FECHA_FIN = "fechaFin";

    public String id;
    public String idCreador;
    public String idGrupo;
    public String titulo;
    public String descripcion;
    public String nombreImagen;
    public String url;
    public String archivo;
    public long longInicio;
    public long longFin;
    private Fecha fechaInicio;
    private Fecha fechaFin;
    //public boolean todoElDia;

    public Aviso() {
        //setFechaInicio(Fecha.toFecha(longInicio));
        //setFechaFin(Fecha.toFecha(longFin));
    }

    public Aviso(String titulo, String descripcion, String idGrupo, Fecha fechaInicio, Fecha fechaFin, /*boolean todoElDia,*/ String nombreImagen, String uidCreador) {
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.idGrupo = idGrupo;
        this.longInicio = Fecha.toMillis(fechaInicio);
        this.longFin = Fecha.toMillis(fechaFin);
        //this.todoElDia = todoElDia;
        this.nombreImagen = nombreImagen;
        this.url = null;
        this.archivo = null;
        this.idCreador = uidCreador;
    }

    public Aviso(String titulo, String descripcion, String idGrupo, Fecha fechaInicio, /*boolean todoElDia,*/ String nombreImagen, String uidCreador) {
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.idGrupo = idGrupo;
        this.longInicio = Fecha.toMillis(fechaInicio);
		/*if(todoElDia){
			fechaInicio.sumDias(1);
			fechaInicio.hora = 0;
			fechaInicio.minuto = 0;
			fechaInicio.segundo = 0;
		}else{
			fechaInicio.sumHoras(1);
		}*/
        this.longFin = Fecha.toMillis(fechaInicio);
        //this.todoElDia = todoElDia;
        this.nombreImagen = nombreImagen;
        this.url = null;
        this.archivo = null;
        this.idCreador = uidCreador;
    }

    public Fecha getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(Fecha fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public Fecha getFechaFin() {
        return fechaFin;
    }

    public void setFechaFin(Fecha fechaFin) {
        this.fechaFin = fechaFin;
    }

    public void asignarColor(Context context, LinearLayout linearLayout) {
        linearLayout.setBackgroundColor(Color.parseColor(Grupo.obtenerColorGrupo(context, idGrupo)));
    }

    public static List<Aviso> JSONArrayToAvisos(JSONArray jsonArrayAvisos) {
        List<Aviso> avisos = new ArrayList<>();
        for (int i = 0; i < jsonArrayAvisos.length(); i++) {
            Aviso avisoAux = new Aviso();
            try {
                JSONObject jsonAviso = jsonArrayAvisos.getJSONObject(i);
                avisoAux.id = jsonAviso.getString("id");
                avisoAux.idCreador = jsonAviso.getString("id_creador");
                avisoAux.titulo = jsonAviso.getString("titulo");
                avisoAux.idGrupo = jsonAviso.getString("id_grupo");
                avisoAux.descripcion = jsonAviso.getString("descripcion");
                avisoAux.nombreImagen = jsonAviso.getString("imagen");
                avisoAux.longInicio = jsonAviso.getLong("fecha_inicio");
                avisoAux.longFin = jsonAviso.getLong("fecha_fin");
                avisoAux.fechaInicio = Fecha.toFecha(jsonAviso.getLong("fecha_inicio"));
                avisoAux.fechaFin = Fecha.toFecha(jsonAviso.getLong("fecha_fin"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            avisos.add(avisoAux);
        }
        return avisos;
    }

    public static Aviso JSONObjectToAviso(JSONObject jsonAviso) {
        Aviso aviso = new Aviso();
        try {
            aviso.id = jsonAviso.getString("id");
            aviso.idCreador = jsonAviso.getString("id_creador");
            aviso.titulo = jsonAviso.getString("titulo");
            aviso.idGrupo = jsonAviso.getString("id_grupo");
            aviso.descripcion = jsonAviso.getString("descripcion");
            aviso.nombreImagen = jsonAviso.getString("imagen");
            aviso.url = jsonAviso.getString("url");
            if (aviso.url.equals("null")) aviso.url = null;
            aviso.archivo = jsonAviso.getString("archivo");
            if (aviso.archivo.equals("null")) aviso.archivo = null;
            aviso.longInicio = jsonAviso.getLong("fecha_inicio");
            aviso.longFin = jsonAviso.getLong("fecha_fin");
            aviso.fechaInicio = Fecha.toFecha(jsonAviso.getLong("fecha_inicio"));
            aviso.fechaFin = Fecha.toFecha(jsonAviso.getLong("fecha_fin"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return aviso;
    }

    //Funcion para asignar la imagen del aviso obteniendolo de la bbdd al imageView
    public void asignarImagen(Context context, ImageView imageView) {
        if (!nombreImagen.equals("null")) {
            Glide.with(context).load(Url.obtenerImagenAviso + idGrupo + "/img/" + nombreImagen + ".jpeg").into(imageView);
        } else {
            Glide.with(context).load(Url.obtenerImagenAviso + idGrupo + "/img/" + Grupo.obtenerImagenGrupo(context, idGrupo)).into(imageView);
        }
    }

    public Map<String, String> toMap() {
        Map<String, String> avisoMap = new HashMap<>();
        avisoMap.put(Aviso.TITULO, titulo);
        avisoMap.put(Aviso.DESCRIPCION, descripcion);
        avisoMap.put(Aviso.ID_GRUPO, idGrupo);
        avisoMap.put(Aviso.ID_CREADOR, idCreador);
        avisoMap.put(Aviso.FECHA_INICIO, longInicio + "");
        avisoMap.put(Aviso.FECHA_FIN, longFin + "");

        if (nombreImagen != null) avisoMap.put(Aviso.NOMBRE_IMAGEN, nombreImagen);
        if (url != null) avisoMap.put(Aviso.URL, url);
        if (archivo != null) avisoMap.put(Aviso.ARCHIVO, archivo);
        return avisoMap;
    }
}
