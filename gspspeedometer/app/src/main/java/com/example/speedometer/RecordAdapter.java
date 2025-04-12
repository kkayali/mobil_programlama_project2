package com.example.speedometer;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class RecordAdapter extends RecyclerView.Adapter<RecordAdapter.RecordViewHolder> {

    private List<Record> recordList;
    private Context context;

    public RecordAdapter(Context context, List<Record> records) {
        this.context = context;
        this.recordList = records;
    }

    @NonNull
    @Override
    public RecordViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.record_item, parent, false);
        return new RecordViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecordViewHolder holder, int position) {
        Record record = recordList.get(position);

        String formattedDate = new SimpleDateFormat("dd MMM yyyy HH:mm", Locale.getDefault())
                .format(new Date(record.timestamp));

        String info = "Tarih: " + formattedDate +
                "\nOrtalama Hız: " + record.average_speed + " km/h" +
                "\nMax Hız: " + record.max_speed + " km/h" +
                "\nSüre: " + record.duration_hours + " saat" +
                "\nMesafe: " + record.distance_km + " km";

        holder.recordInfo.setText(info);

        holder.deleteButton.setOnClickListener(v -> {
            String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
            FirebaseDatabase.getInstance().getReference("users")
                    .child(userId).child("records").child(record.id)
                    .removeValue();
            recordList.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, recordList.size());
        });

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, MapActivity.class);
            intent.putExtra("locations", new ArrayList<>(record.locations));
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return recordList.size();
    }

    public static class RecordViewHolder extends RecyclerView.ViewHolder {
        TextView recordInfo;
        Button deleteButton;

        public RecordViewHolder(@NonNull View itemView) {
            super(itemView);
            recordInfo = itemView.findViewById(R.id.recordInfo);
            deleteButton = itemView.findViewById(R.id.deleteButton);
        }
    }
}