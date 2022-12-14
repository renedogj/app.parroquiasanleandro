package es.parroquiasanleandro;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import es.parroquiasanleandro.bbdd_SQLite.FeedReaderContract;
import es.parroquiasanleandro.bbdd_SQLite.FeedReaderDbHelper;

public class Grupo {
    public static final String GRUPOS = "GRUPOS";
    public static final String SUSCRIPCIONES = "suscripciones";
    public static final String ID_PADRE = "A";
    public static final String NOMBRE_PADRE = "General";
    public static final String ID = "id";
    public static final String NOMBRE = "nombre";
    public static final String COLOR = "color";
    public static final String IMAGEN = "imagen";
    public static final String MILLIS_ACTUALIZACION = "millis_actualizacion";

    public String key;
    public String nombre;
    public String color;
    public String imagen;
    int posicion;

    public Grupo() {
    }

    public Grupo(String key, String nombre) {
        this.key = key;
        this.nombre = nombre;
    }

    public Grupo(String key, String nombre, String color, String imagen) {
        this.key = key;
        this.nombre = nombre;
        this.color = color;
        this.imagen = imagen;
    }

    public String getKey() {
        return key;
    }

    public String getNombre() {
        return nombre;
    }

    public int getPosicion() {
        return posicion;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setPosicion(int posicion) {
        this.posicion = posicion;
    }

    public static Grupo[] convertirGrupo(String[] gruposKey, String[] gruposNombre) {
        List<Grupo> grupos = new ArrayList<>();
        if (gruposKey.length == gruposNombre.length) {
            for (int i = 0; i < gruposKey.length; i++) {
                grupos.add(new Grupo(gruposKey[i], gruposNombre[i]));
            }
        }
        return grupos.toArray(new Grupo[0]);
    }

    public static String[] getNombreGrupos(Grupo[] grupos){
        List<String> nombres = new ArrayList<>();
        for (Grupo grupo : grupos){
            nombres.add(grupo.nombre);
        }
        return nombres.toArray(new String[0]);
    }

    public static String[] getKeysGrupos(Grupo[] grupos){
        List<String> keys = new ArrayList<>();
        for (Grupo grupo : grupos){
            keys.add(grupo.key);
        }
        return keys.toArray(new String[0]);
    }

    /*public static String getKey(Grupo[] categorias,String nombre){
        for (Grupo grupo: categorias){
            if(grupo.nombre.equals(nombre)){
                return grupo.key;
            }
        }
        return null;
    }*/

    public static long getMillisUltimaActualizacion(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences(GRUPOS, Context.MODE_PRIVATE);
        return sharedPreferences.getLong(MILLIS_ACTUALIZACION, 0);
    }

    public static void setMillisUltimaActualizacion(Context context, long timestamp ){
        SharedPreferences sharedPreferences = context.getSharedPreferences(GRUPOS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putLong(MILLIS_ACTUALIZACION, timestamp );
        editor.apply();
    }

    /**
     * Suscribe al usuario a un grupo en la BBDD
     */
    public void suscribirGrupo(Context context, String uid) {
        guardarSuscripcionLocal(context);
        //FirebaseDatabase.getInstance().getReference().child(Usuario.USUARIOS).child(uid).child(SUSCRIPCIONES).child(key).setValue(nombre);
    }

    /**
     * Elimina la suscripcion al usuario a un grupo en la BBDD
     */
    public void dessuscribirGrupo(Context context, String uid) {
        eliminarSuscripcionLocal(context);
        //FirebaseDatabase.getInstance().getReference().child(Usuario.USUARIOS).child(uid).child(SUSCRIPCIONES).child(key).setValue(null);
    }

    public static void actualizarGruposServidorToLocal(Context context){
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(new JsonArrayRequest(Url.obtenerGrupos, response -> {
            FeedReaderDbHelper dbHelper = new FeedReaderDbHelper(context);
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            dbHelper.truncateTable(db, FeedReaderContract.TablaGrupos.TABLE_NAME);

            JSONObject jsonObject;
            for (int i = 0; i < response.length(); i++) {
                try {
                    jsonObject = response.getJSONObject(i);
                    Grupo grupo = new Grupo(
                            jsonObject.getString(Grupo.ID),
                            jsonObject.getString(Grupo.NOMBRE),
                            jsonObject.getString(Grupo.COLOR),
                            jsonObject.getString(Grupo.IMAGEN)
                    );

                    ContentValues registro = new ContentValues();
                    registro.put(FeedReaderContract.TablaGrupos.COLUMN_NAME_ID, grupo.key);
                    registro.put(FeedReaderContract.TablaGrupos.COLUMN_NAME_NOMBRE, grupo.nombre);
                    registro.put(FeedReaderContract.TablaGrupos.COLUMN_NAME_COLOR, grupo.color);
                    registro.put(FeedReaderContract.TablaGrupos.COLUMN_NAME_IMAGEN, grupo.imagen);

                    db.insert(FeedReaderContract.TablaGrupos.TABLE_NAME, null, registro);

                } catch (JSONException e) {
                    Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
            db.close();
        }, error -> {
            Toast.makeText(context, "Se ha producido un error al conectar con el servidor", Toast.LENGTH_SHORT).show();
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return super.getParams();
            }
        });
    }

    public static List<Grupo> recuperarGruposLocal(Context context) {
        FeedReaderDbHelper dbHelper = new FeedReaderDbHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String[] columnasARetornar = {
                FeedReaderContract.TablaGrupos.COLUMN_NAME_ID,
                FeedReaderContract.TablaGrupos.COLUMN_NAME_NOMBRE,
                FeedReaderContract.TablaGrupos.COLUMN_NAME_COLOR
        };
        Cursor cursorConsulta = db.query(
                FeedReaderContract.TablaGrupos.TABLE_NAME,
                columnasARetornar,
                null,
                null,
                null,
                null,
                FeedReaderContract.TablaGruposSuscritos.COLUMN_NAME_ID
        );
        List<Grupo> grupos = new ArrayList<>();
        while (cursorConsulta.moveToNext()) {
            String key = cursorConsulta.getString(cursorConsulta.getColumnIndexOrThrow(FeedReaderContract.TablaGrupos.COLUMN_NAME_ID));
            String nombre = cursorConsulta.getString(cursorConsulta.getColumnIndexOrThrow(FeedReaderContract.TablaGrupos.COLUMN_NAME_NOMBRE));
            String color = cursorConsulta.getString(cursorConsulta.getColumnIndexOrThrow(FeedReaderContract.TablaGrupos.COLUMN_NAME_COLOR));
            String imagen = cursorConsulta.getString(cursorConsulta.getColumnIndexOrThrow(FeedReaderContract.TablaGrupos.COLUMN_NAME_IMAGEN));
            grupos.add(new Grupo(key, nombre, color, imagen));
        }
        cursorConsulta.close();
        db.close();
        return grupos;
    }

    public static String obtenerColorGrupo(Context context, String id){
        FeedReaderDbHelper dbHelper = new FeedReaderDbHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String[] columnasARetornar = {
                FeedReaderContract.TablaGrupos.COLUMN_NAME_COLOR
        };
        Cursor cursorConsulta = db.query(
                FeedReaderContract.TablaGrupos.TABLE_NAME,
                columnasARetornar,
                FeedReaderContract.TablaGrupos.COLUMN_NAME_ID + " = ?",
                new String[]{id},
                null,
                null,
                null
        );
        String color = null;
        while (cursorConsulta.moveToNext()) {
            color = cursorConsulta.getString(cursorConsulta.getColumnIndexOrThrow(FeedReaderContract.TablaGrupos.COLUMN_NAME_COLOR));
        }
        cursorConsulta.close();
        db.close();
        return color;
    }

    public static String obtenerImagenGrupo(Context context, String id){
        FeedReaderDbHelper dbHelper = new FeedReaderDbHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String[] columnasARetornar = {
                FeedReaderContract.TablaGrupos.COLUMN_NAME_IMAGEN
        };
        Cursor cursorConsulta = db.query(
                FeedReaderContract.TablaGrupos.TABLE_NAME,
                columnasARetornar,
                FeedReaderContract.TablaGrupos.COLUMN_NAME_ID + " = ?",
                new String[]{id},
                null,
                null,
                null
        );
        String imagen = null;
        while (cursorConsulta.moveToNext()) {
            imagen = cursorConsulta.getString(cursorConsulta.getColumnIndexOrThrow(FeedReaderContract.TablaGrupos.COLUMN_NAME_IMAGEN));
        }
        cursorConsulta.close();
        db.close();
        return imagen;
    }

    public void guardarSuscripcionLocal(Context context) {
        FeedReaderDbHelper dbHelper = new FeedReaderDbHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        String consulta = "DELETE FROM " + FeedReaderContract.TablaGruposSuscritos.TABLE_NAME +
                " WHERE " + FeedReaderContract.TablaGruposSuscritos.COLUMN_NAME_ID + " = '" + key + "'" +
                " AND " + FeedReaderContract.TablaGruposSuscritos.COLUMN_NAME_NOMBRE + " = '" + nombre + "'";
        db.execSQL(consulta);

        ContentValues registro = new ContentValues();
        registro.put(FeedReaderContract.TablaGruposSuscritos.COLUMN_NAME_ID, key + "");
        registro.put(FeedReaderContract.TablaGruposSuscritos.COLUMN_NAME_NOMBRE, nombre + "");

        db.insert(FeedReaderContract.TablaGruposSuscritos.TABLE_NAME, null, registro);
        db.close();
    }

    public void eliminarSuscripcionLocal(Context context) {
        FeedReaderDbHelper dbHelper = new FeedReaderDbHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String consulta = "DELETE FROM " + FeedReaderContract.TablaGruposSuscritos.TABLE_NAME +
                " WHERE " + FeedReaderContract.TablaGruposSuscritos.COLUMN_NAME_ID + " = '" + key + "'" +
                " AND " + FeedReaderContract.TablaGruposSuscritos.COLUMN_NAME_NOMBRE + " = '" + nombre + "'";
        db.execSQL(consulta);
        db.close();
    }

    public static void guardarGruposSuscritosLocal(Context context, Grupo[] grupos) {
        FeedReaderDbHelper dbHelper = new FeedReaderDbHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        dbHelper.truncateTable(db, FeedReaderContract.TablaGruposSuscritos.TABLE_NAME);

        for (Grupo grupo : grupos) {
            ContentValues registro = new ContentValues();
            registro.put(FeedReaderContract.TablaGruposSuscritos.COLUMN_NAME_ID, grupo.key);
            registro.put(FeedReaderContract.TablaGruposSuscritos.COLUMN_NAME_NOMBRE, grupo.nombre);

            db.insert(FeedReaderContract.TablaGruposSuscritos.TABLE_NAME, null, registro);
        }
        db.close();
    }

    public static Grupo[] recuperarGruposSuscritosLocal(Context context) {
        FeedReaderDbHelper dbHelper = new FeedReaderDbHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String[] columnasARetornar = {
                FeedReaderContract.TablaGruposSuscritos.COLUMN_NAME_ID,
                FeedReaderContract.TablaGruposSuscritos.COLUMN_NAME_NOMBRE
        };
        Cursor cursorConsulta = db.query(
                FeedReaderContract.TablaGruposSuscritos.TABLE_NAME,
                columnasARetornar,
                null,
                null,
                null,
                null,
                FeedReaderContract.TablaGruposSuscritos.COLUMN_NAME_ID
        );
        List<Grupo> grupos = new ArrayList<>();
        while (cursorConsulta.moveToNext()) {
            String id = cursorConsulta.getString(cursorConsulta.getColumnIndexOrThrow(FeedReaderContract.TablaGruposSuscritos.COLUMN_NAME_ID));
            String nombre = cursorConsulta.getString(cursorConsulta.getColumnIndexOrThrow(FeedReaderContract.TablaGruposSuscritos.COLUMN_NAME_NOMBRE));
            grupos.add(new Grupo(id, nombre, obtenerColorGrupo(context, id), obtenerImagenGrupo(context, id)));
        }
        cursorConsulta.close();
        db.close();
        return grupos.toArray(new Grupo[0]);
    }

    public static void guardarGruposAdministradosLocal(Context context, Grupo[] grupos) {
        FeedReaderDbHelper dbHelper = new FeedReaderDbHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        dbHelper.truncateTable(db, FeedReaderContract.TablaGruposAdministrados.TABLE_NAME);

        for (Grupo grupo : grupos) {
            ContentValues registro = new ContentValues();
            registro.put(FeedReaderContract.TablaGruposAdministrados.COLUMN_NAME_ID, grupo.key);
            registro.put(FeedReaderContract.TablaGruposAdministrados.COLUMN_NAME_NOMBRE, grupo.nombre);

            db.insert(FeedReaderContract.TablaGruposAdministrados.TABLE_NAME, null, registro);
        }
        db.close();
    }

    public static Grupo[] recuperarGruposAdministradosLocal(Context context) {
        FeedReaderDbHelper dbHelper = new FeedReaderDbHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String[] columnasARetornar = {
                FeedReaderContract.TablaGruposAdministrados.COLUMN_NAME_ID,
                FeedReaderContract.TablaGruposAdministrados.COLUMN_NAME_NOMBRE
        };
        Cursor cursorConsulta = db.query(
                FeedReaderContract.TablaGruposAdministrados.TABLE_NAME,
                columnasARetornar,
                null,
                null,
                null,
                null,
                FeedReaderContract.TablaGruposSuscritos.COLUMN_NAME_ID
        );
        List<Grupo> grupos = new ArrayList<>();
        while (cursorConsulta.moveToNext()) {
            String key = cursorConsulta.getString(cursorConsulta.getColumnIndexOrThrow(FeedReaderContract.TablaGruposAdministrados.COLUMN_NAME_ID));
            String nombre = cursorConsulta.getString(cursorConsulta.getColumnIndexOrThrow(FeedReaderContract.TablaGruposAdministrados.COLUMN_NAME_NOMBRE));
            grupos.add(new Grupo(key, nombre));
        }
        cursorConsulta.close();
        db.close();
        return grupos.toArray(new Grupo[0]);
    }

    public static void vaciarTablasGrupos(Context context){
        FeedReaderDbHelper dbHelper = new FeedReaderDbHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        dbHelper.truncateTable(db, FeedReaderContract.TablaGruposAdministrados.TABLE_NAME);
        dbHelper.truncateTable(db, FeedReaderContract.TablaGruposSuscritos.TABLE_NAME);
        db.close();
    }
}
