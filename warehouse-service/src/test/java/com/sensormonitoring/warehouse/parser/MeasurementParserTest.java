package com.sensormonitoring.warehouse.parser;

import com.sensormonitoring.common.SensorReading;
import com.sensormonitoring.common.SensorType;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class MeasurementParserTest {

    private static final Instant NOW = Instant.parse("2026-01-01T10:15:30Z");
    private final Clock clock = Clock.fixed(NOW, ZoneOffset.UTC);
    private final MeasurementParser parser = new MeasurementParser("WH-1", clock);

    @Test
    void parsesTemperatureMeasurement() {
        SensorReading reading = parser.parse("sensor_id=t1; value=30", SensorType.TEMPERATURE);

        assertThat(reading.sensorId()).isEqualTo("t1");
        assertThat(reading.type()).isEqualTo(SensorType.TEMPERATURE);
        assertThat(reading.value()).isEqualTo(30.0);
    }

    @Test
    void stampsWarehouseIdAndCaptureTime() {
        SensorReading reading = parser.parse("sensor_id=t1; value=30", SensorType.TEMPERATURE);

        assertThat(reading.warehouseId()).isEqualTo("WH-1");
        assertThat(reading.capturedAt()).isEqualTo(NOW);
    }

    @Test
    void parsesHumidityMeasurement() {
        SensorReading reading = parser.parse("sensor_id=h1; value=40", SensorType.HUMIDITY);

        assertThat(reading.sensorId()).isEqualTo("h1");
        assertThat(reading.type()).isEqualTo(SensorType.HUMIDITY);
        assertThat(reading.value()).isEqualTo(40.0);
    }

    @Test
    void handlesDecimalValues() {
        SensorReading reading = parser.parse("sensor_id=t1; value=36.6", SensorType.TEMPERATURE);

        assertThat(reading.value()).isEqualTo(36.6);
    }

    @Test
    void toleratesExtraWhitespace() {
        SensorReading reading = parser.parse("  sensor_id=t1 ;   value=30  ", SensorType.TEMPERATURE);

        assertThat(reading.sensorId()).isEqualTo("t1");
        assertThat(reading.value()).isEqualTo(30.0);
    }

    @Test
    void rejectsMissingValue() {
        assertThatThrownBy(() -> parser.parse("sensor_id=t1", SensorType.TEMPERATURE))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void rejectsMissingSensorId() {
        assertThatThrownBy(() -> parser.parse("value=30", SensorType.TEMPERATURE))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void rejectsNonNumericValue() {
        assertThatThrownBy(() -> parser.parse("sensor_id=t1; value=hot", SensorType.TEMPERATURE))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void rejectsEmptyInput() {
        assertThatThrownBy(() -> parser.parse("   ", SensorType.TEMPERATURE))
            .isInstanceOf(IllegalArgumentException.class);
    }
}
