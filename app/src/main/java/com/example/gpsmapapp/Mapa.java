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

    // ===================== Constantes =====================
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    // ======================= Estado =======================
    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationClient;

    // ================== Ciclo de vida =====================
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mapa);

        initLocationClient();
        initMapFragment();
    }

    // ===================== Inicialización =================
    /** Instancia el cliente de ubicación unificada (GPS/Wi-Fi/red). */
    private void initLocationClient() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
    }

    /** Obtiene el fragmento de mapa del layout y registra el callback. */
    private void initMapFragment() {
        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map_fragment);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }

    // =================== Callback del mapa =================
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;

        setupTapToAddMarker();          // interacción del usuario
        checkOrRequestLocationPermission(); // habilita ubicación si hay permiso
    }

    // =================== Permisos & ubicación ==============
    /** Verifica ACCESS_FINE_LOCATION y lo solicita si falta. */
    private void checkOrRequestLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE
            );
        } else {
            enableUserLocation(); // Permiso concedido previamente
        }
    }

    /** Activa "Mi ubicación" y centra la cámara si se obtiene la posición. */
    private void enableUserLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED && mMap != null) {

            mMap.setMyLocationEnabled(true);

            // Lectura puntual con la mayor precisión disponible
            fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
                    .addOnSuccessListener(this, location -> {
                        if (location != null) {
                            LatLng current = new LatLng(location.getLatitude(), location.getLongitude());

                            // Enfoca la cámara y añade un marcador informativo
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

    // ================== Interacción con el mapa ============
    /** Al tocar el mapa, agrega un marcador en ese punto. */
    private void setupTapToAddMarker() {
        if (mMap == null) return;
        mMap.setOnMapClickListener(latLng -> {
            mMap.addMarker(new MarkerOptions()
                    .position(latLng)
                    .title("Marcador personalizado"));
            Toast.makeText(this, "Marcador añadido", Toast.LENGTH_SHORT).show();
        });
    }

    // ============== Resultado del diálogo de permisos ======
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE
                && grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            // Si el permiso fue otorgado, habilita la ubicación (si el mapa ya está listo)
            if (mMap != null) {
                enableUserLocation();
            } else {
                // Caso raro: re-inicializa el mapa si aún no estuviera listo
                initMapFragment();
            }
        }
    }
}
