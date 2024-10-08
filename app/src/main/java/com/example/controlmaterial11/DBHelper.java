package com.example.controlmaterial11;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "Reportes_material.db";
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_LOGIN = "Login";
    private static final String TABLE_REPORTES = "Reportes";

    private static final String COLUMN_ID_USUARIO = "Id_Usuario";
    private static final String COLUMN_USERNAME = "Usuario";
    private static final String COLUMN_PASSWORD = "Contraseña";

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

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("PRAGMA foreign_keys=ON;");

        String CREATE_LOGIN_TABLE = "CREATE TABLE " + TABLE_LOGIN + "("
                + COLUMN_ID_USUARIO + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_USERNAME + " TEXT, "
                + COLUMN_PASSWORD + " TEXT)";
        db.execSQL(CREATE_LOGIN_TABLE);

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
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LOGIN);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_REPORTES);
        onCreate(db);
    }

    private byte[] reducirImagen(Uri imageUri, Context context) throws IOException {
        Bitmap bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), imageUri);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        int calidad = 100; // Comenzar con la calidad máxima
        bitmap.compress(Bitmap.CompressFormat.JPEG, calidad, outputStream);

        // Obtener el tamaño en MB (1 MB = 1024 * 1024 bytes)
        double tamañoEnMB = outputStream.size() / (1024.0 * 1024.0);

        // Si la imagen es mayor a 20 MB, reducir la calidad progresivamente
        while (tamañoEnMB > 20 && calidad > 10) {
            outputStream.reset(); // Limpiar el buffer
            calidad -= 10; // Reducir calidad en incrementos de 10
            bitmap.compress(Bitmap.CompressFormat.JPEG, calidad, outputStream);
            tamañoEnMB = outputStream.size() / (1024.0 * 1024.0);
        }

        return outputStream.toByteArray();
    }


    public boolean insertarReporte(int id_usuario, String fecha_asignacion, String fecha_reparacion, String colonia,
                                   String tipo_suelo, String direccion, String reportante, String telefono_reportante,
                                   String reparador, String material, Uri imagenAntesUri, Uri imagenDespuesUri, Context context) {
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

        try {
            if (imagenAntesUri != null) {
                byte[] imagenAntes = reducirImagen(imagenAntesUri, context);
                values.put(COLUMN_IMAGEN_ANTES, imagenAntes);
            }
            if (imagenDespuesUri != null) {
                byte[] imagenDespues = reducirImagen(imagenDespuesUri, context);
                values.put(COLUMN_IMAGEN_DESPUES, imagenDespues);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        long result = db.insert(TABLE_REPORTES, null, values);
        // db.close(); // Cerrar la base de datos después de insertar <--- (comentado)
        return result != -1;
    }

    public boolean insertarUsuario(String username, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USERNAME, username);
        values.put(COLUMN_PASSWORD, password);

        long result = db.insert(TABLE_LOGIN, null, values);
        // db.close(); // Cerrar la base de datos después de usarla <--- (comentado)
        return result != -1;
    }

    public Cursor buscarReporte(int Id_ticket) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;

        try {
            String query = "SELECT Id_ticket, Fecha_asignacion, Fecha_reparacion, Colonia, Tipo_suelo, Direccion, Reportante, " +
                    "Telefono_reportante, Reparador, Material, Imagen_antes, Imagen_despues " +
                    "FROM " + TABLE_REPORTES + " WHERE Id_ticket = ?";
            cursor = db.rawQuery(query, new String[]{String.valueOf(Id_ticket)});
        } catch (Exception e) {
            e.printStackTrace();
        }

        return cursor; // Retorna el cursor sin cerrarlo
    }

    public boolean eliminarReporte(int id_ticket) {
        SQLiteDatabase db = this.getWritableDatabase();
        boolean result = db.delete(TABLE_REPORTES, COLUMN_ID_TICKET + "=?", new String[]{String.valueOf(id_ticket)}) > 0;
        // db.close(); // Cerrar la base de datos después de usarla <--- (comentado)
        return result;
    }

    public boolean verificarUsuario(String usuario, String clave) {
        SQLiteDatabase db = this.getReadableDatabase();
        boolean existeUsuario = false;
        Cursor cursor = null;

        try {
            String query = "SELECT * FROM " + TABLE_LOGIN + " WHERE " + COLUMN_USERNAME + " = ? AND " + COLUMN_PASSWORD + " = ?";
            cursor = db.rawQuery(query, new String[]{usuario, clave});
            existeUsuario = cursor.getCount() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close(); // Cerrar cursor <---
            }
            // db.close(); // Cerrar la base de datos después de usarla <--- (comentado)
        }

        return existeUsuario;
    }

    public List<Reporte> obtenerTodosLosReportes() {
        List<Reporte> reportes = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT Id_ticket, Colonia, Direccion FROM " + TABLE_REPORTES;
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                String idTicket = cursor.getString(cursor.getColumnIndex(COLUMN_ID_TICKET));
                String colonia = cursor.getString(cursor.getColumnIndex(COLUMN_COLONIA));
                String direccion = cursor.getString(cursor.getColumnIndex(COLUMN_DIRECCION));

                Reporte reporte = new Reporte(idTicket, colonia, direccion);
                reportes.add(reporte);
            } while (cursor.moveToNext());
        }

        cursor.close(); // Cerrar cursor <---
        // db.close(); // Cerrar la base de datos <--- (comentado)

        return reportes;
    }

    public int obtenerIdUsuario(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        int idUsuario = -1; // Valor predeterminado en caso de que no se encuentre el usuario
        Cursor cursor = null;

        try {
            String query = "SELECT " + COLUMN_ID_USUARIO + " FROM " + TABLE_LOGIN + " WHERE " + COLUMN_USERNAME + " = ?";
            cursor = db.rawQuery(query, new String[]{username});
            if (cursor.moveToFirst()) {
                idUsuario = cursor.getInt(cursor.getColumnIndex(COLUMN_ID_USUARIO));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close(); // Cerrar cursor <---
            }
            // db.close(); // Cerrar la base de datos <--- (comentado)
        }

        return idUsuario;
    }

    public boolean actualizarReporte(int id_ticket, String colonia, String direccion, String reportante,
                                     String telefonoReportante, String tipoSuelo, String reparador,
                                     String material, byte[] imagenAntes, byte[] imagenDespues) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("Colonia", colonia);
        values.put("Direccion", direccion);
        values.put("Reportante", reportante);
        values.put("Telefono_reportante", telefonoReportante);
        values.put("Tipo_suelo", tipoSuelo);
        values.put("Reparador", reparador);
        values.put("Material", material);
        values.put("Imagen_antes", imagenAntes);
        values.put("Imagen_despues", imagenDespues);

        // Actualizar la fila con el ID del ticket
        int rowsAffected = db.update("reportes", values, "Id_ticket = ?", new String[]{String.valueOf(id_ticket)});
        db.close(); //cerrar curor <---
        return rowsAffected > 0;  // Retorna true si se actualizaron filas
    }
}
