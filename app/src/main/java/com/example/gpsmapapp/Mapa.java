package com.example.gpsmapapp;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class Mapa extends AppCompatActivity implements OnMapReadyCallback {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mapa);

        // Cliente de localización unificada (combina GPS, Wi-Fi, redes) para obtener la ubicación
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // Se prepara el fragmento del mapa declarado en el layout
        initMapFragment();
    }

    /** Obtiene el fragmento del mapa y registra el callback para inicializarlo de forma asíncrona */
    private void initMapFragment() {
        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map_fragment);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;

        // Configura interacción: permitir añadir marcadores con un toque
        setupTapToAddMarker();

        // Revisa permisos; si están concedidos, activa la capa de "mi ubicación"
        checkOrRequestLocationPermission();
    }

    /** Verifica permisos de ubicación y los solicita si faltan */
    private void checkOrRequestLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE
            );
        } else {
            enableUserLocation(); // Ya hay permiso: se activa la ubicación
        }
    }

    /** Activa la visualización de la ubicación del usuario y centra la cámara si se obtiene la posición */
    private void enableUserLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED && mMap != null) {

            mMap.setMyLocationEnabled(true);

            // Intenta obtener una lectura actual con la mayor precisión disponible
            fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
                    .addOnSuccessListener(this, location -> {
                        if (location != null) {
                            LatLng current = new LatLng(location.getLatitude(), location.getLongitude());

                            // Enfoca la cámara hacia la posición del usuario y añade un marcador informativo
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(current, 15f));
                            mMap.addMarker(new MarkerOptions()
                                    .position(current)
                                    .title("Mi ubicación actual"));
                        } else {
                            Toast.makeText(this, "No se pudo obtener la ubicación", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    /** Configura el gesto de tocar el mapa para añadir un marcador en cualquier punto */
    private void setupTapToAddMarker() {
        if (mMap == null) return;
        mMap.setOnMapClickListener(latLng -> {
            mMap.addMarker(new MarkerOptions()
                    .position(latLng)
                    .title("Marcador personalizado"));
            Toast.makeText(this, "Marcador añadido", Toast.LENGTH_SHORT).show();
        });
    }

    // Respuesta al cuadro de diálogo de permisos del sistema
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE &&
                grantResults.length > 0 &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            // Si el usuario otorgó el permiso, se habilita la ubicación inmediatamente (si el mapa ya está listo)
            if (mMap != null) {
                enableUserLocation();
            } else {
                // En el caso poco probable de que aún no esté listo, re-inicializa el fragmento
                initMapFragment();
            }
        }
    }
}
