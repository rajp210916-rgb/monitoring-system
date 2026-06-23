package com.sensormonitoring.common;

import java.time.Instant;
import java.util.Objects;

public record SensorReading(
        String warehouseId,
        String sensorId,
        SensorType type,
        double value,
        Instant capturedAt) {

    public SensorReading {
        Objects.requireNonNull(warehouseId, "warehouseId must not be null");
        Objects.requireNonNull(sensorId, "sensorId must not be null");
        Objects.requireNonNull(type, "type must not be null");
        Objects.requireNonNull(capturedAt, "capturedAt must not be null");
    }
}
