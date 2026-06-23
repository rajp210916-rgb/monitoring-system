package com.sensormonitoring.common;

import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

class SensorReadingTest {

    @Test
    void holdsItsValues() {
        Instant capturedAt = Instant.parse("2026-01-01T00:00:00Z");
        SensorReading reading = new SensorReading("WH-1", "t1", SensorType.TEMPERATURE, 30.0, capturedAt);

        assertThat(reading.warehouseId()).isEqualTo("WH-1");
        assertThat(reading.sensorId()).isEqualTo("t1");
        assertThat(reading.type()).isEqualTo(SensorType.TEMPERATURE);
        assertThat(reading.value()).isEqualTo(30.0);
        assertThat(reading.capturedAt()).isEqualTo(capturedAt);
    }
}
