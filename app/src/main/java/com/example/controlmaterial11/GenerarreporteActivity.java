package com.example.controlmaterial11;

import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.controlmaterial11.databinding.ActivityGenerarreporteBinding;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Calendar;

public class GenerarreporteActivity extends DrawerBaseActivity {
    ActivityGenerarreporteBinding generarreporteBinding;

    private static final int PICK_IMAGE = 1;
    private Uri imageUriAntes, imageUriDespues;

    private EditText txt_ticket, txt_fecha_asignacion, txt_fecha_reparacion, txt_colonia,
            txt_direccion, txt_tipo_suelo, txt_reportante, txt_tel_reportante, txt_reparador, txt_material;
    private ImageView imageViewEvidencia_antes, imageViewEvidencia_despues;
    private Button btn_seleccionar_imagen_antes, btn_seleccionar_imagen_despues, btn_generar_reporte;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        generarreporteBinding = ActivityGenerarreporteBinding.inflate(getLayoutInflater());
        setContentView(generarreporteBinding.getRoot());

        // Inicializar campos
        txt_ticket = findViewById(R.id.txt_ticket);
        txt_fecha_asignacion = findViewById(R.id.txt_fecha_asignacion);
        txt_fecha_reparacion = findViewById(R.id.txt_fecha_reparacion);
        txt_colonia = findViewById(R.id.txt_colonia);
        txt_direccion = findViewById(R.id.direccion);
        txt_tipo_suelo = findViewById(R.id.txt_tipo_suelo);
        txt_reportante = findViewById(R.id.txt_reportante);
        txt_tel_reportante = findViewById(R.id.txt_tel_reportante);
        txt_reparador = findViewById(R.id.txt_reparador);
        txt_material = findViewById(R.id.txt_material);
        imageViewEvidencia_antes = findViewById(R.id.imageViewEvidencia_antes);
        imageViewEvidencia_despues = findViewById(R.id.imageViewEvidencia_despues);
        btn_seleccionar_imagen_antes = findViewById(R.id.btn_seleccionar_imagen_antes);
        btn_seleccionar_imagen_despues = findViewById(R.id.btn_seleccionar_imagen_despues);
        btn_generar_reporte = findViewById(R.id.btn_generar_reporte);

        // Configurar DatePickers
        setupDatePicker(txt_fecha_asignacion);
        setupDatePicker(txt_fecha_reparacion);

