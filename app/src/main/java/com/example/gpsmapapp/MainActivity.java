package com.example.gpsmapapp;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
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

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private TextView titleText;
    private Button buttonVerMapa, buttonDetenerRastreo;
    private SwitchCompat switchTiempoReal, switchModoAhorro;
    private static final int REQUEST_CODE_PERMISSIONS = 101;
    private String rolUsuario;
    private int intervaloPendiente = 5000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        rolUsuario = getIntent().getStringExtra("ROL_USUARIO");

        initViews();
        setupInsetsPadding();
        configurarInterfazSegunRol();
        setupClickListeners();
    }

    private void initViews() {
        titleText = findViewById(R.id.titleText);
        buttonVerMapa = findViewById(R.id.button2);
        buttonDetenerRastreo = findViewById(R.id.buttonDetener);
        switchTiempoReal = findViewById(R.id.switchTiempoReal);
        switchModoAhorro = findViewById(R.id.switchModoAhorro);
    }

    private void setupInsetsPadding() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void configurarInterfazSegunRol() {
        boolean esPadre = "padre".equals(rolUsuario);
        buttonVerMapa.setVisibility(esPadre ? View.VISIBLE : View.GONE);
        switchTiempoReal.setVisibility(esPadre ? View.GONE : View.VISIBLE);
        switchModoAhorro.setVisibility(esPadre ? View.GONE : View.VISIBLE);
        buttonDetenerRastreo.setVisibility(esPadre ? View.GONE : View.VISIBLE);
        titleText.setText(esPadre ? "Panel de Monitoreo" : "Configurar mi Rastreo");
    }

    private void setupClickListeners() {
        switchTiempoReal.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                if (switchModoAhorro.isChecked()) switchModoAhorro.setChecked(false);
                verificarPermisosYComenzar(2000);
            } else if (!switchModoAhorro.isChecked()) {
                detenerServicioRastreo();
            }
        });

        switchModoAhorro.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                if (switchTiempoReal.isChecked()) switchTiempoReal.setChecked(false);
                verificarPermisosYComenzar(10000);
            } else if (!switchTiempoReal.isChecked()) {
                detenerServicioRastreo();
            }
        });

        buttonDetenerRastreo.setOnClickListener(v -> {
            switchTiempoReal.setChecked(false);
            switchModoAhorro.setChecked(false);
            detenerServicioRastreo();
        });

        buttonVerMapa.setOnClickListener(v -> startActivity(new Intent(this, Mapa.class)));
    }

    private void verificarPermisosYComenzar(int intervalo) {
        intervaloPendiente = intervalo;
        List<String> permisosNecesarios = new ArrayList<>();

        // 1. Permiso de ubicación fina
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            permisosNecesarios.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }

        // 2. Permiso de Notificaciones (Android 13+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                permisosNecesarios.add(Manifest.permission.POST_NOTIFICATIONS);
            }
        }

        if (permisosNecesarios.isEmpty()) {
            verificarPermisoSegundoPlanoYComenzar();
        } else {
            ActivityCompat.requestPermissions(this, permisosNecesarios.toArray(new String[0]), REQUEST_CODE_PERMISSIONS);
        }
    }

    private void verificarPermisoSegundoPlanoYComenzar() {
        // 3. Ubicación en segundo plano (Android 10+). Se pide por separado para no abrumar al usuario
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Por favor, selecciona 'Permitir todo el tiempo' para rastrear en segundo plano", Toast.LENGTH_LONG).show();
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_BACKGROUND_LOCATION}, REQUEST_CODE_PERMISSIONS);
            } else {
                iniciarServicioRastreo(intervaloPendiente);
            }
        } else {
            iniciarServicioRastreo(intervaloPendiente);
        }
    }

    private void iniciarServicioRastreo(int intervalo) {
        Intent intent = new Intent(this, UbicacionService.class);
        intent.putExtra("INTERVALO_MS", intervalo);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(intent);
        } else {
            startService(intent);
        }
        Toast.makeText(this, "Rastreo activo", Toast.LENGTH_SHORT).show();
    }

    private void detenerServicioRastreo() {
        stopService(new Intent(this, UbicacionService.class));
        Toast.makeText(this, "Rastreo detenido", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            boolean todosGarantizados = true;
            for (int res : grantResults) {
                if (res != PackageManager.PERMISSION_GRANTED) todosGarantizados = false;
            }

            if (todosGarantizados) {
                verificarPermisoSegundoPlanoYComenzar();
            } else {
                switchTiempoReal.setChecked(false);
                switchModoAhorro.setChecked(false);
                Toast.makeText(this, "Se requieren todos los permisos para funcionar", Toast.LENGTH_SHORT).show();
            }
        }
    }
}