package com.example.controlmaterial11;

import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

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

        // Referenciar SwipeRefreshLayout
        SincronizarBinding.swipeRefreshLayout.setOnRefreshListener(() -> {
            cargarDatos(); // Cargar datos nuevamente
            reporteAdapter.notifyDataSetChanged(); // Notificar al adaptador que los datos han cambiado
            SincronizarBinding.swipeRefreshLayout.setRefreshing(false); // Detener la animación de refresco
        });

        recyclerView = SincronizarBinding.recyclerViewReportes;
        listaReportes = new ArrayList<>();

        // Cargar datos desde la base de datos
        cargarDatos();

        // Configurar el adaptador
        reporteAdapter = new ReporteAdapter(listaReportes);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(reporteAdapter);
    }

    private void cargarDatos() {
        DBHelper dbHelper = new DBHelper(this);
        listaReportes = dbHelper.obtenerTodosLosReportes(); // Ahora devuelve List<Reporte>
    }

    // Método para sincronizar los datos de SQLite con SQL Server
    private void sincronizarConSQLServer() {
        Connection connection = null;
        PreparedStatement preparedStatement = null;

        try {
            // Obtener la conexión a SQL Server
            connection = ConecctionBD.getConnection();

            if (connection != null) {
                // Obtener los datos de la tabla reportes en SQLite
                Cursor cursor = obtenerDatosParaSincronizar();

                if (cursor.moveToFirst()) {
                    do {
                        String sqlInsert = "INSERT INTO Reportes (Id_ticket, Id_Usuario, Fecha_asignacion, Fecha_reparacion, Colonia, Tipo_suelo, Direccion, Reportante, Telefono_reportante, Reparador, Material, Imagen_antes, Imagen_despues) " +
                                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

                        preparedStatement = connection.prepareStatement(sqlInsert);

                        // Usar las constantes definidas en DBHelper para las columnas
                        preparedStatement.setInt(1, cursor.getInt(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_ID_TICKET)));
                        preparedStatement.setInt(2, cursor.getInt(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_ID_USUARIO)));
                        preparedStatement.setDate(3, java.sql.Date.valueOf(cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_FECHA_ASIGNACION))));
                        preparedStatement.setDate(4, java.sql.Date.valueOf(cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_FECHA_REPARACION))));
                        preparedStatement.setString(5, cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_COLONIA)));
                        preparedStatement.setString(6, cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_TIPO_SUELO)));
                        preparedStatement.setString(7, cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_DIRECCION)));
                        preparedStatement.setString(8, cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_REPORTANTE)));
                        preparedStatement.setString(9, cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_TELEFONO_REPORTANTE)));
                        preparedStatement.setString(10, cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_REPARADOR)));
                        preparedStatement.setString(11, cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_MATERIAL)));
                        preparedStatement.setBytes(12, cursor.getBlob(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_IMAGEN_ANTES)));
                        preparedStatement.setBytes(13, cursor.getBlob(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_IMAGEN_DESPUES)));

                        preparedStatement.executeUpdate();
                    } while (cursor.moveToNext());
                }

                cursor.close();
                preparedStatement.close();
                connection.close();

            } else {
                // Manejar error de conexión
                System.out.println("Error: No se pudo establecer conexión a SQL Server.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Método que obtiene los datos para sincronizar
    private Cursor obtenerDatosParaSincronizar() {
        DBHelper dbHelper = new DBHelper(this);
        return dbHelper.obtenerDatosParaSincronizar();
    }
}
