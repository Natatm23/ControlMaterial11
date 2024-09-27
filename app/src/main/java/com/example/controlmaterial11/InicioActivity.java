package com.example.controlmaterial11;

import android.os.Bundle;

import com.example.controlmaterial11.databinding.ActivityInicioBinding;

public class InicioActivity extends DrawerBaseActivity {
ActivityInicioBinding activityInicioBinding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityInicioBinding = activityInicioBinding.inflate(getLayoutInflater());
        setContentView(activityInicioBinding.getRoot());
    }
}
