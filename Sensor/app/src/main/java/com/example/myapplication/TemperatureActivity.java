package com.example.myapplication;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class ThermometerActivity extends AppCompatActivity {

    private SensorManager sensorManager;
    private Sensor thermometer;
    private SensorEventListener listener;
    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.thermometer);

        textView = findViewById(R.id.textView_the);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        //thermometer = sensorManager.getDefaultSensor(Sensor.TYPE_THERMOMETER); DONT KNOW HOW

        listener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                //content
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) { }
        };
    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(listener, thermometer, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(listener);
    }
}
