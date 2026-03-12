package com.example.gpsmapapp;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.media.RingtoneManager;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.animation.LinearInterpolator;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Mapa extends AppCompatActivity implements OnMapReadyCallback {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private GoogleMap mMap;
    private DatabaseReference databaseReference;
    private ValueEventListener rastreoListener;
    private Marker markerHija;

    private boolean alertaMostrada = false;
    private static final int DISTANCIA_UMBRAL_METROS = 30;
    private static final String CHANNEL_ID = "ALERTA_LLEGADA_CHANNEL";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mapa);

        databaseReference = FirebaseDatabase.getInstance().getReference("ubicaciones/hija1");
        createNotificationChannel(); // Creamos el canal al iniciar
        initMapFragment();
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Alertas de Proximidad",
                    NotificationManager.IMPORTANCE_HIGH // IMPORTANTE: Esto habilita el sonido y el pop-up
            );
            channel.setDescription("Canal para avisar cuando la hija llega a casa");
            channel.enableLights(true);
            channel.setLightColor(Color.RED);
            channel.enableVibration(true);

            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }
    }

    private void initMapFragment() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map_fragment);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);

        checkOrRequestLocationPermission();
        iniciarRastreoFamiliar();
    }

    private void iniciarRastreoFamiliar() {
        rastreoListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Double lat = dataSnapshot.child("latitud").getValue(Double.class);
                    Double lng = dataSnapshot.child("longitud").getValue(Double.class);

                    if (lat != null && lng != null) {
                        LatLng ubiHija = new LatLng(lat, lng);

                        if (markerHija == null) {
                            markerHija = mMap.addMarker(new MarkerOptions().position(ubiHija).title("Hija"));
                            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(ubiHija, 17f));
                        } else {
                            animarMovimientoMarcador(markerHija, ubiHija);
                            mMap.animateCamera(CameraUpdateFactory.newLatLng(ubiHija));
                        }

                        verificarProximidadAlPadre(lat, lng);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(Mapa.this, "Error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        };
        databaseReference.addValueEventListener(rastreoListener);
    }

    private void verificarProximidadAlPadre(double latHija, double lngHija) {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (mMap != null && mMap.isMyLocationEnabled() && mMap.getMyLocation() != null) {
                Location locPadre = mMap.getMyLocation();
                float[] distancia = new float[1];
                Location.distanceBetween(latHija, lngHija, locPadre.getLatitude(), locPadre.getLongitude(), distancia);

                if (distancia[0] <= DISTANCIA_UMBRAL_METROS && !alertaMostrada) {
                    lanzarAlertaLlegada();
                    alertaMostrada = true;
                } else if (distancia[0] > DISTANCIA_UMBRAL_METROS + 20) {
                    alertaMostrada = false;
                }
            }
        }
    }

    private void lanzarAlertaLlegada() {
        // 1. Crear la notificación de barra
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher) // Icono de la app
                .setContentTitle("¡LLEGADA DETECTADA!")
                .setContentText("Tu hija está llegando a tu ubicación.")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_ALARM)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)) // Sonido por defecto
                .setAutoCancel(true); // Se borra al hacer clic

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager != null) {
            notificationManager.notify(1001, builder.build());
        }

        // 2. Refuerzo de Vibración
        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        if (v != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                v.vibrate(VibrationEffect.createOneShot(1500, VibrationEffect.DEFAULT_AMPLITUDE));
            } else {
                v.vibrate(1500);
            }
        }
    }

    private void animarMovimientoMarcador(final Marker marker, final LatLng toPosition) {
        final LatLng startPosition = marker.getPosition();
        final android.animation.ValueAnimator valueAnimator = android.animation.ValueAnimator.ofFloat(0, 1);
        valueAnimator.setDuration(1800);
        valueAnimator.setInterpolator(new LinearInterpolator());
        valueAnimator.addUpdateListener(animation -> {
            float v = animation.getAnimatedFraction();
            double lng = v * toPosition.longitude + (1 - v) * startPosition.longitude;
            double lat = v * toPosition.latitude + (1 - v) * startPosition.latitude;
            marker.setPosition(new LatLng(lat, lng));
        });
        valueAnimator.start();
    }

    private void checkOrRequestLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        } else if (mMap != null) {
            mMap.setMyLocationEnabled(true);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (databaseReference != null && rastreoListener != null) {
            databaseReference.removeEventListener(rastreoListener);
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