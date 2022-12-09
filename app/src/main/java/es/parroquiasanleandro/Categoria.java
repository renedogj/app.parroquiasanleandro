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

public class Categoria {
    public static final String CATEGORIAS = "CATEGORIAS";
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

    public Categoria() {
    }

    public Categoria(String key, String nombre) {
        this.key = key;
        this.nombre = nombre;
    }

    public Categoria(String key, String nombre, String color, String imagen) {
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

    public static Categoria[] convertirCategoria(String[] categoriasKey, String[] categoriasNombre) {
        List<Categoria> categorias = new ArrayList<>();
        if (categoriasKey.length == categoriasNombre.length) {
            for (int i = 0; i < categoriasKey.length; i++) {
                categorias.add(new Categoria(categoriasKey[i], categoriasNombre[i]));
            }
        }
        return categorias.toArray(new Categoria[0]);
    }

    public static String[] getNombreCategorias(Categoria[] categorias){
        List<String> nombres = new ArrayList<>();
        for (Categoria categoria: categorias){
            nombres.add(categoria.nombre);
        }
        return nombres.toArray(new String[0]);
    }

    public static String[] getKeysCategorias(Categoria[] categorias){
        List<String> keys = new ArrayList<>();
        for (Categoria categoria: categorias){
            keys.add(categoria.key);
        }
        return keys.toArray(new String[0]);
    }

    /*public static String getKey(Categoria[] categorias,String nombre){
        for (Categoria categoria: categorias){
            if(categoria.nombre.equals(nombre)){
                return categoria.key;
            }
        }
        return null;
    }*/

    public static long getMillisUltimaActualizacion(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences(CATEGORIAS, Context.MODE_PRIVATE);
        return sharedPreferences.getLong(MILLIS_ACTUALIZACION, 0);
    }

    public static void setMillisUltimaActualizacion(Context context, long timestamp ){
        SharedPreferences sharedPreferences = context.getSharedPreferences(CATEGORIAS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putLong(MILLIS_ACTUALIZACION, timestamp );
        editor.apply();
    }

    /**
     * Suscribe al usuario a la categoria en la BBDD
     */
    public void suscribirCategoria(Context context, String uid) {
        guardarSuscripcionLocal(context);
        //FirebaseDatabase.getInstance().getReference().child(Usuario.USUARIOS).child(uid).child(SUSCRIPCIONES).child(key).setValue(nombre);
    }

    /**
     * Elimina la suscripcion al usuario a la categoria en la BBDD
     */
    public void dessuscribirCategoria(Context context, String uid) {
        eliminarSuscripcionLocal(context);
        //FirebaseDatabase.getInstance().getReference().child(Usuario.USUARIOS).child(uid).child(SUSCRIPCIONES).child(key).setValue(null);
    }

    public static void actualizarCategoriasServidorToLocal(Context context){
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(new JsonArrayRequest(Url.obtenerCategorias, response -> {
            FeedReaderDbHelper dbHelper = new FeedReaderDbHelper(context);
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            dbHelper.truncateTable(db, FeedReaderContract.TablaCategorias.TABLE_NAME);

            JSONObject jsonObject;
            for (int i = 0; i < response.length(); i++) {
                try {
                    jsonObject = response.getJSONObject(i);
                    Categoria categoria = new Categoria(
                            jsonObject.getString(Categoria.ID),
                            jsonObject.getString(Categoria.NOMBRE),
                            jsonObject.getString(Categoria.COLOR),
                            jsonObject.getString(Categoria.IMAGEN)
                    );

                    ContentValues registro = new ContentValues();
                    registro.put(FeedReaderContract.TablaCategorias.COLUMN_NAME_ID, categoria.key);
                    registro.put(FeedReaderContract.TablaCategorias.COLUMN_NAME_NOMBRE, categoria.nombre);
                    registro.put(FeedReaderContract.TablaCategorias.COLUMN_NAME_COLOR, categoria.color);
                    registro.put(FeedReaderContract.TablaCategorias.COLUMN_NAME_IMAGEN, categoria.imagen);

                    db.insert(FeedReaderContract.TablaCategorias.TABLE_NAME, null, registro);

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

    public static List<Categoria> recuperarCategoriasLocal(Context context) {
        FeedReaderDbHelper dbHelper = new FeedReaderDbHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String[] columnasARetornar = {
                FeedReaderContract.TablaCategorias.COLUMN_NAME_ID,
                FeedReaderContract.TablaCategorias.COLUMN_NAME_NOMBRE,
                FeedReaderContract.TablaCategorias.COLUMN_NAME_COLOR
        };
        Cursor cursorConsulta = db.query(
                FeedReaderContract.TablaCategorias.TABLE_NAME,
                columnasARetornar,
                null,
                null,
                null,
                null,
                FeedReaderContract.TablaCategoriasSuscritas.COLUMN_NAME_ID
        );
        List<Categoria> categorias = new ArrayList<>();
        while (cursorConsulta.moveToNext()) {
            String key = cursorConsulta.getString(cursorConsulta.getColumnIndexOrThrow(FeedReaderContract.TablaCategorias.COLUMN_NAME_ID));
            String nombre = cursorConsulta.getString(cursorConsulta.getColumnIndexOrThrow(FeedReaderContract.TablaCategorias.COLUMN_NAME_NOMBRE));
            String color = cursorConsulta.getString(cursorConsulta.getColumnIndexOrThrow(FeedReaderContract.TablaCategorias.COLUMN_NAME_COLOR));
            String imagen = cursorConsulta.getString(cursorConsulta.getColumnIndexOrThrow(FeedReaderContract.TablaCategorias.COLUMN_NAME_IMAGEN));
            categorias.add(new Categoria(key, nombre, color, imagen));
        }
        cursorConsulta.close();
        db.close();
        return categorias;
    }

    public static String obtenerColorCategoria(Context context, String id){
        FeedReaderDbHelper dbHelper = new FeedReaderDbHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String[] columnasARetornar = {
                FeedReaderContract.TablaCategorias.COLUMN_NAME_COLOR
        };
        Cursor cursorConsulta = db.query(
                FeedReaderContract.TablaCategorias.TABLE_NAME,
                columnasARetornar,
                FeedReaderContract.TablaCategorias.COLUMN_NAME_ID + " = ?",
                new String[]{id},
                null,
                null,
                null
        );
        String color = null;
        while (cursorConsulta.moveToNext()) {
            color = cursorConsulta.getString(cursorConsulta.getColumnIndexOrThrow(FeedReaderContract.TablaCategorias.COLUMN_NAME_COLOR));
        }
        cursorConsulta.close();
        db.close();
        return color;
    }

    public static String obtenerImagenCategoria(Context context, String id){
        FeedReaderDbHelper dbHelper = new FeedReaderDbHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String[] columnasARetornar = {
                FeedReaderContract.TablaCategorias.COLUMN_NAME_IMAGEN
        };
        Cursor cursorConsulta = db.query(
                FeedReaderContract.TablaCategorias.TABLE_NAME,
                columnasARetornar,
                FeedReaderContract.TablaCategorias.COLUMN_NAME_ID + " = ?",
                new String[]{id},
                null,
                null,
                null
        );
        String imagen = null;
        while (cursorConsulta.moveToNext()) {
            imagen = cursorConsulta.getString(cursorConsulta.getColumnIndexOrThrow(FeedReaderContract.TablaCategorias.COLUMN_NAME_IMAGEN));
        }
        cursorConsulta.close();
        db.close();
        return imagen;
    }

    public void guardarSuscripcionLocal(Context context) {
        FeedReaderDbHelper dbHelper = new FeedReaderDbHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        String consulta = "DELETE FROM " + FeedReaderContract.TablaCategoriasSuscritas.TABLE_NAME +
                " WHERE " + FeedReaderContract.TablaCategoriasSuscritas.COLUMN_NAME_ID + " = '" + key + "'" +
                " AND " + FeedReaderContract.TablaCategoriasSuscritas.COLUMN_NAME_NOMBRE + " = '" + nombre + "'";
        db.execSQL(consulta);

        ContentValues registro = new ContentValues();
        registro.put(FeedReaderContract.TablaCategoriasSuscritas.COLUMN_NAME_ID, key + "");
        registro.put(FeedReaderContract.TablaCategoriasSuscritas.COLUMN_NAME_NOMBRE, nombre + "");

        db.insert(FeedReaderContract.TablaCategoriasSuscritas.TABLE_NAME, null, registro);
        db.close();
    }

    public void eliminarSuscripcionLocal(Context context) {
        FeedReaderDbHelper dbHelper = new FeedReaderDbHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String consulta = "DELETE FROM " + FeedReaderContract.TablaCategoriasSuscritas.TABLE_NAME +
                " WHERE " + FeedReaderContract.TablaCategoriasSuscritas.COLUMN_NAME_ID + " = '" + key + "'" +
                " AND " + FeedReaderContract.TablaCategoriasSuscritas.COLUMN_NAME_NOMBRE + " = '" + nombre + "'";
        db.execSQL(consulta);
        db.close();
    }

    public static void guardarCategoriasSuscritasLocal(Context context, Categoria[] categorias) {
        FeedReaderDbHelper dbHelper = new FeedReaderDbHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        dbHelper.truncateTable(db, FeedReaderContract.TablaCategoriasSuscritas.TABLE_NAME);

        for (Categoria categoria : categorias) {
            ContentValues registro = new ContentValues();
            registro.put(FeedReaderContract.TablaCategoriasSuscritas.COLUMN_NAME_ID, categoria.key);
            registro.put(FeedReaderContract.TablaCategoriasSuscritas.COLUMN_NAME_NOMBRE, categoria.nombre);

            db.insert(FeedReaderContract.TablaCategoriasSuscritas.TABLE_NAME, null, registro);
        }
        db.close();
    }

    public static Categoria[] recuperarCategoriasSuscritasLocal(Context context) {
        FeedReaderDbHelper dbHelper = new FeedReaderDbHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String[] columnasARetornar = {
                FeedReaderContract.TablaCategoriasSuscritas.COLUMN_NAME_ID,
                FeedReaderContract.TablaCategoriasSuscritas.COLUMN_NAME_NOMBRE
        };
        Cursor cursorConsulta = db.query(
                FeedReaderContract.TablaCategoriasSuscritas.TABLE_NAME,
                columnasARetornar,
                null,
                null,
                null,
                null,
                FeedReaderContract.TablaCategoriasSuscritas.COLUMN_NAME_ID
        );
        List<Categoria> categorias = new ArrayList<>();
        while (cursorConsulta.moveToNext()) {
            String id = cursorConsulta.getString(cursorConsulta.getColumnIndexOrThrow(FeedReaderContract.TablaCategoriasSuscritas.COLUMN_NAME_ID));
            String nombre = cursorConsulta.getString(cursorConsulta.getColumnIndexOrThrow(FeedReaderContract.TablaCategoriasSuscritas.COLUMN_NAME_NOMBRE));
            categorias.add(new Categoria(id, nombre, obtenerColorCategoria(context, id), obtenerImagenCategoria(context, id)));
        }
        cursorConsulta.close();
        db.close();
        return categorias.toArray(new Categoria[0]);
    }

    public static void guardarCategoriasAdministradasLocal(Context context, Categoria[] categorias) {
        FeedReaderDbHelper dbHelper = new FeedReaderDbHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        dbHelper.truncateTable(db, FeedReaderContract.TablaCategoriasAdministradas.TABLE_NAME);

        for (Categoria categoria : categorias) {
            ContentValues registro = new ContentValues();
            registro.put(FeedReaderContract.TablaCategoriasAdministradas.COLUMN_NAME_ID, categoria.key);
            registro.put(FeedReaderContract.TablaCategoriasAdministradas.COLUMN_NAME_NOMBRE, categoria.nombre);

            db.insert(FeedReaderContract.TablaCategoriasAdministradas.TABLE_NAME, null, registro);
        }
        db.close();
    }

    public static Categoria[] recuperarCategoriasAdministradasLocal(Context context) {
        FeedReaderDbHelper dbHelper = new FeedReaderDbHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String[] columnasARetornar = {
                FeedReaderContract.TablaCategoriasAdministradas.COLUMN_NAME_ID,
                FeedReaderContract.TablaCategoriasAdministradas.COLUMN_NAME_NOMBRE
        };
        Cursor cursorConsulta = db.query(
                FeedReaderContract.TablaCategoriasAdministradas.TABLE_NAME,
                columnasARetornar,
                null,
                null,
                null,
                null,
                FeedReaderContract.TablaCategoriasSuscritas.COLUMN_NAME_ID
        );
        List<Categoria> categorias = new ArrayList<>();
        while (cursorConsulta.moveToNext()) {
            String key = cursorConsulta.getString(cursorConsulta.getColumnIndexOrThrow(FeedReaderContract.TablaCategoriasAdministradas.COLUMN_NAME_ID));
            String nombre = cursorConsulta.getString(cursorConsulta.getColumnIndexOrThrow(FeedReaderContract.TablaCategoriasAdministradas.COLUMN_NAME_NOMBRE));
            categorias.add(new Categoria(key, nombre));
        }
        cursorConsulta.close();
        db.close();
        return categorias.toArray(new Categoria[0]);
    }

    public static void vaciarTablasCategorias(Context context){
        FeedReaderDbHelper dbHelper = new FeedReaderDbHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        dbHelper.truncateTable(db, FeedReaderContract.TablaCategoriasAdministradas.TABLE_NAME);
        dbHelper.truncateTable(db, FeedReaderContract.TablaCategoriasSuscritas.TABLE_NAME);
        db.close();
    }
}
