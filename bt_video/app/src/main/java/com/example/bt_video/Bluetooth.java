package com.example.bt_video;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

public class Bluetooth extends AppCompatActivity {

    BluetoothAdapter bluetoothAdapter;
    Button btnOpen, btnClose;
    final int REQUEST_ENABLE_BT = 1;
    final int REQUEST_BLUETOOTH_PERMISSION = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth);

        btnOpen = findViewById(R.id.btnOpenBluetooth);
        btnClose = findViewById(R.id.btnCloseBluetooth);
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        btnOpen.setOnClickListener(v -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.BLUETOOTH_CONNECT},
                            REQUEST_BLUETOOTH_PERMISSION);
                    return;
                }
            }

            if (bluetoothAdapter == null) {
                Toast.makeText(this, "Cihazda Bluetooth yok", Toast.LENGTH_SHORT).show();
            } else if (!bluetoothAdapter.isEnabled()) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            } else {
                Toast.makeText(this, "Bluetooth zaten açık", Toast.LENGTH_SHORT).show();
            }
        });

        btnClose.setOnClickListener(v -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.BLUETOOTH_CONNECT},
                            REQUEST_BLUETOOTH_PERMISSION);
                    return;
                }
            }

            if (bluetoothAdapter != null && bluetoothAdapter.isEnabled()) {
                bluetoothAdapter.disable();
                Toast.makeText(this, "Bluetooth kapatıldı", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Bluetooth zaten kapalı", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
