package com.example.controlmaterial11;

import android.os.Bundle;

import com.example.controlmaterial11.databinding.ActivityAcercaDeBinding;

public class Acerca_de_Activity extends DrawerBaseActivity {
    ActivityAcercaDeBinding ActivityAcercaDeBinding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityAcercaDeBinding = ActivityAcercaDeBinding.inflate(getLayoutInflater());
        setContentView(ActivityAcercaDeBinding.getRoot());
    }
}


