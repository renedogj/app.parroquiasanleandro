package es.parroquiasanleandro.bbdd_SQLite;

import android.provider.BaseColumns;

public class FeedReaderContract {
    private FeedReaderContract() {}

    public static class TablaCategorias implements BaseColumns {
        public static final String TABLE_NAME = "Categorias";
        public static final String COLUMN_NAME_ID = "id";
        public static final String COLUMN_NAME_NOMBRE = "nombre";
        public static final String COLUMN_NAME_COLOR = "color";
        public static final String COLUMN_NAME_IMAGEN = "imagen";

        public static final String SQL_CREATE_ENTRIES =
                "CREATE TABLE " + TABLE_NAME + " (" +
                        COLUMN_NAME_ID + " TEXT," +
                        COLUMN_NAME_NOMBRE + " TEXT," +
                        COLUMN_NAME_COLOR + " TEXT," +
                        COLUMN_NAME_IMAGEN + " TEXT)";

        public static final String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS " + TABLE_NAME;
    }

    public static class TablaCategoriasSuscritas implements BaseColumns {
        public static final String TABLE_NAME = "CategoriasSuscritas";
        public static final String COLUMN_NAME_ID = "id";
        public static final String COLUMN_NAME_NOMBRE = "nombre";

        public static final String SQL_CREATE_ENTRIES =
                "CREATE TABLE " + TABLE_NAME + " (" +
                        COLUMN_NAME_ID + " TEXT," +
                        COLUMN_NAME_NOMBRE + " TEXT)";

        public static final String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS " + TABLE_NAME;
    }

    public static class TablaCategoriasAdministradas implements BaseColumns {
        public static final String TABLE_NAME = "CategoriasAdministradas";
        public static final String COLUMN_NAME_ID = "id";
        public static final String COLUMN_NAME_NOMBRE = "nombre";

        public static final String SQL_CREATE_ENTRIES =
                "CREATE TABLE " + TABLE_NAME + " (" +
                        COLUMN_NAME_ID + " TEXT," +
                        COLUMN_NAME_NOMBRE + " TEXT)";

        public static final String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS " + TABLE_NAME;
    }
}
