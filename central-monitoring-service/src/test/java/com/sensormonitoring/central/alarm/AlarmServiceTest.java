package com.sensormonitoring.central.alarm;

import com.sensormonitoring.central.config.ThresholdProperties;
import com.sensormonitoring.common.SensorReading;
import com.sensormonitoring.common.SensorType;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

class AlarmServiceTest {

    private static final Instant T = Instant.parse("2026-01-01T00:00:00Z");

    private final AlarmService alarmService =
        new AlarmService(new ThresholdChecker(new ThresholdProperties(35.0, 50.0)));

    @Test
    void raisesAlarmWhenThresholdExceeded() {
        boolean raised = alarmService.evaluate(
            new SensorReading("WH-1", "t1", SensorType.TEMPERATURE, 40, T));

        assertThat(raised).isTrue();
    }

    @Test
    void doesNotRaiseAlarmWhenWithinThreshold() {
        boolean raised = alarmService.evaluate(
            new SensorReading("WH-1", "h1", SensorType.HUMIDITY, 45, T));

        assertThat(raised).isFalse();
    }
}
