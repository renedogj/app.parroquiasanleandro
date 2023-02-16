package es.parroquiasanleandro;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Articulo {
    public String id;
    public String nombre;
    public String descripcion;
    public Double precio;
    public boolean mostrar;
    public int disponibilidad;
    public List<String> imagenes;
    public List<String> categorias;

    public Articulo() {
    }

    public static List<Articulo> JSONArrayToArticulos(JSONArray jsonArrayArticulos) {
        List<Articulo> articulos = new ArrayList<>();
        for (int i = 0; i < jsonArrayArticulos.length(); i++) {
            Articulo articuloAux = new Articulo();
            try {
                JSONObject jsonArticulo = jsonArrayArticulos.getJSONObject(i);
                articuloAux.id = jsonArticulo.getString("id");
                articuloAux.nombre = jsonArticulo.getString("nombre");
                articuloAux.descripcion = jsonArticulo.getString("descripcion");
                articuloAux.precio = jsonArticulo.getDouble("precio");
                //articuloAux.mostrar = jsonArticulo.getInt("mostrar") == 1;
                articuloAux.disponibilidad = jsonArticulo.getInt("disponibilidad");
                JSONArray jsonArrayImagenes = jsonArticulo.getJSONArray("imagenes");

                articuloAux.imagenes = new ArrayList<>();
                for (int j = 0; j < jsonArrayImagenes.length(); j++) {
                    articuloAux.imagenes.add(jsonArrayImagenes.getString(j));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            articulos.add(articuloAux);
        }
        return articulos;
    }

    public static Articulo JSONToArticulo(JSONObject jsonArticulo) {
        Articulo articuloAux = new Articulo();
        try {
            articuloAux.id = jsonArticulo.getString("id");
            articuloAux.nombre = jsonArticulo.getString("nombre");
            articuloAux.descripcion = jsonArticulo.getString("descripcion");
            articuloAux.precio = jsonArticulo.getDouble("precio");
            articuloAux.mostrar = jsonArticulo.getInt("mostrar") == 1;
            articuloAux.disponibilidad = jsonArticulo.getInt("disponibilidad");

            JSONArray jsonArrayImagenes = jsonArticulo.getJSONArray("imagenes");
            articuloAux.imagenes = new ArrayList<>();
            for (int j = 0; j < jsonArrayImagenes.length(); j++) {
                articuloAux.imagenes.add(jsonArrayImagenes.getString(j));
            }

            JSONArray jsonArrayCategorias = jsonArticulo.getJSONArray("categorias");
            articuloAux.categorias = new ArrayList<>();
            for (int j = 0; j < jsonArrayCategorias.length(); j++) {
                articuloAux.categorias.add(jsonArrayCategorias.getString(j));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return articuloAux;
    }
}
