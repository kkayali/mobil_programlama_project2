package com.example.speedometer;

import java.util.List;
import java.util.ArrayList;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    private LocationManager locationManager;
    private LocationListener locationListener;
    private TextView speedTextView, latLngTextView, maxSpeedTextView;
    private PieChart speedometerChart;
    private Button startButton, settingsButton, logButton, logoutButton;

    private boolean isTracking = false;
    private long startTime;
    private float maxSpeed = 0f;
    private float totalDistance = 0f;
    private Location lastLocation = null;

    private MediaPlayer alertPlayer;
    private File logFile;
    private float speedLimit = 80f;

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private DatabaseReference databaseRef;

    private List<LocationPoint> locationsList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Tema tercihini uygula
        SharedPreferences prefs = getSharedPreferences("SpeedometerPrefs", MODE_PRIVATE);
        boolean darkMode = prefs.getBoolean("dark_mode", false);
        AppCompatDelegate.setDefaultNightMode(
                darkMode ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO
        );

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        if (currentUser == null) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        databaseRef = FirebaseDatabase.getInstance().getReference("users").child(currentUser.getUid());

        speedTextView = findViewById(R.id.speedTextView);
        latLngTextView = findViewById(R.id.latLngTextView);
        maxSpeedTextView = findViewById(R.id.maxSpeedTextView);
        speedometerChart = findViewById(R.id.pieChart);

        startButton = findViewById(R.id.startButton);
        settingsButton = findViewById(R.id.settingsButton);
        logButton = findViewById(R.id.logButton);
        logoutButton = findViewById(R.id.logoutButton);

        alertPlayer = MediaPlayer.create(this, R.raw.alert);
        logFile = new File(getExternalFilesDir(null), "speed_log.txt");

        loadSpeedLimit();
        checkLocationPermission();
        setupSpeedometer(0);

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        locationListener = location -> {
            if (!isTracking) return;

            float speedKmph = location.getSpeed() * 3.6f;
            double lat = location.getLatitude();
            double lng = location.getLongitude();

            // Hız ve konum bilgilerini güncelle
            speedTextView.setText(String.format("Hız: %.2f km/h", speedKmph));
            latLngTextView.setText("Konum: " + lat + ", " + lng);
            updateSpeedometer(speedKmph);

            // Hız limiti kontrolü ve uyarı sesi
            if (speedKmph >= speedLimit) {
                speedTextView.setTextColor(Color.RED);
                if (!alertPlayer.isPlaying()) alertPlayer.start();
            } else {
                speedTextView.setTextColor(Color.parseColor("#FFC107"));
                if (alertPlayer.isPlaying()) alertPlayer.pause();
            }

            // Maksimum hızı güncelle
            if (speedKmph > maxSpeed) {
                maxSpeed = speedKmph;
                maxSpeedTextView.setText(String.format("Maksimum Hız: %.2f km/h", maxSpeed));
            }

            // Mesafe hesaplama
            if (lastLocation != null) {
                totalDistance += location.distanceTo(lastLocation) / 1000f;
            }
            lastLocation = location;

            // Lokasyonları listeye ekle
            locationsList.add(new LocationPoint(lat, lng));

            // Hız ve konum bilgisini log dosyasına yaz
            try (FileWriter writer = new FileWriter(logFile, true)) {
                writer.append(String.format("Hız: %.2f - Konum: %.5f, %.5f\n", speedKmph, lat, lng));
            } catch (IOException e) {
                e.printStackTrace();
            }
        };

        startButton.setOnClickListener(v -> {
            if (!isTracking) {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED) {
                    locationsList.clear();
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, locationListener);
                    isTracking = true;
                    startTime = System.currentTimeMillis();
                    maxSpeed = 0f;
                    totalDistance = 0f;
                    lastLocation = null;
                    updateSpeedometer(0);
                    maxSpeedTextView.setText("Maksimum Hız: 0.0 km/h");
                    startButton.setText("Dur");
                    Toast.makeText(this, "Takip başladı", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Konum izni gerekli!", Toast.LENGTH_SHORT).show();
                }
            } else {
                locationManager.removeUpdates(locationListener);
                isTracking = false;
                long durationMillis = System.currentTimeMillis() - startTime;
                float hours = durationMillis / (1000f * 60 * 60);
                float avgSpeed = totalDistance / hours;

                saveToFirebase(avgSpeed, maxSpeed, hours, totalDistance, locationsList);

                startButton.setText("Başla");
                speedTextView.setText("Hız: 0.00 km/h");
                maxSpeedTextView.setText("Maksimum Hız: 0.0 km/h");
                latLngTextView.setText("Konum: -");
                updateSpeedometer(0);
                if (alertPlayer.isPlaying()) alertPlayer.pause();
                Toast.makeText(this, "Takip durduruldu", Toast.LENGTH_SHORT).show();
            }
        });

        settingsButton.setOnClickListener(v -> startActivity(new Intent(this, SettingsActivity.class)));
        logButton.setOnClickListener(v -> startActivity(new Intent(this, RecordsActivity.class)));
        logoutButton.setOnClickListener(v -> {
            mAuth.signOut();
            Toast.makeText(MainActivity.this, "Çıkış yapıldı", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            finish();
        });
    }

    private void updateSpeedometer(float speed) {
        ArrayList<PieEntry> entries = new ArrayList<>();
        float cappedSpeed = Math.min(speed, 200f);
        entries.add(new PieEntry(cappedSpeed));
        entries.add(new PieEntry(200f - cappedSpeed));
        PieDataSet dataSet = new PieDataSet(entries, "");
        dataSet.setColors(Color.GREEN, Color.DKGRAY);
        dataSet.setDrawValues(false);
        PieData pieData = new PieData(dataSet);
        speedometerChart.setData(pieData);
        speedometerChart.invalidate();
    }

    private void setupSpeedometer(float initialSpeed) {
        speedometerChart.setUsePercentValues(false);
        speedometerChart.getDescription().setEnabled(false);
        speedometerChart.setDrawHoleEnabled(true);
        speedometerChart.setHoleColor(Color.TRANSPARENT);
        speedometerChart.setTransparentCircleRadius(40f);
        speedometerChart.setDrawEntryLabels(false);
        speedometerChart.getLegend().setEnabled(false);
        updateSpeedometer(initialSpeed);
    }

    private void saveToFirebase(float avgSpeed, float maxSpeed, float hours, float distance, List<LocationPoint> locs) {
        String recordId = databaseRef.child("records").push().getKey();
        if (recordId == null) return;

        Record record = new Record(recordId, avgSpeed, maxSpeed, hours, distance, System.currentTimeMillis(), locs);
        databaseRef.child("records").child(recordId).setValue(record);
    }

    private void loadSpeedLimit() {
        SharedPreferences prefs = getSharedPreferences("SpeedometerPrefs", MODE_PRIVATE);

        try {
            speedLimit = prefs.getFloat("speed_limit", 80f);
        } catch (ClassCastException e) {
            // Eski kaydedilmiş int değeri varsa, güvenli şekilde dönüştür
            int intLimit = prefs.getInt("speed_limit", 80);
            speedLimit = (float) intLimit;
            prefs.edit().putFloat("speed_limit", speedLimit).apply(); // float olarak tekrar kaydet
        }
    }

    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        }
    }
}
