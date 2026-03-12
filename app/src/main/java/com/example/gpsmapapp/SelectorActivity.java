package com.example.gpsmapapp;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

public class SelectorActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selector);

        findViewById(R.id.btnSoyPadre).setOnClickListener(v -> entrar("padre"));
        findViewById(R.id.btnSoyHija).setOnClickListener(v -> entrar("hija"));
    }

    private void entrar(String rol) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("ROL_USUARIO", rol);
        startActivity(intent);
    }
}