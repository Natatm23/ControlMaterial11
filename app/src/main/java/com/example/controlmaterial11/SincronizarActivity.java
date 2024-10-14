package com.example.controlmaterial11;

import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
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


    }

    private void cargarDatos() {
        DBHelper dbHelper = new DBHelper(this);
        listaReportes = dbHelper.obtenerTodosLosReportes(); // Obtener reportes desde SQLite
    }





}
