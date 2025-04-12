package com.example.speedometer;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.widget.Button;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;

import java.io.File;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class RecordsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecordAdapter adapter;
    private List<Record> recordList;
    private Button btnBack, btnExport;
    private DatabaseReference databaseRef;
    private LineChart lineChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_records);

        btnBack = findViewById(R.id.btnBack);
        btnExport = findViewById(R.id.btnExport);
        recyclerView = findViewById(R.id.recordsRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recordList = new ArrayList<>();
        adapter = new RecordAdapter(this, recordList);
        recyclerView.setAdapter(adapter);

        lineChart = findViewById(R.id.lineChart);

        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        databaseRef = FirebaseDatabase.getInstance().getReference("users")
                .child(userId).child("records");

        databaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                recordList.clear();
                List<Entry> entries = new ArrayList<>();
                int index = 0;

                for (DataSnapshot snap : snapshot.getChildren()) {
                    Record record = snap.getValue(Record.class);
                    if (record != null) {
                        record.id = snap.getKey();
                        recordList.add(record);
                        entries.add(new Entry(index++, record.average_speed));
                    }
                }

                adapter.notifyDataSetChanged();

                LineDataSet dataSet = new LineDataSet(entries, "Ortalama Hız (km/h)");
                dataSet.setColor(getResources().getColor(R.color.teal_700));
                dataSet.setValueTextColor(getResources().getColor(R.color.white));
                dataSet.setCircleColor(getResources().getColor(R.color.teal_200));

                LineData lineData = new LineData(dataSet);
                lineChart.setData(lineData);

                Description desc = new Description();
                desc.setText("Ortalama Hız Geçmişi");
                desc.setTextColor(getResources().getColor(R.color.white));
                lineChart.setDescription(desc);
                lineChart.invalidate();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(RecordsActivity.this, "Veri alınamadı!", Toast.LENGTH_SHORT).show();
            }
        });

        btnBack.setOnClickListener(v -> {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        });

        btnExport.setOnClickListener(v -> exportToCSV());
    }

    private void exportToCSV() {
        if (recordList.isEmpty()) {
            Toast.makeText(this, "Dışa aktarılacak veri yok!", Toast.LENGTH_SHORT).show();
            return;
        }

        StringBuilder data = new StringBuilder();
        data.append("Tarih,Ortalama Hız (km/h),Maksimum Hız (km/h),Süre (saat),Mesafe (km)\n");

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.getDefault());

        for (Record record : recordList) {
            String date = dateFormat.format(new Date(record.timestamp));
            data.append(date).append(",")
                    .append(record.average_speed).append(",")
                    .append(record.max_speed).append(",")
                    .append(record.duration_hours).append(",")
                    .append(record.distance_km).append("\n");
        }

        try {
            String fileName = "SpeedometerRecords_" + System.currentTimeMillis() + ".csv";
            File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            File file = new File(path, fileName);

            FileWriter fileWriter = new FileWriter(file);
            fileWriter.write(data.toString());
            fileWriter.close();

            Toast.makeText(this, "CSV dosyası oluşturuldu:\n" + file.getAbsolutePath(), Toast.LENGTH_LONG).show();

        } catch (Exception e) {
            Toast.makeText(this, "Hata oluştu: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}