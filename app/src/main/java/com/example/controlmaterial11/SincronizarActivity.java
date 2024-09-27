package com.example.controlmaterial11;

import android.os.Bundle;

import com.example.controlmaterial11.databinding.SincronizarBinding;

public class SincronizarActivity extends DrawerBaseActivity {
    SincronizarBinding sincronizarBinding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sincronizarBinding = sincronizarBinding.inflate(getLayoutInflater());
        setContentView(sincronizarBinding.getRoot());
    }
}
