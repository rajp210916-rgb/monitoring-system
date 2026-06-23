package com.sensormonitoring.central.alarm;

import com.sensormonitoring.central.config.ThresholdProperties;
import com.sensormonitoring.common.SensorReading;
import com.sensormonitoring.common.SensorType;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

class ThresholdCheckerTest {

    private final ThresholdChecker checker =
        new ThresholdChecker(new ThresholdProperties(35.0, 50.0));

    @Test
    void temperatureAboveThresholdIsBreach() {
        assertThat(checker.isBreached(reading(SensorType.TEMPERATURE, 36))).isTrue();
    }

    @Test
    void temperatureBelowThresholdIsNotBreach() {
        assertThat(checker.isBreached(reading(SensorType.TEMPERATURE, 34))).isFalse();
    }

    @Test
    void temperatureExactlyAtThresholdIsNotBreach() {
        assertThat(checker.isBreached(reading(SensorType.TEMPERATURE, 35))).isFalse();
    }

    @Test
    void humidityAboveThresholdIsBreach() {
        assertThat(checker.isBreached(reading(SensorType.HUMIDITY, 51))).isTrue();
    }

    @Test
    void humidityBelowThresholdIsNotBreach() {
        assertThat(checker.isBreached(reading(SensorType.HUMIDITY, 49))).isFalse();
    }

    @Test
    void humidityExactlyAtThresholdIsNotBreach() {
        assertThat(checker.isBreached(reading(SensorType.HUMIDITY, 50))).isFalse();
    }

    private SensorReading reading(SensorType type, double value) {
        return new SensorReading("WH-1", type == SensorType.TEMPERATURE ? "t1" : "h1", type, value,
            Instant.parse("2026-01-01T00:00:00Z"));
    }
}
