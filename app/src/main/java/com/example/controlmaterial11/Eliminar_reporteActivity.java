package com.example.controlmaterial11;

import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import com.bumptech.glide.Glide;
import com.example.controlmaterial11.databinding.ActivityEliminarReporteBinding;

import java.util.List;

public class Eliminar_reporteActivity extends DrawerBaseActivity {
    ActivityEliminarReporteBinding eliminarReporteBinding;
    DBHelper dbHelper; // Instancia de DBHelper
    private int id_ticketActual = -1;  // Variable para almacenar el ID del reporte actual
    private Spinner spinnerDepartamento;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        eliminarReporteBinding = eliminarReporteBinding.inflate(getLayoutInflater());
        setContentView(eliminarReporteBinding.getRoot());

        // Inicializar DBHelper
        dbHelper = new DBHelper(this);

        // Configurar el botón de búsqueda
        eliminarReporteBinding.buttonBuscar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Obtener el ID del ticket ingresado
                String idTicketStr = eliminarReporteBinding.editTextBusqueda.getText().toString().trim();
                if (!idTicketStr.isEmpty()) {
                    int id_ticket = Integer.parseInt(idTicketStr);
                    buscarYMostrarReporte(id_ticket);
                } else {
                    Toast.makeText(Eliminar_reporteActivity.this, "Por favor ingresa un ID de ticket", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Configurar el botón de eliminar
        eliminarReporteBinding.btnEliminarReporte.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mostrarDialogoConfirmacion();
            }
        });
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
            String departamento = cursor.getString(cursor.getColumnIndexOrThrow("Departamento"));  // Asegúrate de tener esta columna

            byte[] imagenAntesBlob = cursor.getBlob(cursor.getColumnIndexOrThrow("Imagen_antes"));
            byte[] imagenDespuesBlob = cursor.getBlob(cursor.getColumnIndexOrThrow("Imagen_despues"));

            eliminarReporteBinding.txtTicket.setText(Id_ticket);
            eliminarReporteBinding.txtFechaAsignacion.setText(fechaAsignacion);
            eliminarReporteBinding.txtFechaReparacion.setText(fechaReparacion);
            eliminarReporteBinding.txtColonia.setText(colonia);
            eliminarReporteBinding.txtTipoSuelo.setText(tipoSuelo);
            eliminarReporteBinding.direccion.setText(direccion);
            eliminarReporteBinding.txtReportante.setText(reportante);
            eliminarReporteBinding.txtTelReportante.setText(telefonoReportante);
            eliminarReporteBinding.txtReparador.setText(reparador);
            eliminarReporteBinding.txtMaterial.setText(material);

            // Cargar las imágenes
            if (imagenAntesBlob != null) {
                Bitmap bitmapAntes = BitmapFactory.decodeByteArray(imagenAntesBlob, 0, imagenAntesBlob.length);
                eliminarReporteBinding.imageViewEvidenciaAntes.setImageBitmap(bitmapAntes);
            } else {
                eliminarReporteBinding.imageViewEvidenciaAntes.setImageResource(R.drawable.info);
            }

            if (imagenDespuesBlob != null) {
                Bitmap bitmapDespues = BitmapFactory.decodeByteArray(imagenDespuesBlob, 0, imagenDespuesBlob.length);
                eliminarReporteBinding.imageViewEvidenciaDespues.setImageBitmap(bitmapDespues);
            } else {
                eliminarReporteBinding.imageViewEvidenciaDespues.setImageResource(R.drawable.info);
            }

            // Seleccionar el valor correcto en el Spinner de departamentos
            if (departamento != null) {
                int position = ((ArrayAdapter<String>) spinnerDepartamento.getAdapter()).getPosition(departamento);
                if (position >= 0) {
                    spinnerDepartamento.setSelection(position);
                }
            }

            cursor.close();
        } else {
            Toast.makeText(this, "Reporte no encontrado", Toast.LENGTH_SHORT).show();

            // Cierra el cursor si es null
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    // Método para mostrar un cuadro de diálogo de confirmación
    private void mostrarDialogoConfirmacion() {
        if (id_ticketActual != -1) {  // Verificar que hay un reporte seleccionado
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Confirmar eliminación");
            builder.setMessage("¿Deseas eliminar este reporte?");
            builder.setPositiveButton("Sí", (dialog, which) -> {
                eliminarReporte(); // Llamar al método para eliminar el reporte
            });
            builder.setNegativeButton("No", (dialog, which) -> {
                // Cerrar el diálogo sin hacer nada
                dialog.dismiss();
            });

            // Mostrar el cuadro de diálogo
            builder.create().show();
        } else {
            Toast.makeText(this, "Primero busca un reporte para eliminar", Toast.LENGTH_SHORT).show();
        }
    }

    // Método para eliminar el reporte actual
    private void eliminarReporte() {
        if (id_ticketActual != -1) {  // Verificar que hay un reporte seleccionado
            boolean resultado = dbHelper.eliminarReporte(id_ticketActual); // Llama al método de DBHelper para eliminar el reporte
            if (resultado) {
                // Limpiar los campos si el reporte fue eliminado
                eliminarReporteBinding.editTextBusqueda.setText("");
                eliminarReporteBinding.txtTicket.setText("");
                eliminarReporteBinding.txtFechaAsignacion.setText("");
                eliminarReporteBinding.txtFechaReparacion.setText("");
                eliminarReporteBinding.txtColonia.setText("");
                eliminarReporteBinding.txtTipoSuelo.setText("");
                eliminarReporteBinding.direccion.setText("");
                eliminarReporteBinding.txtReportante.setText("");
                eliminarReporteBinding.txtTelReportante.setText("");
                eliminarReporteBinding.txtReparador.setText("");
                eliminarReporteBinding.txtMaterial.setText("");
                eliminarReporteBinding.imageViewEvidenciaAntes.setImageResource(0);
                eliminarReporteBinding.imageViewEvidenciaDespues.setImageResource(0);

                Toast.makeText(this, "Reporte eliminado exitosamente", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Error al eliminar el reporte", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Primero busca un reporte para eliminar", Toast.LENGTH_SHORT).show();
        }
    }
}
