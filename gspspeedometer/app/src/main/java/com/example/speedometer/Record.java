package com.example.speedometer;

import java.util.List;
import com.example.speedometer.LocationPoint;
import java.util.List;


public class Record {
    public String id;
    public float average_speed;
    public float max_speed;
    public float duration_hours;
    public float distance_km;
    public long timestamp;
    public List<LocationPoint> locations;

    public Record() {}

    public Record(String id, float avg, float max, float dur, float dist, long ts, List<LocationPoint> locs) {
        this.id = id;
        this.average_speed = avg;
        this.max_speed = max;
        this.duration_hours = dur;
        this.distance_km = dist;
        this.timestamp = ts;
        this.locations = locs;
    }
}