package com.example.speedometer;

import java.io.Serializable; // <-- Bu satır eksikti, eklendi.

public class LocationPoint implements Serializable {
    public double latitude;
    public double longitude;

    public LocationPoint() {}

    public LocationPoint(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }
}
