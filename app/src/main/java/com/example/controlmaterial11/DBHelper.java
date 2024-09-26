package com.example.controlmaterial11;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

    // Definir el nombre y versión de la base de datos
    private static final String DATABASE_NAME = "Reportes_material.db";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_NAME = "Login";

    // Columnas de la tabla usuarios
    private static final String COLUMN_ID = "Id_Usuario";
    private static final String COLUMN_USERNAME = "Usuario";
    private static final String COLUMN_PASSWORD = "Contraseña";

    // Constructor
    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Crear la tabla usuarios
        String CREATE_USER_TABLE = "CREATE TABLE " + TABLE_NAME + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY, "
                + COLUMN_USERNAME + " TEXT, "
                + COLUMN_PASSWORD + " TEXT)";
        db.execSQL(CREATE_USER_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Eliminar la tabla si ya existe
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    // Método para insertar un usuario en la base de datos
    public boolean insertarUsuario(String usuario, String clave) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USERNAME, usuario);
        values.put(COLUMN_PASSWORD, clave);
        long result = db.insert(TABLE_NAME, null, values);
        return result != -1; // Devuelve true si se inserta correctamente
    }

    // Método para verificar si un usuario existe
    public boolean verificarUsuario(String usuario, String clave) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME,
                new String[]{COLUMN_ID},
                COLUMN_USERNAME + "=? AND " + COLUMN_PASSWORD + "=?",
                new String[]{usuario, clave},
                null, null, null);
        boolean existe = cursor.getCount() > 0;
        cursor.close();
        return existe; // Devuelve true si las credenciales son correctas
    }
}