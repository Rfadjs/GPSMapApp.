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

        // Se llama al metodo que configura y prepara el mapa
        inicializarMapa();
    }

    // Metodo que obtiene el fragmento del mapa desde el layout y lo prepara
    private void inicializarMapa() {
        SupportMapFragment mapFragment = (SupportMapFragment)
                getSupportFragmentManager().findFragmentById(R.id.map_fragment_lugar);

        // Si el fragmento del mapa existe, se carga de forma asíncrona
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;

        // Llamada al metodo que coloca el marcador y centra la vista
        mostrarUbicacionSantoTomas();
    }

    // Metodo separado para crear y mostrar la ubicación con marcador y zoom
    private void mostrarUbicacionSantoTomas() {
        // Coordenadas de Santo Tomás - Ovalle
        LatLng lugar = new LatLng(-30.60465, -71.20476);

        // Se añade un marcador en el punto seleccionado
        mMap.addMarker(new MarkerOptions()
                .position(lugar)
                .title("Santo Tomás - Ovalle"));

        // Se centra la cámara en el marcador y se ajusta el nivel de zoom
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lugar, 18));

        // Se establece el tipo de mapa en modo híbrido (satélite + etiquetas)
        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
    }
}
