package com.example.controlmaterial11;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.example.controlmaterial11.databinding.ActivityEditarReporteBinding;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class Editar_reporte_Activity extends DrawerBaseActivity {
    ActivityEditarReporteBinding editarReporteBinding;
    DBHelper dbHelper; // Instancia de DBHelper

    private static final int REQUEST_IMAGE_BEFORE = 1;
    private static final int REQUEST_IMAGE_AFTER = 2;

    private Uri imageUriAntes = null;
    private Uri imageUriDespues = null;
    private int id_ticketActual = -1;  // Variable para almacenar el ID del reporte actual

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        editarReporteBinding = ActivityEditarReporteBinding.inflate(getLayoutInflater());
        setContentView(editarReporteBinding.getRoot());

        // Inicializar DBHelper
        dbHelper = new DBHelper(this);

        // Configurar el botón de búsqueda
        editarReporteBinding.buttonBuscar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String idTicketStr = editarReporteBinding.editTextBusqueda.getText().toString().trim();
                if (!idTicketStr.isEmpty()) {
                    int id_ticket = Integer.parseInt(idTicketStr);
                    buscarYMostrarReporte(id_ticket);
                } else {
                    Toast.makeText(Editar_reporte_Activity.this, "Por favor ingresa un ID de ticket", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Configurar el botón para seleccionar imagen antes
        editarReporteBinding.btnSeleccionarImagenAntes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                seleccionarImagen(REQUEST_IMAGE_BEFORE);
            }
        });

        // Configurar el botón para seleccionar imagen después
        editarReporteBinding.btnSeleccionarImagenDespues.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                seleccionarImagen(REQUEST_IMAGE_AFTER);
            }
        });

        // Configurar la selección de imágenes (si es necesario)
        editarReporteBinding.imageViewEvidenciaAntes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                seleccionarImagen(REQUEST_IMAGE_BEFORE);
            }
        });

        editarReporteBinding.imageViewEvidenciaDespues.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                seleccionarImagen(REQUEST_IMAGE_AFTER);
            }
        });

        // Configurar el botón de guardar
        editarReporteBinding.btnEditarReporte.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                guardarCambiosReporte();
            }
        });
    }

    private void buscarYMostrarReporte(int id_ticket) {
        Cursor cursor = dbHelper.buscarReporte(id_ticket);

        if (cursor != null && cursor.moveToFirst()) {
            id_ticketActual = id_ticket;  // Guardar el ID del ticket actual

            // Extraer y mostrar los datos del reporte
            String Id_ticket = cursor.getString(cursor.getColumnIndexOrThrow("Id_ticket"));
            String fechaAsignacion = cursor.getString(cursor.getColumnIndexOrThrow("Fecha_asignacion"));
            String fechaReparacion = cursor.getString(cursor.getColumnIndexOrThrow("Fecha_reparacion"));
            String colonia = cursor.getString(cursor.getColumnIndexOrThrow("Colonia"));
            String tipoSuelo = cursor.getString(cursor.getColumnIndexOrThrow("Tipo_suelo"));
            String direccion = cursor.getString(cursor.getColumnIndexOrThrow("Direccion"));
            String reportante = cursor.getString(cursor.getColumnIndexOrThrow("Reportante"));
            String telefonoReportante = cursor.getString(cursor.getColumnIndexOrThrow("Telefono_reportante"));
            String reparador = cursor.getString(cursor.getColumnIndexOrThrow("Reparador"));
            String material = cursor.getString(cursor.getColumnIndexOrThrow("Material"));

            byte[] imagenAntesBlob = cursor.getBlob(cursor.getColumnIndexOrThrow("Imagen_antes"));
            byte[] imagenDespuesBlob = cursor.getBlob(cursor.getColumnIndexOrThrow("Imagen_despues"));

            editarReporteBinding.txtTicket.setText(Id_ticket);
            editarReporteBinding.txtFechaAsignacion.setText(fechaAsignacion);
            editarReporteBinding.txtFechaReparacion.setText(fechaReparacion);
            editarReporteBinding.txtColonia.setText(colonia);
            editarReporteBinding.txtTipoSuelo.setText(tipoSuelo);
            editarReporteBinding.direccion.setText(direccion);
            editarReporteBinding.txtReportante.setText(reportante);
            editarReporteBinding.txtTelReportante.setText(telefonoReportante);
            editarReporteBinding.txtReparador.setText(reparador);
            editarReporteBinding.txtMaterial.setText(material);

            // Cargar las imágenes
            if (imagenAntesBlob != null) {
                Bitmap bitmapAntes = BitmapFactory.decodeByteArray(imagenAntesBlob, 0, imagenAntesBlob.length);
                editarReporteBinding.imageViewEvidenciaAntes.setImageBitmap(bitmapAntes);
            } else {
                editarReporteBinding.imageViewEvidenciaAntes.setImageResource(R.drawable.info);
            }

            if (imagenDespuesBlob != null) {
                Bitmap bitmapDespues = BitmapFactory.decodeByteArray(imagenDespuesBlob, 0, imagenDespuesBlob.length);
                editarReporteBinding.imageViewEvidenciaDespues.setImageBitmap(bitmapDespues);
            } else {
                editarReporteBinding.imageViewEvidenciaDespues.setImageResource(R.drawable.info);
            }

            // Cierra el cursor después de usarlo
            cursor.close();
        } else {
            Toast.makeText(this, "Reporte no encontrado", Toast.LENGTH_SHORT).show();

            // Cierra el cursor si es null
            if (cursor != null) {
                cursor.close();
            }
        }
    }


    // Método para seleccionar una imagen
    private void seleccionarImagen(int requestCode) {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, requestCode);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null) {
            Uri selectedImage = data.getData();
            if (requestCode == REQUEST_IMAGE_BEFORE) {
                imageUriAntes = selectedImage;
                Glide.with(this).load(imageUriAntes).into(editarReporteBinding.imageViewEvidenciaAntes);
            } else if (requestCode == REQUEST_IMAGE_AFTER) {
                imageUriDespues = selectedImage;
                Glide.with(this).load(imageUriDespues).into(editarReporteBinding.imageViewEvidenciaDespues);
            }
        }
    }

    // Método para guardar los cambios del reporte
    private void guardarCambiosReporte() {
        if (id_ticketActual == -1) {
            Toast.makeText(this, "Primero busca un reporte para editar", Toast.LENGTH_SHORT).show();
            return;
        }

        String colonia = editarReporteBinding.txtColonia.getText().toString().trim();
        String direccion = editarReporteBinding.direccion.getText().toString().trim();
        String tipoSuelo = editarReporteBinding.txtTipoSuelo.getText().toString().trim();
        String reportante = editarReporteBinding.txtReportante.getText().toString().trim();
        String telefonoReportante = editarReporteBinding.txtTelReportante.getText().toString().trim();
        String reparador = editarReporteBinding.txtReparador.getText().toString().trim();
        String material = editarReporteBinding.txtMaterial.getText().toString().trim();

        byte[] imagenAntesBytes = null;
        byte[] imagenDespuesBytes = null;

        if (imageUriAntes != null) {
            imagenAntesBytes = convertirUriABytes(imageUriAntes);
        }

        if (imageUriDespues != null) {
            imagenDespuesBytes = convertirUriABytes(imageUriDespues);
        }


        // Actualizar el reporte en la base de datos
        boolean resultado = dbHelper.actualizarReporte(
                id_ticketActual,
                colonia,
                direccion,
                reportante,
                telefonoReportante,
                tipoSuelo,
                reparador,
                material,
                imagenAntesBytes,
                imagenDespuesBytes
        );


        if (resultado) {
            Toast.makeText(this, "Reporte actualizado exitosamente", Toast.LENGTH_SHORT).show();
            limpiarCampos(); // Limpiar campos después de guardar
        } else {
            Toast.makeText(this, "Error al actualizar el reporte", Toast.LENGTH_SHORT).show();
        }
    }

    // Método para convertir una Uri en un byte array
    private byte[] convertirUriABytes(Uri uri) {
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
            if (bitmap == null) {
                Toast.makeText(this, "No se pudo cargar la imagen", Toast.LENGTH_SHORT).show();
                return null;
            }
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            return stream.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error al convertir la imagen", Toast.LENGTH_SHORT).show();
            return null;
        }
    }

    // Método para limpiar los campos
    private void limpiarCampos() {
        editarReporteBinding.editTextBusqueda.setText("");
        editarReporteBinding.txtTicket.setText("");
        editarReporteBinding.txtFechaAsignacion.setText("");
        editarReporteBinding.txtFechaReparacion.setText("");
        editarReporteBinding.txtColonia.setText("");
        editarReporteBinding.txtTipoSuelo.setText("");
        editarReporteBinding.direccion.setText("");
        editarReporteBinding.txtReportante.setText("");
        editarReporteBinding.txtTelReportante.setText("");
        editarReporteBinding.txtReparador.setText("");
        editarReporteBinding.txtMaterial.setText("");
        editarReporteBinding.imageViewEvidenciaAntes.setImageResource(R.drawable.info);
        editarReporteBinding.imageViewEvidenciaDespues.setImageResource(R.drawable.info);
        imageUriAntes = null;
        imageUriDespues = null;
        id_ticketActual = -1;  // Resetear el ID del ticket actual
    }
}
