package com.example.speedometer;

public class SpeedRecord {
    public float duration;
    public float distance;
    public float averageSpeed;
    public float maxSpeed;

    public SpeedRecord() {
        // Gerekli boş constructor (Firebase için)
    }

    public SpeedRecord(float duration, float distance, float averageSpeed, float maxSpeed) {
        this.duration = duration;
        this.distance = distance;
        this.averageSpeed = averageSpeed;
        this.maxSpeed = maxSpeed;
    }
}
