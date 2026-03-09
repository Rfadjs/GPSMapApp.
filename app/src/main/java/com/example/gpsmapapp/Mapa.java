package com.example.gpsmapapp;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
// NUEVAS IMPORTACIONES PARA FIREBASE
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Mapa extends AppCompatActivity implements OnMapReadyCallback {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private GoogleMap mMap;

    // VARIABLES PARA RASTREO EN TIEMPO REAL
    private DatabaseReference databaseReference;
    private Marker markerHija;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mapa);

        // Inicializamos la conexión a Firebase
        databaseReference = FirebaseDatabase.getInstance().getReference("ubicaciones/hija1");

        initMapFragment();
    }

    private void initMapFragment() {
        // Asegúrate de que en activity_mapa.xml el ID sea map_fragment
        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map_fragment);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;

        // Verificamos permisos para que el familiar también vea su propia ubi (opcional)
        checkOrRequestLocationPermission();

        // ACTIVAMOS EL ESCUCHADOR DE TIEMPO REAL
        iniciarRastreoFamiliar();
    }

    private void iniciarRastreoFamiliar() {
        // Este método escucha a Firebase perpetuamente mientras la pantalla esté abierta
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Extraemos los datos que el UbicacionService está subiendo
                    Double lat = dataSnapshot.child("latitud").getValue(Double.class);
                    Double lng = dataSnapshot.child("longitud").getValue(Double.class);

                    if (lat != null && lng != null) {
                        LatLng ubiHija = new LatLng(lat, lng);

                        if (markerHija == null) {
                            // Si el marcador no existe, lo creamos
                            markerHija = mMap.addMarker(new MarkerOptions()
                                    .position(ubiHija)
                                    .title("Ubicación de mi hija"));

                            // Movemos la cámara a ella la primera vez
                            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(ubiHija, 15f));
                        } else {
                            // Si ya existe, solo actualizamos su posición suavemente
                            markerHija.setPosition(ubiHija);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(Mapa.this, "Error de conexión: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void checkOrRequestLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            if (mMap != null) mMap.setMyLocationEnabled(true);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (mMap != null) {
                try { mMap.setMyLocationEnabled(true); } catch (SecurityException e) { e.printStackTrace(); }
            }
        }
    }
}