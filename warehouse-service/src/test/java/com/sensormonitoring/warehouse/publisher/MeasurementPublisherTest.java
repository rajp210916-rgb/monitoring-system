package com.sensormonitoring.warehouse.publisher;

import com.sensormonitoring.common.SensorReading;
import com.sensormonitoring.common.SensorType;
import org.junit.jupiter.api.Test;
import org.springframework.kafka.core.KafkaTemplate;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@SuppressWarnings("unchecked")
class MeasurementPublisherTest {

    @Test
    void publishesReadingToConfiguredTopicKeyedByWarehouseAndSensor() {
        KafkaTemplate<String, SensorReading> template = mock(KafkaTemplate.class);
        MeasurementPublisher publisher = new MeasurementPublisher(template, "sensor-measurements");

        SensorReading reading = new SensorReading("WH-1", "t1", SensorType.TEMPERATURE, 30.0,
            Instant.parse("2026-01-01T00:00:00Z"));
        publisher.publish(reading);

        verify(template).send("sensor-measurements", "WH-1:t1", reading);
    }

    @Test
    void keyDisambiguatesSameSensorIdAcrossWarehouses() {
        Instant t = Instant.parse("2026-01-01T00:00:00Z");
        SensorReading a = new SensorReading("WH-1", "t1", SensorType.TEMPERATURE, 30.0, t);
        SensorReading b = new SensorReading("WH-2", "t1", SensorType.TEMPERATURE, 30.0, t);

        assertThat(MeasurementPublisher.key(a)).isEqualTo("WH-1:t1");
        assertThat(MeasurementPublisher.key(b)).isEqualTo("WH-2:t1");
        assertThat(MeasurementPublisher.key(a)).isNotEqualTo(MeasurementPublisher.key(b));
    }
}
