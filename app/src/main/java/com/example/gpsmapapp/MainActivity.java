package com.example.gpsmapapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    // region UI
    private Button buttonMostrarUbicacion;
    private Button buttonGoogleMap;
    private Button buttonDescargarImagen;
    private ImageView imageView;
    // endregion

    // region Constantes
    private static final String IMAGE_URL =
            "https://static.vecteezy.com/system/resources/thumbnails/034/928/042/small_2x/ai-generated-cat-clip-art-free-png.png";
    // endregion

    // region Ciclo de vida
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupEdgeToEdge();
        setContentView(R.layout.activity_main);

        initViews();
        setupInsetsPadding();
        setupClickListeners();
    }
    // endregion

    // region Setup
    private void setupEdgeToEdge() {
        EdgeToEdge.enable(this);
    }

    private void initViews() {
        buttonMostrarUbicacion = findViewById(R.id.button);
        buttonGoogleMap = findViewById(R.id.button2);
        buttonDescargarImagen = findViewById(R.id.buttonDescargarImagen);
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
        // Abrir pantalla con la ubicación actual
        buttonMostrarUbicacion.setOnClickListener(v -> open(Mapa.class));

        // Abrir mapa con ubicación predefinida
        buttonGoogleMap.setOnClickListener(v -> open(Ubicacion.class));

        // Descargar y mostrar imagen desde la web
        buttonDescargarImagen.setOnClickListener(v -> downloadImageFromUrl(IMAGE_URL));
    }
    // endregion

    // region Navegación
    private void open(Class<?> activity) {
        startActivity(new Intent(MainActivity.this, activity));
    }
    // endregion

    // region Red / Imagen
    private void downloadImageFromUrl(String urlString) {
        new Thread(() -> {
            try {
                java.net.URL url = new java.net.URL(urlString);
                // Nota: se mantiene la descarga simple para no bloquear la UI; se actualiza en el hilo principal
                final Bitmap bitmap = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                runOnUiThread(() -> imageView.setImageBitmap(bitmap));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }
    // endregion
}
