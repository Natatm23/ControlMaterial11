package com.example.controlmaterial11;

import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.controlmaterial11.databinding.ActivityGenerarreporteBinding;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Calendar;

public class GenerarreporteActivity extends DrawerBaseActivity {
    ActivityGenerarreporteBinding generarreporteBinding;

    private static final int PICK_IMAGE = 1;
    private Uri imageUriAntes, imageUriDespues;

    private EditText txt_ticket, txt_fecha_asignacion, txt_fecha_reparacion, txt_colonia, direccion,
            txt_tipo_suelo, txt_reportante, txt_tel_reportante, txt_reparador, txt_material;
    private ImageView imageViewEvidencia_antes, imageViewEvidencia_despues;
    private Button btn_seleccionar_imagen_antes, btn_seleccionar_imagen_despues, btn_generar_reporte;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        generarreporteBinding = generarreporteBinding.inflate(getLayoutInflater());
        setContentView(generarreporteBinding.getRoot());

        // Inicializar campos
        txt_ticket = findViewById(R.id.txt_ticket);
        txt_fecha_asignacion = findViewById(R.id.txt_fecha_asignacion);
        txt_fecha_reparacion = findViewById(R.id.txt_fecha_reparacion);
        txt_colonia = findViewById(R.id.txt_colonia);
        direccion = findViewById(R.id.direccion);
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
        String direccionText = direccion.getText().toString();
        String tipoSuelo = txt_tipo_suelo.getText().toString();
        String reportante = txt_reportante.getText().toString();
        String telReportante = txt_tel_reportante.getText().toString();
        String reparador = txt_reparador.getText().toString();
        String material = txt_material.getText().toString();

        if (ticket.isEmpty() || fechaAsignacion.isEmpty() || fechaReparacion.isEmpty() ||
                colonia.isEmpty() || direccionText.isEmpty() || tipoSuelo.isEmpty() ||
                reportante.isEmpty() || telReportante.isEmpty() || reparador.isEmpty() ||
                material.isEmpty() || imageUriAntes == null || imageUriDespues == null) {
            Toast.makeText(this, "Por favor, complete todos los campos.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Convertir imágenes a bytes
        byte[] imagenAntes = convertImageToByte(imageUriAntes);
        byte[] imagenDespues = convertImageToByte(imageUriDespues);

        // Guardar el reporte en la base de datos
        insertarReporte(ticket, fechaAsignacion, fechaReparacion,
                colonia, direccionText, tipoSuelo,
                reportante, telReportante, reparador,
                material, imagenAntes, imagenDespues);
    }

    private void insertarReporte(String ticket, String fechaAsignacion, String fechaReparacion,
                                 String colonia, String direccionText, String tipoSuelo,
                                 String reportante, String telReportante, String reparador,
                                 String material, byte[] imagenAntes, byte[] imagenDespues) {
        // Crear una instancia de tu clase DBHelper
        DBHelper dbHelper = new DBHelper(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase(); // Obtener una base de datos en modo escritura

        // Iniciar una transacción
        db.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            // Asignar los valores a ContentValues
            values.put("ticket", ticket);
            values.put("fecha_asignacion", fechaAsignacion);
            values.put("fecha_reparacion", fechaReparacion);
            values.put("colonia", colonia);
            values.put("direccion", direccionText);
            values.put("tipo_suelo", tipoSuelo);
            values.put("reportante", reportante);
            values.put("tel_reportante", telReportante);
            values.put("reparador", reparador);
            values.put("material", material);
            values.put("imagen_antes", imagenAntes);
            values.put("imagen_despues", imagenDespues);

            // Insertar los datos en la tabla reportes
            db.insert("reportes", null, values);

            // Confirmar la transacción
            db.setTransactionSuccessful();
            Toast.makeText(this, "Reporte guardado con éxito en la base de datos.", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Log.e("GenerarreporteActivity", "Error al guardar el reporte: " + e.getMessage());
            Toast.makeText(this, "Error al guardar el reporte: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        } finally {
            // Finalizar la transacción
            db.endTransaction();
            // Cerrar la base de datos
            db.close();
        }
    }

    private byte[] convertImageToByte(Uri imageUri) {
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            return stream.toByteArray();
        } catch (IOException e) {
            Log.e("GenerarreporteActivity", "Error al convertir la imagen a byte: " + e.getMessage());
            return null;
        }
    }
}
