package com.example.controlmaterial11;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.controlmaterial11.databinding.SincronizarBinding;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SincronizarActivity extends DrawerBaseActivity {
    SincronizarBinding SincronizarBinding;
    private RecyclerView recyclerView;
    private ReporteAdapter reporteAdapter;
    private List<Reporte> listaReportes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SincronizarBinding = SincronizarBinding.inflate(getLayoutInflater());
        setContentView(SincronizarBinding.getRoot());

        // SwipeRefreshLayout para recargar los datos
        SincronizarBinding.swipeRefreshLayout.setOnRefreshListener(() -> {
            cargarDatos(); // Cargar datos nuevamente
            reporteAdapter.notifyDataSetChanged(); // Notificar al adaptador que los datos han cambiado
            SincronizarBinding.swipeRefreshLayout.setRefreshing(false); // Detener la animación de refresco
        });

        recyclerView = SincronizarBinding.recyclerViewReportes;
        listaReportes = new ArrayList<>();

        // Cargar los datos desde SQLite
        cargarDatos();

        // Configurar el adaptador
        reporteAdapter = new ReporteAdapter(listaReportes);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(reporteAdapter);

        // Configurar el botón de sincronización
        SincronizarBinding.btnSincronizar.setOnClickListener(v -> {
            // Llamar al método para sincronizar los datos
            sincronizarDatos();
        });
    }

    private void cargarDatos() {
        DBHelper dbHelper = new DBHelper(this);
        listaReportes = dbHelper.obtenerTodosLosReportes(); // Cambia el método según lo que necesites
    }

    private void sincronizarDatos() {
        // Obtener los reportes que deseas sincronizar
        DBHelper dbHelper = new DBHelper(this);
        List<Reporte_sincronizar> reportesParaSincronizar = dbHelper.obtenerTodosLosReportesParaSincronizar();

        // Crear una tarea asíncrona para enviar los datos a SQL Server
        new SincronizarTask(reportesParaSincronizar).execute();
    }

    private class SincronizarTask extends AsyncTask<Void, Void, Boolean> {
        private List<Reporte_sincronizar> reportes;

        public SincronizarTask(List<Reporte_sincronizar> reportes) {
            this.reportes = reportes;
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            Connection connection = null;
            try {
                // Establecer la conexión a la base de datos SQL Server
                connection = ConecctionBD.getConnection();
                if (connection == null) {
                    Log.e("Sincronizar", "No se pudo establecer la conexión a SQL Server");
                    return false;
                }

                // Iterar sobre cada reporte y guardarlo en SQL Server
                for (Reporte_sincronizar reporte : reportes) {
                    String query = "INSERT INTO Reportes (Id_ticket, Fecha_asignacion, Fecha_reparacion, " +
                            "Colonia, Tipo_suelo, Direccion, Reportante, Telefono_reportante, " +
                            "Reparador, Material, Imagen_antes, Imagen_despues) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

                    try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                        preparedStatement.setString(1, reporte.getIdTicket());
                        preparedStatement.setString(2, reporte.getFechaAsignacion());
                        preparedStatement.setString(3, reporte.getFechaReparacion());
                        preparedStatement.setString(4, reporte.getColonia());
                        preparedStatement.setString(5, reporte.getTipoSuelo());
                        preparedStatement.setString(6, reporte.getDireccion());
                        preparedStatement.setString(7, reporte.getReportante());
                        preparedStatement.setString(8, reporte.getTelefonoReportante());
                        preparedStatement.setString(9, reporte.getReparador());
                        preparedStatement.setString(10, reporte.getMaterial());
                        preparedStatement.setBytes(11, reporte.getImagenAntes());
                        preparedStatement.setBytes(12, reporte.getImagenDespues());

                        preparedStatement.executeUpdate();
                    } catch (SQLException e) {
                        Log.e("Sincronizar", "Error al insertar el reporte: " + e.getMessage());
                        e.printStackTrace();
                        return false; // Hubo un error al insertar
                    }
                }
                return true; // Sincronización exitosa
            } catch (Exception e) { // Captura de excepciones generales
                Log.e("Sincronizar", "Error al conectar a la base de datos: " + e.getMessage());
                e.printStackTrace();
                return false; // Hubo un error general
            } finally {
                // Cerrar la conexión
                ConecctionBD.closeConnection(connection);
            }
        }

        @Override
        protected void onPostExecute(Boolean success) {
            super.onPostExecute(success);
            if (success) {
                Log.i("Sincronizar", "Datos sincronizados exitosamente");
            } else {
                Log.e("Sincronizar", "Error al sincronizar los datos");
            }
        }
    }
}
