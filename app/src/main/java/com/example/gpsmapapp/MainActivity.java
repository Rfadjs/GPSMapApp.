package com.example.gpsmapapp;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    // region UI
    private Button buttonVerMapa;
    private Button buttonDetenerRastreo;
    private SwitchCompat switchRastreo;
    private ImageView imageView;
    // endregion

    private static final int REQUEST_CODE_LOCATION = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupEdgeToEdge();
        setContentView(R.layout.activity_main);

        initViews();
        setupInsetsPadding();
        setupClickListeners();
    }

    private void setupEdgeToEdge() {
        EdgeToEdge.enable(this);
    }

    private void initViews() {
        // Enlazamos solo los elementos que quedaron en tu XML final
        buttonVerMapa = findViewById(R.id.button2);
        buttonDetenerRastreo = findViewById(R.id.buttonDetener);
        switchRastreo = findViewById(R.id.switchRastreo);
        imageView = findViewById(R.id.imageView);
    }

    private void setupInsetsPadding() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void setupClickListeners() {
        // Lógica del Switch: Ideal para el celular de la persona rastreada
        switchRastreo.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                verificarPermisosYComenzar();
            } else {
                detenerServicioRastreo();
            }
        });

        // Botón para ir al Mapa: Ideal para el celular del familiar que vigila
        buttonVerMapa.setOnClickListener(v -> open(Mapa.class));

        // Botón para detener todo manualmente
        buttonDetenerRastreo.setOnClickListener(v -> {
            detenerServicioRastreo();
            switchRastreo.setChecked(false); // Apaga el switch visualmente
        });
    }

    // ================= LÓGICA DE SERVICIO =================

    private void verificarPermisosYComenzar() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            iniciarServicioRastreo();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE_LOCATION);
        }
    }

    private void iniciarServicioRastreo() {
        Intent serviceIntent = new Intent(this, UbicacionService.class);
        // Como tu proyecto ya requiere SDK >= 26, usamos directamente startForegroundService
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(serviceIntent);
        } else {
            startService(serviceIntent);
        }
        Toast.makeText(this, "Compartiendo ubicación en tiempo real", Toast.LENGTH_SHORT).show();
    }

    private void detenerServicioRastreo() {
        Intent serviceIntent = new Intent(this, UbicacionService.class);
        stopService(serviceIntent);
        Toast.makeText(this, "Rastreo detenido", Toast.LENGTH_SHORT).show();
    }

    // ================= OTROS MÉTODOS =================

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_LOCATION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                iniciarServicioRastreo();
            } else {
                switchRastreo.setChecked(false); // Si no da permiso, apagamos el switch
                Toast.makeText(this, "Se requiere permiso de GPS para rastrear", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void open(Class<?> activity) {
        startActivity(new Intent(MainActivity.this, activity));
    }
}