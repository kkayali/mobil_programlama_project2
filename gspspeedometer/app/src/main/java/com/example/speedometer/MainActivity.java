package com.example.speedometer;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Button;
import android.graphics.Color;
import android.media.MediaPlayer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    private LocationManager locationManager;
    private LocationListener locationListener;
    private TextView speedTextView, latLngTextView, maxSpeedTextView;
    private Button startButton, settingsButton, logButton;
    private boolean isTracking = false;

    private MediaPlayer alertPlayer;
    private File logFile;
    private float speedLimit = 80f;
    private float maxSpeed = 0f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        speedTextView = findViewById(R.id.speedTextView);
        latLngTextView = findViewById(R.id.latLngTextView);
        maxSpeedTextView = findViewById(R.id.maxSpeedTextView);
        startButton = findViewById(R.id.startButton);
        settingsButton = findViewById(R.id.settingsButton);
        logButton = findViewById(R.id.logButton);

        alertPlayer = MediaPlayer.create(this, R.raw.alert);
        logFile = new File(getExternalFilesDir(null), "speed_log.txt");

        loadSpeedLimit();
        checkLocationPermission();

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
                if (!isTracking) return;

                float speedKmph = location.getSpeed() * 3.6f;
                speedTextView.setText(String.format("Hız: %.2f km/h", speedKmph));

                if (speedKmph > maxSpeed) {
                    maxSpeed = speedKmph;
                    maxSpeedTextView.setText(String.format("Maksimum Hız: %.2f km/h", maxSpeed));
                }

                double lat = location.getLatitude();
                double lng = location.getLongitude();
                latLngTextView.setText("Konum: " + lat + ", " + lng);

                if (speedKmph >= speedLimit) {
                    speedTextView.setTextColor(Color.RED);
                    if (!alertPlayer.isPlaying()) alertPlayer.start();
                } else {
                    speedTextView.setTextColor(Color.parseColor("#FFC107"));
                    if (alertPlayer.isPlaying()) alertPlayer.pause();
                }

                try (FileWriter writer = new FileWriter(logFile, true)) {
                    writer.append(String.format("Hız: %.2f - Konum: %.5f, %.5f\n", speedKmph, lat, lng));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };

        startButton.setOnClickListener(v -> {
            if (!isTracking) {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED) {
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, locationListener);
                    isTracking = true;
                    startButton.setText("Dur");
                    Toast.makeText(this, "Hız takibi başlatıldı.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Konum izni gerekli!", Toast.LENGTH_SHORT).show();
                }
            } else {
                locationManager.removeUpdates(locationListener);
                isTracking = false;
                startButton.setText("Başla");
                speedTextView.setText("Hız: 0.00 km/h");
                latLngTextView.setText("Konum: -");
                maxSpeedTextView.setText("Maksimum Hız: 0.00 km/h");
                maxSpeed = 0f;
                if (alertPlayer.isPlaying()) alertPlayer.pause();
                Toast.makeText(this, "Takip durduruldu.", Toast.LENGTH_SHORT).show();
            }
        });

        settingsButton.setOnClickListener(v ->
                startActivity(new Intent(this, SettingsActivity.class)));

        logButton.setOnClickListener(v ->
                startActivity(new Intent(this, LogActivity.class)));
    }

    private void loadSpeedLimit() {
        SharedPreferences prefs = getSharedPreferences("settings", MODE_PRIVATE);
        speedLimit = prefs.getFloat("speed_limit", 80f);
    }

    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Konum izni verildi.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Konum izni reddedildi!", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
