package com.example.controlmaterial11;

import android.os.Bundle;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.controlmaterial11.databinding.SincronizarBinding;

import java.util.ArrayList;
import java.util.List;

public class SincronizarActivity extends DrawerBaseActivity {
    SincronizarBinding sincronizarBinding;
    private RecyclerView recyclerView;
    private ReporteAdapter reporteAdapter;
    private List<Reporte> listaReportes; // Cambiado a List<Reporte>

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sincronizarBinding = SincronizarBinding.inflate(getLayoutInflater());
        setContentView(sincronizarBinding.getRoot());

        recyclerView = sincronizarBinding.recyclerViewReportes;
        listaReportes = new ArrayList<>(); // Inicializa la lista como una lista de Reporte

        // Configurar el adaptador
        reporteAdapter = new ReporteAdapter(listaReportes);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(reporteAdapter);

        // Cargar datos desde la base de datos
        cargarDatos();

        // Configurar SwipeRefreshLayout
        sincronizarBinding.swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Llamar al método para cargar datos
                cargarDatos();
            }
        });
    }

    private void cargarDatos() {
        DBHelper dbHelper = new DBHelper(this);
        listaReportes.clear(); // Limpiar la lista antes de cargar nuevos datos
        listaReportes.addAll(dbHelper.obtenerTodosLosReportes()); // Ahora devuelve List<Reporte>

        // Notificar al adaptador que los datos han cambiado
        reporteAdapter.notifyDataSetChanged();

        // Detener la animación de refresco
        sincronizarBinding.swipeRefreshLayout.setRefreshing(false);
    }
}
