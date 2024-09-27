package com.example.controlmaterial11;

import android.os.Bundle;

import com.example.controlmaterial11.databinding.ActivityEditarReporteBinding;
public class Editar_reporte_Activity extends DrawerBaseActivity {
    ActivityEditarReporteBinding editarReporteBinding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        editarReporteBinding = editarReporteBinding.inflate(getLayoutInflater());
        setContentView(editarReporteBinding.getRoot());
    }
}
