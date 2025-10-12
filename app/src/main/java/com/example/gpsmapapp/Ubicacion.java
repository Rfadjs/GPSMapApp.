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

    // ======================= Estado =======================
    private GoogleMap mMap;

    // ================== Ciclo de vida =====================
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ubicacion);

        initMapFragment(); // Configura y prepara el mapa
    }

    // =================== Inicialización ===================
    /**
     * Obtiene el fragmento del mapa desde el layout y lo prepara
     * para ser cargado de forma asíncrona.
     */
    private void initMapFragment() {
        SupportMapFragment mapFragment = (SupportMapFragment)
                getSupportFragmentManager().findFragmentById(R.id.map_fragment_lugar);

        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }

    // ================== Callback del mapa =================
    /**
     * Método invocado automáticamente cuando el mapa está listo para su uso.
     * Aquí se configura el marcador y el enfoque de cámara.
     */
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        showSantoTomasLocation(); // Muestra el marcador y centra la vista
    }

    // =================== Lógica del mapa ==================
    /**
     * Crea y muestra la ubicación de Santo Tomás – Ovalle en el mapa
     * con un marcador y un nivel de zoom apropiado.
     */
    private void showSantoTomasLocation() {
        // Coordenadas de Santo Tomás - Ovalle
        LatLng santoTomasOvalle = new LatLng(-30.60465, -71.20476);

        // Agrega un marcador en la posición indicada
        mMap.addMarker(new MarkerOptions()
                .position(santoTomasOvalle)
                .title("Santo Tomás - Ovalle"));

        // Centra la cámara y ajusta el zoom
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(santoTomasOvalle, 18));

        // Configura el tipo de mapa en modo híbrido (satélite + etiquetas)
        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
    }
}
