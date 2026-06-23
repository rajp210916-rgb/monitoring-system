package com.sensormonitoring.warehouse.parser;

import com.sensormonitoring.common.SensorReading;
import com.sensormonitoring.common.SensorType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Clock;

@Component
public class MeasurementParser {

    private final String warehouseId;
    private final Clock clock;

    public MeasurementParser(@Value("${warehouse.id}") String warehouseId, Clock clock) {
        this.warehouseId = warehouseId;
        this.clock = clock;
    }

    public SensorReading parse(String raw, SensorType type) {
        if (raw == null || raw.isBlank()) {
            throw new IllegalArgumentException("Measurement is empty");
        }

        String sensorId = null;
        Double value = null;

        for (String part : raw.split(";")) {
            String[] kv = part.trim().split("=", 2);
            if (kv.length != 2) {
                continue;
            }
            String key = kv[0].trim();
            String val = kv[1].trim();
            switch (key) {
                case "sensor_id" -> sensorId = val;
                case "value" -> value = parseValue(val);
                default -> { /* ignore unknown fields */ }
            }
        }

        if (sensorId == null || sensorId.isBlank()) {
            throw new IllegalArgumentException("Missing sensor_id in: " + raw);
        }
        if (value == null) {
            throw new IllegalArgumentException("Missing value in: " + raw);
        }
        return new SensorReading(warehouseId, sensorId, type, value, clock.instant());
    }

    private double parseValue(String val) {
        try {
            return Double.parseDouble(val);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid numeric value: " + val, e);
        }
    }
}
