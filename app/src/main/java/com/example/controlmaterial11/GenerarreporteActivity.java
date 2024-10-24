package com.example.controlmaterial11;

import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;
import android.content.Context;


import com.example.controlmaterial11.databinding.ActivityGenerarreporteBinding;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.List;

public class GenerarreporteActivity extends DrawerBaseActivity {
    ActivityGenerarreporteBinding generarreporteBinding;

    private static final int PICK_IMAGE = 1;
    private Uri imageUriAntes, imageUriDespues;

    private EditText txt_ticket, txt_fecha_asignacion, txt_fecha_reparacion, txt_colonia,
            txt_direccion, txt_tipo_suelo, txt_reportante, txt_tel_reportante, txt_reparador, txt_material;
    private Spinner spinnerDepartamento;
    private ImageView imageViewEvidencia_antes, imageViewEvidencia_despues;
    private Button btn_seleccionar_imagen_antes, btn_seleccionar_imagen_despues, btn_generar_reporte;
    private DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        generarreporteBinding = ActivityGenerarreporteBinding.inflate(getLayoutInflater());
        setContentView(generarreporteBinding.getRoot());

        // Inicializar el DBHelper para interactuar con la base de datos
        dbHelper = new DBHelper(this);

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
        spinnerDepartamento = findViewById(R.id.spinner);

        // Obtener los departamentos de la base de datos
        List<String> departamentos = dbHelper.getDepartamentos();

        // Crear un adaptador para el spinner usando el diseño personalizado
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                R.layout.spinner_item, // Usar el archivo de diseño del spinner
                departamentos
        );

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDepartamento.setAdapter(adapter);

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
        String departamentoSeleccionado = spinnerDepartamento.getSelectedItem().toString(); // Obtener el departamento seleccionado

        if (ticket.isEmpty() || fechaAsignacion.isEmpty() || fechaReparacion.isEmpty() ||
                colonia.isEmpty() || direccionText.isEmpty() || tipoSuelo.isEmpty() ||
                reportante.isEmpty() || telReportante.isEmpty() || reparador.isEmpty() ||
                material.isEmpty()) {
            Toast.makeText(this, "Por favor, complete todos los campos.", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            // Convertir las imágenes a byte arrays
            byte[] imagenAntesBytes = imageUriAntes != null ? reducirImagen(imageUriAntes, this) : null;
            byte[] imagenDespuesBytes = imageUriDespues != null ? reducirImagen(imageUriDespues, this) : null;

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
            boolean result = insertarReporte(this, ticket, fechaAsignacion, fechaReparacion, colonia,
                    direccionText, tipoSuelo, reportante, telReportante, reparador, material,
                    departamentoSeleccionado, imagenAntesBytes, imagenDespuesBytes);


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

    private byte[] reducirImagen(Uri imageUri, Context context) throws IOException {
        // Obtener el bitmap original
        Bitmap bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), imageUri);

        // Escalar el bitmap a un tamaño más pequeño
        int nuevoAncho = 900; // Cambia a un tamaño deseado
        int nuevoAlto = 900; // Cambia a un tamaño deseado
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(bitmap, nuevoAncho, nuevoAlto, false);

        // Convertir la imagen a byte array
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        // Comprimir a un 50% de calidad para reducir aún más el tamaño
        resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 50, stream);
        return stream.toByteArray();
    }

    private boolean insertarReporte(Context context, String id_ticket, String fecha_asignacion, String fecha_reparacion,
                                    String colonia, String direccion, String tipo_suelo, String reportante,
                                    String telefono_reportante, String reparador, String material,
                                    String departamento, byte[] imagen_antes, byte[] imagen_despues) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // Obtener el id_usuario de SharedPreferences
        SharedPreferences sharedPreferences = context.getSharedPreferences("nombre_prefs", Context.MODE_PRIVATE);
        int idUsuario = sharedPreferences.getInt("Id_Usuario", -1); // -1 si no se encuentra

        if (idUsuario == -1) {
            Log.e("InsertarReporte", "Id_Usuario no encontrado. Asegúrate de que el usuario haya iniciado sesión.");
            return false; // Salir del método si no hay un Id_Usuario válido
        }

        ContentValues values = new ContentValues();
        values.put("id_ticket", id_ticket);
        values.put("fecha_asignacion", fecha_asignacion);
        values.put("fecha_reparacion", fecha_reparacion);
        values.put("colonia", colonia);
        values.put("direccion", direccion);
        values.put("tipo_suelo", tipo_suelo);
        values.put("reportante", reportante);
        values.put("telefono_reportante", telefono_reportante);
        values.put("reparador", reparador);
        values.put("material", material);
        values.put("departamento", departamento); // Asegúrate de que el nombre coincida con la columna en tu tabla
        values.put("imagen_antes", imagen_antes);
        values.put("imagen_despues", imagen_despues);
        values.put("id_usuario", idUsuario); // Agregar el id_usuario a los valores

        long id = db.insert("reportes", null, values);
        db.close();
        return id != -1; // Devuelve true si la inserción fue exitosa
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
        imageViewEvidencia_antes.setImageResource(0);
        imageViewEvidencia_despues.setImageResource(0);
        spinnerDepartamento.setSelection(0); // Restablecer el spinner
    }
}
