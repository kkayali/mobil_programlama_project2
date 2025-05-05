package com.example.bt_video;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class WifiActivity extends AppCompatActivity {

    WifiManager wifiManager;
    Button btnOpenWifi, btnCloseWifi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi);

        btnOpenWifi = findViewById(R.id.btnOpenWifi);
        btnCloseWifi = findViewById(R.id.btnCloseWifi);
        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        btnOpenWifi.setOnClickListener(v -> {
            if (!wifiManager.isWifiEnabled()) {
                wifiManager.setWifiEnabled(true);
                Toast.makeText(this, "Wi-Fi açıldı", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Wi-Fi zaten açık", Toast.LENGTH_SHORT).show();
            }
        });

        btnCloseWifi.setOnClickListener(v -> {
            if (wifiManager.isWifiEnabled()) {
                wifiManager.setWifiEnabled(false);
                Toast.makeText(this, "Wi-Fi kapatıldı", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Wi-Fi zaten kapalı", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
