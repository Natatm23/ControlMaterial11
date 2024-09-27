package com.example.controlmaterial11;

import android.os.Bundle;

import com.example.controlmaterial11.databinding.DescargarReporteBinding;

public class DescargarReporteActivity extends DrawerBaseActivity {
    DescargarReporteBinding descargarReporteBinding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        descargarReporteBinding = descargarReporteBinding.inflate(getLayoutInflater());
        setContentView(descargarReporteBinding.getRoot());
    }
}
