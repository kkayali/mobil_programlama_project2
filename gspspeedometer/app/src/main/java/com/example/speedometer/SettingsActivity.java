package com.example.speedometer;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

public class SettingsActivity extends AppCompatActivity {

    private EditText speedLimitInput;
    private Button saveButton;
    private Switch themeSwitch;

    public static final String PREFS_NAME = "SpeedometerPrefs";
    public static final String SPEED_LIMIT_KEY = "speed_limit";
    public static final String DARK_MODE_KEY = "dark_mode";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        boolean darkMode = prefs.getBoolean(DARK_MODE_KEY, false);
        AppCompatDelegate.setDefaultNightMode(
                darkMode ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO
        );

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        speedLimitInput = findViewById(R.id.speedLimitInput);
        saveButton = findViewById(R.id.saveButton);
        themeSwitch = findViewById(R.id.themeSwitch);


        float savedLimit = prefs.getFloat(SPEED_LIMIT_KEY, 80f);
        speedLimitInput.setText(String.valueOf((int) savedLimit));


        themeSwitch.setChecked(darkMode);


        saveButton.setOnClickListener(v -> {
            String input = speedLimitInput.getText().toString().trim();
            if (!input.isEmpty()) {
                try {
                    float limit = Float.parseFloat(input);
                    prefs.edit().putFloat(SPEED_LIMIT_KEY, limit).apply();
                    Toast.makeText(this, "Hız limiti kaydedildi!", Toast.LENGTH_SHORT).show();
                    finish();
                } catch (NumberFormatException e) {
                    Toast.makeText(this, "Geçerli bir sayı girin!", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Lütfen bir hız limiti girin!", Toast.LENGTH_SHORT).show();
            }
        });


        themeSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            prefs.edit().putBoolean(DARK_MODE_KEY, isChecked).apply();

            AppCompatDelegate.setDefaultNightMode(
                    isChecked ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO
            );


            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finishAffinity();
        });
    }
}
