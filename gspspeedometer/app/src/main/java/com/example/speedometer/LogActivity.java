package com.example.speedometer;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class LogActivity extends AppCompatActivity {

    private TextView logTextView;
    private Button backToMainButton, clearLogButton;
    private File logFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log);

        logTextView = findViewById(R.id.logTextView);
        backToMainButton = findViewById(R.id.backToMainButton);
        clearLogButton = findViewById(R.id.clearLogButton);
        logFile = new File(getExternalFilesDir(null), "speed_log.txt");

        loadLogData();

        backToMainButton.setOnClickListener(v -> {
            Intent intent = new Intent(LogActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
        });

        clearLogButton.setOnClickListener(v -> {
            if (logFile.exists()) {
                logFile.delete();
                logTextView.setText("Kayıtlar silindi.");
            } else {
                logTextView.setText("Zaten kayıt yok.");
            }
        });
    }

    private void loadLogData() {
        if (logFile.exists()) {
            try (FileInputStream fis = new FileInputStream(logFile)) {
                byte[] data = new byte[(int) logFile.length()];
                fis.read(data);
                logTextView.setText(new String(data));
            } catch (IOException e) {
                logTextView.setText("Veriler okunamadı.");
                e.printStackTrace();
            }
        } else {
            logTextView.setText("Henüz kayıtlı veri yok.");
        }
    }
}
