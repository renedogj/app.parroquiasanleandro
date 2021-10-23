package com.parroquiasanleandro.bbdd_SQLite;

import android.provider.BaseColumns;

public class FeedReaderContract {
    private FeedReaderContract() {}

    public static class TablaCategorias implements BaseColumns {
        public static final String TABLE_NAME = "Categorias";
        public static final String COLUMN_NAME_ID = "ID_categoria";
        public static final String COLUMN_NAME_Nombre = "Nombre_categoria";

        public static final String SQL_CREATE_ENTRIES =
                "CREATE TABLE " + TABLE_NAME + " (" +
                        COLUMN_NAME_ID + " TEXT," +
                        COLUMN_NAME_Nombre + " TEXT)";

        public static final String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS " + TABLE_NAME;
    }

}
