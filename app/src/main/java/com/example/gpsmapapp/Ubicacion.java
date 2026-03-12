package com.example.gpsmapapp;

import android.content.IntentSender;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.Task;

public class Ubicacion extends AppCompatActivity implements OnMapReadyCallback {

    // ======================= Estado =======================
    private GoogleMap mMap;
    private static final int REQUEST_CHECK_SETTINGS = 1001; // Código para identificar la petición de GPS

    // ================== Ciclo de vida =====================
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ubicacion);

        // NUEVO: Al abrir esta pantalla, verificamos si el GPS está encendido
        solicitarEncendidoGPS();

        initMapFragment(); // Configura y prepara el mapa
    }

    // =================== Lógica de GPS ====================
    /**
     * Verifica si el GPS físico del celular está encendido.
     * Si está apagado, levanta una ventana nativa de Google pidiendo al usuario que lo encienda.
     */
    private void solicitarEncendidoGPS() {
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);

        SettingsClient client = LocationServices.getSettingsClient(this);
        Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());

        task.addOnFailureListener(this, e -> {
            if (e instanceof ResolvableApiException) {
                try {
                    // Muestra el diálogo nativo de Android: "¿Activar la ubicación?"
                    ResolvableApiException resolvable = (ResolvableApiException) e;
                    resolvable.startResolutionForResult(Ubicacion.this, REQUEST_CHECK_SETTINGS);
                } catch (IntentSender.SendIntentException sendEx) {
                    // Ignorar el error si no se puede mostrar el diálogo
                }
            }
        });
    }

    // =================== Inicialización ===================
    private void initMapFragment() {
        SupportMapFragment mapFragment = (SupportMapFragment)
                getSupportFragmentManager().findFragmentById(R.id.map_fragment_lugar);

        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }

    // ================== Callback del mapa =================
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        showSantoTomasLocation();
    }

    // =================== Lógica del mapa ==================
    private void showSantoTomasLocation() {
        LatLng santoTomasOvalle = new LatLng(-30.60465, -71.20476);

        mMap.addMarker(new MarkerOptions()
                .position(santoTomasOvalle)
                .title("Santo Tomás - Ovalle"));

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(santoTomasOvalle, 18));
        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
    }
}