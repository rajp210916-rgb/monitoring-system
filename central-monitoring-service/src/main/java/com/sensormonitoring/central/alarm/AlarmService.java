package com.sensormonitoring.central.alarm;

import com.sensormonitoring.common.SensorReading;
import com.sensormonitoring.common.SensorType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;


@Service
public class AlarmService {

    private static final Logger log = LoggerFactory.getLogger(AlarmService.class);

    private final ThresholdChecker thresholdChecker;

    public AlarmService(ThresholdChecker thresholdChecker) {
        this.thresholdChecker = thresholdChecker;
    }


    public boolean evaluate(SensorReading reading) {
        if (thresholdChecker.isBreached(reading)) {
            raiseAlarm(reading);
            return true;
        }
        log.info("OK: warehouse '{}' {} sensor '{}' = {}{} at {}",
            reading.warehouseId(), reading.type(), reading.sensorId(),
            reading.value(), unit(reading.type()), reading.capturedAt());
        return false;
    }

    private void raiseAlarm(SensorReading reading) {
        log.warn("ALARM! warehouse '{}' {} sensor '{}' reported {}{} at {} which exceeds the configured threshold",
            reading.warehouseId(), reading.type(), reading.sensorId(),
            reading.value(), unit(reading.type()), reading.capturedAt());
    }

    private String unit(SensorType type) {
        return switch (type) {
            case TEMPERATURE -> "C";
            case HUMIDITY -> "%";
        };
    }
}
