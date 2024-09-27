package com.example.controlmaterial11;

import android.os.Bundle;

import com.example.controlmaterial11.databinding.ActivityEliminarReporteBinding;

public class Eliminar_reporteActivity extends DrawerBaseActivity {
    ActivityEliminarReporteBinding eliminarReporteBinding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        eliminarReporteBinding = eliminarReporteBinding.inflate(getLayoutInflater());
        setContentView(eliminarReporteBinding.getRoot());
    }
}
