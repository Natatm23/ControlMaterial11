package com.example.controlmaterial11;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.controlmaterial11.databinding.SincronizarBinding;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SincronizarActivity extends DrawerBaseActivity {
    SincronizarBinding sincronizarBinding;
    private RecyclerView recyclerView;
    private ReporteAdapter reporteAdapter;
    private List<Reporte> listaReportes;
    private View progressBar; // Agrega el ProgressBar

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sincronizarBinding = SincronizarBinding.inflate(getLayoutInflater());
        setContentView(sincronizarBinding.getRoot());

        // Inicializa el ProgressBar
        progressBar = sincronizarBinding.progressBar;

        // SwipeRefreshLayout para recargar los datos
        sincronizarBinding.swipeRefreshLayout.setOnRefreshListener(() -> {
            cargarDatos(); // Cargar datos nuevamente
            reporteAdapter.notifyDataSetChanged(); // Notificar al adaptador que los datos han cambiado
            sincronizarBinding.swipeRefreshLayout.setRefreshing(false); // Detener la animación de refresco
        });

        recyclerView = sincronizarBinding.recyclerViewReportes;
        listaReportes = new ArrayList<>();

        // Cargar los datos desde SQLite
        cargarDatos();

        // Configurar el adaptador
        reporteAdapter = new ReporteAdapter(listaReportes);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(reporteAdapter);

        // Configurar el botón de sincronización
        sincronizarBinding.btnSincronizar.setOnClickListener(v -> {
            // Llamar al método para sincronizar los datos
            sincronizarDatos();
        });
    }

    private void cargarDatos() {
        DBHelper dbHelper = new DBHelper(this);
        listaReportes = dbHelper.obtenerTodosLosReportes();
    }

    private void sincronizarDatos() {
        // Obtener los reportes que deseas sincronizar
        DBHelper dbHelper = new DBHelper(this);
        List<Reporte_sincronizar> reportesParaSincronizar = dbHelper.obtenerTodosLosReportesParaSincronizar();

        // Verificar si no hay reportes para sincronizar
        if (reportesParaSincronizar.isEmpty()) {
            Toast.makeText(this, "No se encontaron reportes para sincronizar", Toast.LENGTH_SHORT).show();
            return; // Salir del método si no hay reportes
        }

        // Crear una tarea asíncrona para enviar los datos a SQL Server
        new SincronizarTask(reportesParaSincronizar).execute();
    }


    private class SincronizarTask extends AsyncTask<Void, Void, Boolean> {
        private List<Reporte_sincronizar> reportes;
        private DBHelper dbHelper;

        public SincronizarTask(List<Reporte_sincronizar> reportes) {
            this.reportes = reportes;
            this.dbHelper = new DBHelper(SincronizarActivity.this); // Instanciar DBHelper
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Mostrar el ProgressBar antes de comenzar la tarea
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            Connection connection = null;
            List<String> idsDuplicados = new ArrayList<>(); // Lista para almacenar IDs duplicados
            try {
                connection = ConecctionBD.getConnection();
                if (connection == null) {
                    Log.e("Sincronizar", "No se pudo establecer la conexión a SQL Server");
                    return false;
                }

                for (Reporte_sincronizar reporte : reportes) {
                    String idTicket = reporte.getIdTicket();

                    // Verificar si el ID ya existe en SQL Server
                    String checkQuery = "SELECT COUNT(*) FROM Reportes WHERE Id_ticket = ?";
                    try (PreparedStatement checkStatement = connection.prepareStatement(checkQuery)) {
                        checkStatement.setString(1, idTicket);
                        ResultSet resultSet = checkStatement.executeQuery();
                        if (resultSet.next() && resultSet.getInt(1) > 0) {
                            idsDuplicados.add(idTicket); // Agregar el ID a la lista de duplicados
                            continue; // Saltar al siguiente reporte si ya existe
                        }
                    }

                    // Verificar que las fechas no sean nulas antes de insertar
                    Date fechaAsignacion = reporte.getFechaAsignacionDate();
                    Date fechaReparacion = reporte.getFechaReparacionDate();

                    if (fechaAsignacion == null) {
                        throw new IllegalArgumentException("La fecha de asignación no puede ser nula para el ticket: " + reporte.getIdTicket());
                    }

                    if (fechaReparacion == null) {
                        throw new IllegalArgumentException("La fecha de reparación no puede ser nula para el ticket: " + reporte.getIdTicket());
                    }

                    String query = "INSERT INTO Reportes (Id_ticket, Departamento, Fecha_asignacion, Fecha_reparacion, " +
                            "Colonia, Tipo_suelo, Direccion, Reportante, Telefono_reportante, " +
                            "Reparador, Material, Imagen_antes, Imagen_despues, id_usuario) " +
                            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

                    try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                        preparedStatement.setString(1, idTicket);                                // 1. Id_ticket
                        preparedStatement.setString(2, reporte.getDepartamento());                // 2. Departamento
                        preparedStatement.setDate(3, new java.sql.Date(fechaAsignacion.getTime())); // 3. Fecha_asignacion
                        preparedStatement.setDate(4, new java.sql.Date(fechaReparacion.getTime())); // 4. Fecha_reparacion
                        preparedStatement.setString(5, reporte.getColonia());                    // 5. Colonia
                        preparedStatement.setString(6, reporte.getTipoSuelo());                  // 6. Tipo_suelo
                        preparedStatement.setString(7, reporte.getDireccion());                  // 7. Direccion
                        preparedStatement.setString(8, reporte.getReportante());                 // 8. Reportante
                        preparedStatement.setString(9, reporte.getTelefonoReportante());         // 9. Telefono_reportante
                        preparedStatement.setString(10, reporte.getReparador());                 // 10. Reparador
                        preparedStatement.setString(11, reporte.getMaterial());                  // 11. Material
                        preparedStatement.setBytes(12, reporte.getImagenAntes());                // 12. Imagen_antes
                        preparedStatement.setBytes(13, reporte.getImagenDespues());              // 13. Imagen_despues
                        preparedStatement.setInt(14, LoginActivity.idUsuario);                   // 14. id_usuario

                        preparedStatement.executeUpdate();
                    } catch (SQLException e) {
                        Log.e("Sincronizar", "Error al insertar el reporte sql: " + e.getMessage());
                        e.printStackTrace();

                        runOnUiThread(() -> Toast.makeText(SincronizarActivity.this, "Error al insertar el reporte: " + e.getMessage(), Toast.LENGTH_LONG).show());
                        return false; // Hubo un error al insertar
                    }


                }

                // Si hay IDs duplicados, mostrar un mensaje y no continuar
                if (!idsDuplicados.isEmpty()) {
                    StringBuilder mensajeDuplicados = new StringBuilder("Ya existe un reporte con los IDs: ");
                    for (String id : idsDuplicados) {
                        mensajeDuplicados.append(id).append(", ");
                    }
                    // Eliminar la última coma y espacio
                    mensajeDuplicados.setLength(mensajeDuplicados.length() - 2);
                    runOnUiThread(() -> Toast.makeText(SincronizarActivity.this, mensajeDuplicados.toString(), Toast.LENGTH_LONG).show());
                    return false; // Finalizar si hay duplicados
                }

                // Si la sincronización fue exitosa, borrar los reportes de SQLite
                dbHelper.borrarTodosLosReportes();

                return true; // Sincronización y eliminación de datos exitosas
            } catch (IllegalArgumentException e) {
                Log.e("Sincronizar", e.getMessage());

                // Mostrar Toast con el error
                runOnUiThread(() -> Toast.makeText(SincronizarActivity.this, e.getMessage(), Toast.LENGTH_LONG).show());
                return false; // Evita continuar con la sincronización si hay errores
            } catch (Exception e) {
                Log.e("Sincronizar", "Error general: " + e.getMessage());
                e.printStackTrace();

                // Mostrar Toast con el error
                runOnUiThread(() -> Toast.makeText(SincronizarActivity.this, "Error general: " + e.getMessage(), Toast.LENGTH_LONG).show());
                return false; // Hubo un error general
            } finally {
                ConecctionBD.closeConnection(connection);
            }
        }

        @Override
        protected void onPostExecute(Boolean result) {
            // Pausar el ProgressBar por un tiempo extra
            new Handler().postDelayed(() -> {
                progressBar.setVisibility(View.GONE); // Ocultar el ProgressBar
                if (result) {
                    // Mostrar un Toast de éxito al usuario
                    runOnUiThread(() -> Toast.makeText(SincronizarActivity.this, "Reportes sincronizados con éxito", Toast.LENGTH_LONG).show());
                } else {
                    // Mostrar un Toast de error al usuario
                    runOnUiThread(() -> Toast.makeText(SincronizarActivity.this, "Ocurrió un error durante la sincronización", Toast.LENGTH_LONG).show());
                }
            }, 2500);
        }
    }
}