        // Configurar botones
        btn_seleccionar_imagen_antes.setOnClickListener(view -> selectImage(1));
        btn_seleccionar_imagen_despues.setOnClickListener(view -> selectImage(2));
        btn_generar_reporte.setOnClickListener(view -> generateReport());
    }

    private void setupDatePicker(EditText editText) {
        editText.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    GenerarreporteActivity.this,
                    (view, selectedYear, selectedMonth, selectedDay) -> {
                        String date = selectedDay + "/" + (selectedMonth + 1) + "/" + selectedYear;
                        editText.setText(date);
                    }, year, month, day);
            datePickerDialog.show();
        });
    }

    private void selectImage(int requestCode) {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, requestCode);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null) {
            Uri selectedImageUri = data.getData();

            // Verificar si la URI es nula
            if (selectedImageUri == null) {
                Toast.makeText(this, "No se pudo obtener la imagen.", Toast.LENGTH_SHORT).show();
                return;
            }

            // Cargar y mostrar la imagen
            if (requestCode == 1) {
                imageUriAntes = selectedImageUri;
                imageViewEvidencia_antes.setImageURI(imageUriAntes);
            } else if (requestCode == 2) {
                imageUriDespues = selectedImageUri;
                imageViewEvidencia_despues.setImageURI(imageUriDespues);
            }
        }
    }

    private void generateReport() {
        String ticket = txt_ticket.getText().toString();
        String fechaAsignacion = txt_fecha_asignacion.getText().toString();
        String fechaReparacion = txt_fecha_reparacion.getText().toString();
        String colonia = txt_colonia.getText().toString();
        String direccionText = txt_direccion.getText().toString();
        String tipoSuelo = txt_tipo_suelo.getText().toString();
        String reportante = txt_reportante.getText().toString();
        String telReportante = txt_tel_reportante.getText().toString();
        String reparador = txt_reparador.getText().toString();
        String material = txt_material.getText().toString();

        if (ticket.isEmpty() || fechaAsignacion.isEmpty() || fechaReparacion.isEmpty() ||
                colonia.isEmpty() || direccionText.isEmpty() || tipoSuelo.isEmpty() ||
                reportante.isEmpty() || telReportante.isEmpty() || reparador.isEmpty() ||
                material.isEmpty()) {
            Toast.makeText(this, "Por favor, complete todos los campos.", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            // Convertir las imágenes a byte arrays
            byte[] imagenAntesBytes = imageUriAntes != null ? convertImageToBytes(imageUriAntes) : null;
            byte[] imagenDespuesBytes = imageUriDespues != null ? convertImageToBytes(imageUriDespues) : null;

            // Verificar si alguna de las imágenes excedió el tamaño permitido
            if (imagenAntesBytes == null && imageUriAntes != null) {
                Toast.makeText(this, "La imagen 'antes' es demasiado grande.", Toast.LENGTH_SHORT).show();
                return;
            }
            if (imagenDespuesBytes == null && imageUriDespues != null) {
                Toast.makeText(this, "La imagen 'después' es demasiado grande.", Toast.LENGTH_SHORT).show();
                return;
            }

            // Llamar al método para insertar el reporte
            boolean result = insertarReporte(ticket, fechaAsignacion, fechaReparacion, colonia,
                    direccionText, tipoSuelo, reportante, telReportante, reparador, material,
                    imagenAntesBytes, imagenDespuesBytes);

            if (result) {
                Toast.makeText(this, "Reporte generado exitosamente", Toast.LENGTH_SHORT).show();
                limpiarCampos();  // Llamar al método para limpiar los campos
            } else {
                Toast.makeText(this, "Error al generar el reporte", Toast.LENGTH_SHORT).show();
            }
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error al convertir las imágenes", Toast.LENGTH_SHORT).show();
        }
    }


    private void limpiarCampos() {
        txt_ticket.setText("");
        txt_fecha_asignacion.setText("");
        txt_fecha_reparacion.setText("");
        txt_colonia.setText("");
        txt_direccion.setText("");
        txt_tipo_suelo.setText("");
        txt_reportante.setText("");
        txt_tel_reportante.setText("");
        txt_reparador.setText("");
        txt_material.setText("");

        imageViewEvidencia_antes.setImageURI(null);
        imageViewEvidencia_despues.setImageURI(null);

        imageUriAntes = null;
        imageUriDespues = null;
    }

    private byte[] convertImageToBytes(Uri imageUri) throws IOException {
        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();

        // Comprimir la imagen inicialmente
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] imageBytes = stream.toByteArray();

        // Verificar si la imagen supera los 20 MB
        if (imageBytes.length > 20 * 1024 * 1024) {
            // Mostrar advertencia si la imagen es mayor al límite permitido
            Toast.makeText(this, "La imagen excede el tamaño máximo permitido de 20MB. Por favor, seleccione una imagen más pequeña.", Toast.LENGTH_LONG).show();
            return null; // Devolver null para evitar procesar una imagen demasiado grande
        }

        // Si la imagen es aceptable, devolver el array de bytes
        return imageBytes;
    }


    private boolean insertarReporte(String ticket, String fechaAsignacion, String fechaReparacion,
                                    String colonia, String direccionText, String tipoSuelo,
                                    String reportante, String telReportante, String reparador,
                                    String material, byte[] imagenAntesBytes, byte[] imagenDespuesBytes) {
        DBHelper dbHelper = new DBHelper(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int id_usuario = LoginActivity.idUsuario; // Asegúrate de que este id se esté pasando correctamente

        // Verificar si el Id_ticket ya existe
        String query = "SELECT COUNT(*) FROM reportes WHERE Id_ticket = ?";
        Cursor cursor = db.rawQuery(query, new String[]{ticket});
        cursor.moveToFirst();
        int count = cursor.getInt(0);
        cursor.close();

        if (count > 0) {
            Toast.makeText(this, "El ticket ya existe en la base de datos", Toast.LENGTH_SHORT).show();
            return false;
        }

        ContentValues values = new ContentValues();
        values.put("Id_ticket", ticket);
        values.put("Fecha_asignacion", fechaAsignacion);
        values.put("Fecha_reparacion", fechaReparacion);
        values.put("Colonia", colonia);
        values.put("Direccion", direccionText);
        values.put("Tipo_suelo", tipoSuelo);
        values.put("Reportante", reportante);
        values.put("Telefono_reportante", telReportante);
        values.put("Reparador", reparador);
        values.put("Material", material);
        values.put("Imagen_antes", imagenAntesBytes);
        values.put("Imagen_despues", imagenDespuesBytes);
        values.put("id_usuario", id_usuario); // Asegúrate de que el id_usuario se esté pasando correctamente

        long newRowId = db.insert("reportes", null, values);
        db.close();

        return newRowId != -1; // Retorna true si la inserción fue exitosa
    }
}
//holaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaeserga