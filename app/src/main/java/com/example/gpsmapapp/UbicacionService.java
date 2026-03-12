package com.example.gpsmapapp;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.IBinder;
import android.os.Looper;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class UbicacionService extends Service {

    private static final String CHANNEL_ID = "UbicacionChannel";
    private FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback;
    private DatabaseReference databaseReference;
    private int intervaloActual = 5000;

    @Override
    public void onCreate() {
        super.onCreate();
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        databaseReference = FirebaseDatabase.getInstance().getReference("ubicaciones/hija1");

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null || locationResult.getLastLocation() == null) return;

                for (Location location : locationResult.getLocations()) {
                    if (location != null) {
                        // Filtro de precisión balanceado para Chile (calles con edificios)
                        if (location.getAccuracy() < 100) {
                            actualizarEnFirebase(location);
                        }
                    }
                }
            }
        };
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Notificación obligatoria para Foreground Service
        createNotificationChannel();
        startForegroundServiceNotification();

        if (intent != null) {
            int nuevoIntervalo = intent.getIntExtra("INTERVALO_MS", 5000);

            if (nuevoIntervalo != intervaloActual) {
                intervaloActual = nuevoIntervalo;
                fusedLocationClient.removeLocationUpdates(locationCallback);
                startLocationUpdates();
            } else {
                startLocationUpdates();
            }
        }

        // START_STICKY: Clave para que el servicio se reinicie solo si el sistema lo mata
        return START_STICKY;
    }

    private void startLocationUpdates() {
        // CONFIGURACIÓN DE RECONEXIÓN Y RED
        LocationRequest locationRequest = new LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, intervaloActual)
                .setMinUpdateIntervalMillis(intervaloActual / 2)
                // CLAVE: No espera a que el GPS sea perfecto. Si sale del Metro y solo hay 4G/WiFi,
                // enviará la ubicación aproximada de inmediato.
                .setWaitForAccurateLocation(false)
                .build();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
        } else {
            stopSelf();
        }
    }

    private void actualizarEnFirebase(Location location) {
        Map<String, Object> updates = new HashMap<>();
        updates.put("latitud", location.getLatitude());
        updates.put("longitud", location.getLongitude());
        updates.put("timestamp", System.currentTimeMillis());
        updates.put("velocidad", location.getSpeed());

        // Al usar updateChildren, Firebase mantiene la "última ubicación conocida" sincronizada
        databaseReference.updateChildren(updates);
    }

    private void startForegroundServiceNotification() {
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Seguridad Activa")
                .setContentText("Tu familia puede ver tu ubicación en tiempo real.")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setOngoing(true)
                .build();

        startForeground(1, notification);
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "Canal de Ubicación",
                    NotificationManager.IMPORTANCE_LOW
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) manager.createNotificationChannel(serviceChannel);
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) { return null; }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (fusedLocationClient != null && locationCallback != null) {
            fusedLocationClient.removeLocationUpdates(locationCallback);
        }
    }
}