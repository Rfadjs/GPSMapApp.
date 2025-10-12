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

    Button buttonMostrarUbicacion;
    Button buttonGoogleMap;
    Button buttonDescargarImagen;
    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Botón que abre la pantalla con la ubicación actual del usuario
        buttonMostrarUbicacion = findViewById(R.id.button);
        buttonMostrarUbicacion.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, Mapa.class);
            startActivity(intent);
        });

        // Botón que dirige al mapa con una localización establecida previamente
        buttonGoogleMap = findViewById(R.id.button2);
        buttonGoogleMap.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, Ubicacion.class);
            startActivity(intent);
        });

        // Elementos para obtener y mostrar una imagen descargada desde la web
        buttonDescargarImagen = findViewById(R.id.buttonDescargarImagen);
        imageView = findViewById(R.id.imageView);

        buttonDescargarImagen.setOnClickListener(v -> {
            String urlImagen = "https://static.vecteezy.com/system/resources/thumbnails/034/928/042/small_2x/ai-generated-cat-clip-art-free-png.png";

            // Se ejecuta un hilo en segundo plano para realizar la descarga sin afectar la interfaz
            new Thread(() -> {
                try {
                    java.net.URL url = new java.net.URL(urlImagen);
                    final Bitmap bitmap = BitmapFactory.decodeStream(url.openConnection().getInputStream());

                    // La imagen descargada se muestra en el ImageView desde el hilo principal
                    runOnUiThread(() -> imageView.setImageBitmap(bitmap));

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();
        });
    }
}
