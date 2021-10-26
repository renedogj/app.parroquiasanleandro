package com.parroquiasanleandro;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.google.firebase.database.FirebaseDatabase;
import com.parroquiasanleandro.bbdd_SQLite.FeedReaderContract;
import com.parroquiasanleandro.bbdd_SQLite.FeedReaderDbHelper;

import java.util.ArrayList;
import java.util.List;

public class Categoria {
    public static final String CATEGORIAS = "Categorias";
    public static final String SUSCRIPCIONES = "suscripciones";

    public String key;
    public String nombre;
    int posicion;

    public Categoria() {
    }

    public Categoria(String key, String nombre) {
        this.key = key;
        this.nombre = nombre;
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

    public static String getKey(Categoria[] categorias,String nombre){
        for (Categoria categoria: categorias){
            if(categoria.nombre.equals(nombre)){
                return categoria.key;
            }
        }
        return null;
    }

    public void guardarCategoria(Context context, String uid) {
        guardarCategoriaLocal(context);
        FirebaseDatabase.getInstance().getReference().child(Usuario.USUARIOS).child(uid).child(SUSCRIPCIONES).child(key).setValue(nombre);
    }

    public void eliminarCategoria(Context context, String uid) {
        eliminarCategoriaLocal(context);
        FirebaseDatabase.getInstance().getReference().child(Usuario.USUARIOS).child(uid).child(SUSCRIPCIONES).child(key).setValue(null);
    }

    public void guardarCategoriaLocal(Context context) {
        FeedReaderDbHelper dbHelper = new FeedReaderDbHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        String consulta = "DELETE FROM " + FeedReaderContract.TablaCategorias.TABLE_NAME +
                " WHERE " + FeedReaderContract.TablaCategorias.COLUMN_NAME_ID + " = '" + key + "'" +
                " AND " + FeedReaderContract.TablaCategorias.COLUMN_NAME_Nombre + " = '" + nombre + "'";
        db.execSQL(consulta);

        ContentValues registro = new ContentValues();
        registro.put(FeedReaderContract.TablaCategorias.COLUMN_NAME_ID, key + "");
        registro.put(FeedReaderContract.TablaCategorias.COLUMN_NAME_Nombre, nombre + "");

        db.insert(FeedReaderContract.TablaCategorias.TABLE_NAME, null, registro);
        db.close();
    }

    public void eliminarCategoriaLocal(Context context) {
        FeedReaderDbHelper dbHelper = new FeedReaderDbHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String consulta = "DELETE FROM " + FeedReaderContract.TablaCategorias.TABLE_NAME +
                " WHERE " + FeedReaderContract.TablaCategorias.COLUMN_NAME_ID + " = '" + key + "'" +
                " AND " + FeedReaderContract.TablaCategorias.COLUMN_NAME_Nombre + " = '" + nombre + "'";
        db.execSQL(consulta);
        db.close();
    }

    public static void guardarCategoriasLocal(Context context, Categoria[] categorias) {
        FeedReaderDbHelper dbHelper = new FeedReaderDbHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        dbHelper.truncateTable(db, FeedReaderContract.TablaCategorias.TABLE_NAME);

        for (Categoria categoria : categorias) {
            ContentValues registro = new ContentValues();
            registro.put(FeedReaderContract.TablaCategorias.COLUMN_NAME_ID, categoria.key + "");
            registro.put(FeedReaderContract.TablaCategorias.COLUMN_NAME_Nombre, categoria.nombre + "");

            db.insert(FeedReaderContract.TablaCategorias.TABLE_NAME, null, registro);
        }
        db.close();
    }

    public static Categoria[] recuperarCategoriasLocal(Context context) {
        FeedReaderDbHelper dbHelper = new FeedReaderDbHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String[] columnasARetornar = {
                FeedReaderContract.TablaCategorias.COLUMN_NAME_ID,
                FeedReaderContract.TablaCategorias.COLUMN_NAME_Nombre
        };
        Cursor cursorConsulta = db.query(
                FeedReaderContract.TablaCategorias.TABLE_NAME,
                columnasARetornar,
                null,
                null,
                null,
                null,
                null
        );
        List<Categoria> categorias = new ArrayList<>();
        while (cursorConsulta.moveToNext()) {
            String key = cursorConsulta.getString(cursorConsulta.getColumnIndexOrThrow(FeedReaderContract.TablaCategorias.COLUMN_NAME_ID));
            String nombre = cursorConsulta.getString(cursorConsulta.getColumnIndexOrThrow(FeedReaderContract.TablaCategorias.COLUMN_NAME_Nombre));
            categorias.add(new Categoria(key, nombre));
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
            registro.put(FeedReaderContract.TablaCategoriasAdministradas.COLUMN_NAME_ID, categoria.key + "");
            registro.put(FeedReaderContract.TablaCategoriasAdministradas.COLUMN_NAME_Nombre, categoria.nombre + "");

            db.insert(FeedReaderContract.TablaCategoriasAdministradas.TABLE_NAME, null, registro);
        }
        db.close();
    }

    public static Categoria[] recuperarCategoriasAdministradasLocal(Context context) {
        FeedReaderDbHelper dbHelper = new FeedReaderDbHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String[] columnasARetornar = {
                FeedReaderContract.TablaCategoriasAdministradas.COLUMN_NAME_ID,
                FeedReaderContract.TablaCategoriasAdministradas.COLUMN_NAME_Nombre
        };
        Cursor cursorConsulta = db.query(
                FeedReaderContract.TablaCategoriasAdministradas.TABLE_NAME,
                columnasARetornar,
                null,
                null,
                null,
                null,
                null
        );
        List<Categoria> categorias = new ArrayList<>();
        while (cursorConsulta.moveToNext()) {
            String key = cursorConsulta.getString(cursorConsulta.getColumnIndexOrThrow(FeedReaderContract.TablaCategoriasAdministradas.COLUMN_NAME_ID));
            String nombre = cursorConsulta.getString(cursorConsulta.getColumnIndexOrThrow(FeedReaderContract.TablaCategoriasAdministradas.COLUMN_NAME_Nombre));
            categorias.add(new Categoria(key, nombre));
        }
        cursorConsulta.close();
        db.close();
        return categorias.toArray(new Categoria[0]);
    }
}
