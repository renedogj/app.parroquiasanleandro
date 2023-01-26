package es.parroquiasanleandro;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
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
    public static final String TEXTO = "texto";
    public static final String PRIVADO = "privado";

    public String id;
    public String nombre;
    public String color;
    public String imagen;
    public String texto;
    public boolean privado;

    public Grupo() {
    }

    public Grupo(String id, String nombre) {
        this.id = id;
        this.nombre = nombre;
    }

    public Grupo(String id, String nombre, String color, String imagen) {
        this.id = id;
        this.nombre = nombre;
        this.color = color;
        this.imagen = imagen;
    }

    public Grupo(String id, String nombre, String color, String imagen, String texto, boolean privado) {
        this.id = id;
        this.nombre = nombre;
        this.color = color;
        this.imagen = imagen;
        this.texto = texto;
        this.privado = privado;
    }

    public String getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public static Grupo[] convertirGrupos(JSONArray jsonArrayGrupos) {
        JSONObject jsonObject;
        List<Grupo> grupos = new ArrayList<>();
        for (int i = 0; i < jsonArrayGrupos.length(); i++) {
            try {
                jsonObject = jsonArrayGrupos.getJSONObject(i);
                Grupo grupo = new Grupo(
                        jsonObject.getString(Grupo.ID),
                        jsonObject.getString(Grupo.NOMBRE),
                        jsonObject.getString(Grupo.COLOR),
                        jsonObject.getString(Grupo.IMAGEN)
                );
                grupos.add(grupo);

            } catch (JSONException e) {
                e.printStackTrace();
                Log.e("ERROR JSON", e.getMessage());
            }
        }
        return grupos.toArray(new Grupo[0]);
    }

    public static Grupo convertirGrupo(JSONObject jsonObject) {
        try {
            return new Grupo(
                    jsonObject.getString(Grupo.ID),
                    jsonObject.getString(Grupo.NOMBRE),
                    jsonObject.getString(Grupo.COLOR),
                    jsonObject.getString(Grupo.IMAGEN)
            );
        } catch (JSONException e) {
            e.printStackTrace();
            return new Grupo();
        }
    }

    public static String[] getNombreGrupos(Grupo[] grupos) {
        List<String> nombres = new ArrayList<>();
        for (Grupo grupo : grupos) {
            nombres.add(grupo.nombre);
        }
        return nombres.toArray(new String[0]);
    }

    public static String[] getIdsGrupos(Grupo[] grupos) {
        List<String> keys = new ArrayList<>();
        for (Grupo grupo : grupos) {
            keys.add(grupo.id);
        }
        return keys.toArray(new String[0]);
    }

    public static String getIndex(Grupo[] grupos, String nombre) {
        for (Grupo grupo : grupos) {
            if (grupo.nombre.equals(nombre)) {
                return grupo.id;
            }
        }
        return null;
    }

    public boolean isGrupoGuardado(Usuario usuario) {
        for (Grupo grupoAux : usuario.getGruposSeguidos()) {
            if (grupoAux.id.equals(id) && grupoAux.nombre.equals(nombre)) {
                return true;
            }
        }
        return false;
    }

    public boolean existenSubniveles(List<Grupo> grupos) {
        for (Grupo grupo : grupos) {
            if (grupo.id.length() == id.length() + 1) {
                if (grupo.id.startsWith(id)) {
                    return true;
                }
            }
        }
        return false;
    }

    public void chekGruposPadre(Context context, List<Grupo> grupos, Usuario usuario) {
        List<String> gruposId = new ArrayList<>();
        for (int i = 1; i <= id.length(); i++) {
            if (!gruposId.contains(id.substring(0, i))) {
                gruposId.add(id.substring(0, i));
            }
        }
        for (Grupo grupoAux : grupos) {
            if (gruposId.contains(grupoAux.id)) {
                if (!grupoAux.id.equals(id) && !grupoAux.isGrupoGuardado(usuario)) {
                    grupoAux.seguirGrupo(context, usuario.getId());
                    usuario.addGrupo(grupoAux);
                }
            }
        }
    }

    /**
     * Suscribe al usuario a un grupo en la BBDD y en local
     */
    public void seguirGrupo(Context context, String idUsuario) {
        modificarServidorSeguirEliminarGrupo(context, idUsuario, false);
        guardarGrupoSeguidoEnLocal(context);
    }

    /**
     * Elimina la suscripcion del usuario a un grupo en la BBDD y en local
     */
    public void eliminarGrupoSeguido(Context context, String idUsuario) {
        modificarServidorSeguirEliminarGrupo(context, idUsuario, true);
        eliminarGrupoSeguidoDeLocal(context);
    }

    /**
     * Modifica en el servidor el estado de siguiendo o no del usuario sobre el grupo
     * (@param siguiendo == true) el usuario ya sigue el grupo y se elimina de sus seguidos
     * (@param siguiendo == false) el usuario no sigue el grupo y lo añade a sus seguidos
     */
    public void modificarServidorSeguirEliminarGrupo(Context context, String idUsuario, boolean siguiendo) {
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(new StringRequest(Request.Method.POST, Url.modificarSeguirEliminarSeguido, result -> {
            try {
                JSONObject jsonResult = new JSONObject(result);
                if (jsonResult.getBoolean("error")) {
                    if (siguiendo) {
                        Toast.makeText(context, "Se ha producido un error al dejar de seguir la información del grupo", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(context, "Se ha producido un error al seguir la información del grupo", Toast.LENGTH_SHORT).show();
                    }
                }
            } catch (JSONException e) {
                if (siguiendo) {
                    Toast.makeText(context, "Se ha producido un error al dejar de seguir la información del grupo", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, "Se ha producido un error al seguir la información del grupo", Toast.LENGTH_SHORT).show();
                }
                e.printStackTrace();
            }
        }, error -> {
            Toast.makeText(context, "Se ha producido un error al conectar con el servidor", Toast.LENGTH_SHORT).show();
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> parametros = new HashMap<>();
                parametros.put("siguiendo", siguiendo + "");
                parametros.put("idUsuario", idUsuario);
                parametros.put("idGrupo", id);
                return parametros;
            }
        });
    }

    public static void actualizarGruposServidorToLocal(Context context) {
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(new JsonArrayRequest(Url.obtenerGrupos, jsonArrayResult -> {
            guardarGruposEnLocal(context, jsonArrayResult);
        }, error -> {
            Toast.makeText(context, "Se ha producido un error al conectar con el servidor", Toast.LENGTH_SHORT).show();
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return super.getParams();
            }
        });
    }

    public static void guardarGruposEnLocal(Context context, JSONArray jsonArray) {
        FeedReaderDbHelper dbHelper = new FeedReaderDbHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        dbHelper.truncateTable(db, FeedReaderContract.TablaGrupos.TABLE_NAME);

        JSONObject jsonObject;
        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                jsonObject = jsonArray.getJSONObject(i);
                ContentValues registro = new ContentValues();
                registro.put(FeedReaderContract.TablaGrupos.COLUMN_NAME_ID, jsonObject.getString(Grupo.ID));
                registro.put(FeedReaderContract.TablaGrupos.COLUMN_NAME_NOMBRE, jsonObject.getString(Grupo.NOMBRE));
                registro.put(FeedReaderContract.TablaGrupos.COLUMN_NAME_COLOR, jsonObject.getString(Grupo.COLOR));
                registro.put(FeedReaderContract.TablaGrupos.COLUMN_NAME_IMAGEN, jsonObject.getString(Grupo.IMAGEN));
                registro.put(FeedReaderContract.TablaGrupos.COLUMN_NAME_TEXTO, jsonObject.getString(Grupo.TEXTO));
                registro.put(FeedReaderContract.TablaGrupos.COLUMN_NAME_PRIVADO, jsonObject.getString(Grupo.PRIVADO));

                db.insert(FeedReaderContract.TablaGrupos.TABLE_NAME, null, registro);
            } catch (JSONException e) {
                Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
        db.close();
    }

    public static List<Grupo> recuperarGruposDeLocal(Context context) {
        FeedReaderDbHelper dbHelper = new FeedReaderDbHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String[] columnasARetornar = {
                FeedReaderContract.TablaGrupos.COLUMN_NAME_ID,
                FeedReaderContract.TablaGrupos.COLUMN_NAME_NOMBRE,
                FeedReaderContract.TablaGrupos.COLUMN_NAME_COLOR,
                FeedReaderContract.TablaGrupos.COLUMN_NAME_IMAGEN,
                FeedReaderContract.TablaGrupos.COLUMN_NAME_TEXTO,
                FeedReaderContract.TablaGrupos.COLUMN_NAME_PRIVADO
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
            String texto = cursorConsulta.getString(cursorConsulta.getColumnIndexOrThrow(FeedReaderContract.TablaGrupos.COLUMN_NAME_TEXTO));
            boolean privado = (cursorConsulta.getInt(cursorConsulta.getColumnIndexOrThrow(FeedReaderContract.TablaGrupos.COLUMN_NAME_PRIVADO)) == 1);
            grupos.add(new Grupo(key, nombre, color, imagen, texto, privado));
        }
        cursorConsulta.close();
        db.close();
        return grupos;
    }

    public static Grupo recuperarGrupoDeLocal(Context context, String id) {
        FeedReaderDbHelper dbHelper = new FeedReaderDbHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String[] columnasARetornar = {
                FeedReaderContract.TablaGrupos.COLUMN_NAME_ID,
                FeedReaderContract.TablaGrupos.COLUMN_NAME_NOMBRE,
                FeedReaderContract.TablaGrupos.COLUMN_NAME_COLOR,
                FeedReaderContract.TablaGrupos.COLUMN_NAME_IMAGEN,
                FeedReaderContract.TablaGrupos.COLUMN_NAME_TEXTO,
                FeedReaderContract.TablaGrupos.COLUMN_NAME_PRIVADO
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
        Grupo grupo = new Grupo();
        while (cursorConsulta.moveToNext()) {
            grupo.id = cursorConsulta.getString(cursorConsulta.getColumnIndexOrThrow(FeedReaderContract.TablaGrupos.COLUMN_NAME_ID));
            grupo.nombre = cursorConsulta.getString(cursorConsulta.getColumnIndexOrThrow(FeedReaderContract.TablaGrupos.COLUMN_NAME_NOMBRE));
            grupo.color = cursorConsulta.getString(cursorConsulta.getColumnIndexOrThrow(FeedReaderContract.TablaGrupos.COLUMN_NAME_COLOR));
            grupo.imagen = cursorConsulta.getString(cursorConsulta.getColumnIndexOrThrow(FeedReaderContract.TablaGrupos.COLUMN_NAME_IMAGEN));
            grupo.texto = cursorConsulta.getString(cursorConsulta.getColumnIndexOrThrow(FeedReaderContract.TablaGrupos.COLUMN_NAME_TEXTO));
            grupo.privado = (cursorConsulta.getInt(cursorConsulta.getColumnIndexOrThrow(FeedReaderContract.TablaGrupos.COLUMN_NAME_PRIVADO)) == 1);
        }
        cursorConsulta.close();
        db.close();
        return grupo;
    }

    public static String obtenerColorGrupo(Context context, String id) {
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

    public static String obtenerImagenGrupo(Context context, String id) {
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

    public void guardarGrupoSeguidoEnLocal(Context context) {
        FeedReaderDbHelper dbHelper = new FeedReaderDbHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        String consulta = "DELETE FROM " + FeedReaderContract.TablaGruposSuscritos.TABLE_NAME +
                " WHERE " + FeedReaderContract.TablaGruposSuscritos.COLUMN_NAME_ID + " = '" + id + "'" +
                " AND " + FeedReaderContract.TablaGruposSuscritos.COLUMN_NAME_NOMBRE + " = '" + nombre + "'";
        db.execSQL(consulta);

        ContentValues registro = new ContentValues();
        registro.put(FeedReaderContract.TablaGruposSuscritos.COLUMN_NAME_ID, id + "");
        registro.put(FeedReaderContract.TablaGruposSuscritos.COLUMN_NAME_NOMBRE, nombre + "");

        db.insert(FeedReaderContract.TablaGruposSuscritos.TABLE_NAME, null, registro);
        db.close();
    }

    public void eliminarGrupoSeguidoDeLocal(Context context) {
        FeedReaderDbHelper dbHelper = new FeedReaderDbHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String consulta = "DELETE FROM " + FeedReaderContract.TablaGruposSuscritos.TABLE_NAME +
                " WHERE " + FeedReaderContract.TablaGruposSuscritos.COLUMN_NAME_ID + " = '" + id + "'" +
                " AND " + FeedReaderContract.TablaGruposSuscritos.COLUMN_NAME_NOMBRE + " = '" + nombre + "'";
        db.execSQL(consulta);
        db.close();
    }

    public static void guardarGruposSeguidosEnLocal(Context context, Grupo[] grupos) {
        FeedReaderDbHelper dbHelper = new FeedReaderDbHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        dbHelper.truncateTable(db, FeedReaderContract.TablaGruposSuscritos.TABLE_NAME);

        for (Grupo grupo : grupos) {
            ContentValues registro = new ContentValues();
            registro.put(FeedReaderContract.TablaGruposSuscritos.COLUMN_NAME_ID, grupo.id);
            registro.put(FeedReaderContract.TablaGruposSuscritos.COLUMN_NAME_NOMBRE, grupo.nombre);

            db.insert(FeedReaderContract.TablaGruposSuscritos.TABLE_NAME, null, registro);
        }
        db.close();
    }

    public static Grupo[] recuperarGruposSeguidosDeLocal(Context context) {
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

    public static void guardarGruposAdministradosEnLocal(Context context, Grupo[] grupos) {
        FeedReaderDbHelper dbHelper = new FeedReaderDbHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        dbHelper.truncateTable(db, FeedReaderContract.TablaGruposAdministrados.TABLE_NAME);

        for (Grupo grupo : grupos) {
            ContentValues registro = new ContentValues();
            registro.put(FeedReaderContract.TablaGruposAdministrados.COLUMN_NAME_ID, grupo.id);
            registro.put(FeedReaderContract.TablaGruposAdministrados.COLUMN_NAME_NOMBRE, grupo.nombre);

            db.insert(FeedReaderContract.TablaGruposAdministrados.TABLE_NAME, null, registro);
        }
        db.close();
    }

    public static Grupo[] recuperarGruposAdministradosDeLocal(Context context) {
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

    public static void vaciarTablasGruposSeguidosYAdministrados(Context context) {
        FeedReaderDbHelper dbHelper = new FeedReaderDbHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        dbHelper.truncateTable(db, FeedReaderContract.TablaGruposSuscritos.TABLE_NAME);
        dbHelper.truncateTable(db, FeedReaderContract.TablaGruposAdministrados.TABLE_NAME);
        db.close();
    }
}
