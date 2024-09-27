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

    // Nombre de las tablas
    private static final String TABLE_LOGIN = "Login";
    private static final String TABLE_REPORTES = "Reportes";

    // Columnas de la tabla Login
    private static final String COLUMN_ID_USUARIO = "Id_Usuario";
    private static final String COLUMN_USERNAME = "Usuario";
    private static final String COLUMN_PASSWORD = "Contraseña";

    // Columnas de la tabla Reportes
    private static final String COLUMN_ID_TICKET = "Id_ticket";
    private static final String COLUMN_FECHA_ASIGNACION = "Fecha_asignacion";
    private static final String COLUMN_FECHA_REPARACION = "Fecha_reparacion";
    private static final String COLUMN_COLONIA = "Colonia";
    private static final String COLUMN_TIPO_SUELO = "Tipo_suelo";
    private static final String COLUMN_DIRECCION = "Direccion";
    private static final String COLUMN_REPORTANTE = "Reportante";
    private static final String COLUMN_TELEFONO_REPORTANTE = "Telefono_reportante";
    private static final String COLUMN_REPARADOR = "Reparador";
    private static final String COLUMN_MATERIAL = "Material";
    private static final String COLUMN_IMAGEN_ANTES = "Imagen_antes";
    private static final String COLUMN_IMAGEN_DESPUES = "Imagen_despues";

    // Constructor
    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Habilitar soporte para claves foráneas
        db.execSQL("PRAGMA foreign_keys=ON;");

        // Crear la tabla Login
        String CREATE_LOGIN_TABLE = "CREATE TABLE " + TABLE_LOGIN + "("
                + COLUMN_ID_USUARIO + " INTEGER PRIMARY KEY, "
                + COLUMN_USERNAME + " TEXT, "
                + COLUMN_PASSWORD + " TEXT)";
        db.execSQL(CREATE_LOGIN_TABLE);



        // Crear la tabla Reportes con la clave foránea Id_Usuario
        String CREATE_REPORTES_TABLE = "CREATE TABLE " + TABLE_REPORTES + "("
                + COLUMN_ID_TICKET + " INTEGER PRIMARY KEY, "
                + COLUMN_ID_USUARIO + " INTEGER NOT NULL, "
                + COLUMN_FECHA_ASIGNACION + " TEXT NOT NULL, "
                + COLUMN_FECHA_REPARACION + " TEXT NOT NULL, "
                + COLUMN_COLONIA + " TEXT NOT NULL, "
                + COLUMN_TIPO_SUELO + " TEXT NOT NULL, "
                + COLUMN_DIRECCION + " TEXT NOT NULL, "
                + COLUMN_REPORTANTE + " TEXT NOT NULL, "
                + COLUMN_TELEFONO_REPORTANTE + " TEXT NOT NULL, "
                + COLUMN_REPARADOR + " TEXT NOT NULL, "
                + COLUMN_MATERIAL + " TEXT NOT NULL, "
                + COLUMN_IMAGEN_ANTES + " BLOB, "
                + COLUMN_IMAGEN_DESPUES + " BLOB, "
                + "FOREIGN KEY (" + COLUMN_ID_USUARIO + ") REFERENCES " + TABLE_LOGIN + "(" + COLUMN_ID_USUARIO + ")"
                + " ON DELETE CASCADE ON UPDATE CASCADE)";
        db.execSQL(CREATE_REPORTES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Eliminar las tablas si ya existen
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LOGIN);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_REPORTES);
        onCreate(db);
    }

    // Métodos para insertar, verificar, y manipular datos en la tabla Login

    public boolean insertarUsuario(String usuario, String clave) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USERNAME, usuario);
        values.put(COLUMN_PASSWORD, clave);
        long result = db.insert(TABLE_LOGIN, null, values);
        return result != -1; // Devuelve true si se inserta correctamente
    }

    public boolean verificarUsuario(String usuario, String clave) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_LOGIN,
                new String[]{COLUMN_ID_USUARIO},
                COLUMN_USERNAME + "=? AND " + COLUMN_PASSWORD + "=?",
                new String[]{usuario, clave},
                null, null, null);
        boolean existe = cursor.getCount() > 0;
        cursor.close();
        return existe; // Devuelve true si las credenciales son correctas
    }

    // Métodos para insertar reportes
    public boolean insertarReporte(int id_usuario, String fecha_asignacion, String fecha_reparacion, String colonia,
                                   String tipo_suelo, String direccion, String reportante, String telefono_reportante,
                                   String reparador, String material, byte[] imagen_antes, byte[] imagen_despues) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_ID_USUARIO, id_usuario);
        values.put(COLUMN_FECHA_ASIGNACION, fecha_asignacion);
        values.put(COLUMN_FECHA_REPARACION, fecha_reparacion);
        values.put(COLUMN_COLONIA, colonia);
        values.put(COLUMN_TIPO_SUELO, tipo_suelo);
        values.put(COLUMN_DIRECCION, direccion);
        values.put(COLUMN_REPORTANTE, reportante);
        values.put(COLUMN_TELEFONO_REPORTANTE, telefono_reportante);
        values.put(COLUMN_REPARADOR, reparador);
        values.put(COLUMN_MATERIAL, material);
        values.put(COLUMN_IMAGEN_ANTES, imagen_antes);
        values.put(COLUMN_IMAGEN_DESPUES, imagen_despues);
        long result = db.insert(TABLE_REPORTES, null, values);
        return result != -1; // Devuelve true si se inserta correctamente
    }

    // Métodos para obtener reportes de la base de datos
    public Cursor obtenerTodosLosReportes() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_REPORTES, null);
    }

    // Método para eliminar un reporte
    public boolean eliminarReporte(int id_ticket) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_REPORTES, COLUMN_ID_TICKET + "=?", new String[]{String.valueOf(id_ticket)}) > 0;
    }


}
