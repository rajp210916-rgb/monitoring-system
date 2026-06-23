package com.sensormonitoring.central.alarm;

import com.sensormonitoring.central.config.ThresholdProperties;
import com.sensormonitoring.common.SensorReading;
import org.springframework.stereotype.Component;


@Component
public class ThresholdChecker {

    private final ThresholdProperties thresholds;

    public ThresholdChecker(ThresholdProperties thresholds) {
        this.thresholds = thresholds;
    }

    public boolean isBreached(SensorReading reading) {
        double limit = switch (reading.type()) {
            case TEMPERATURE -> thresholds.temperature();
            case HUMIDITY -> thresholds.humidity();
        };
        return reading.value() > limit;
    }
}
