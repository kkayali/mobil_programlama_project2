package com.example.speedometer;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class SettingsActivity extends AppCompatActivity {

    private EditText speedLimitInput;
    private Button saveButton;

    public static final String PREFS_NAME = "SpeedometerPrefs";
    public static final String SPEED_LIMIT_KEY = "speed_limit";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        speedLimitInput = findViewById(R.id.speedLimitInput);
        saveButton = findViewById(R.id.saveButton);

        // Önceki limiti göster
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        int savedLimit = prefs.getInt(SPEED_LIMIT_KEY, 80); // default 80
        speedLimitInput.setText(String.valueOf(savedLimit));

        saveButton.setOnClickListener(v -> {
            String input = speedLimitInput.getText().toString().trim();
            if (!input.isEmpty()) {
                int limit = Integer.parseInt(input);
                prefs.edit().putInt(SPEED_LIMIT_KEY, limit).apply();
                Toast.makeText(this, "Hız limiti kaydedildi!", Toast.LENGTH_SHORT).show();
                finish(); // Ayarları kapat
            } else {
                Toast.makeText(this, "Lütfen bir hız limiti girin!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
