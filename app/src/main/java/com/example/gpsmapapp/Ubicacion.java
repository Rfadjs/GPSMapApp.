package com.example.gpsmapapp;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class Ubicacion extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ubicacion);

        // Obtenemos el fragmento del mapa y esperamos a que esté listo
        SupportMapFragment mapFragment = (SupportMapFragment)
                getSupportFragmentManager().findFragmentById(R.id.map_fragment_lugar);

        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;

        // Coordenadas del lugar
        LatLng lugar = new LatLng(-30.60465, -71.20476);

        // Añadir marcador con nombre
        mMap.addMarker(new MarkerOptions()
                .position(lugar)
                .title(" Santo Tomás - Ovalle"));

        // Centrar la cámara en ese lugar con zoom
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lugar, 18));
    }
}
