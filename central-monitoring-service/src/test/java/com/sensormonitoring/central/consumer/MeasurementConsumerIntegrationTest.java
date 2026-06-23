package com.sensormonitoring.central.consumer;

import com.sensormonitoring.central.alarm.AlarmService;
import com.sensormonitoring.common.SensorReading;
import com.sensormonitoring.common.SensorType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

import java.time.Duration;
import java.time.Instant;

import static org.awaitility.Awaitility.await;
import static org.mockito.Mockito.verify;

@SpringBootTest
@EmbeddedKafka(partitions = 1, topics = "sensor-measurements")
class MeasurementConsumerIntegrationTest {

    @Autowired
    private KafkaTemplate<String, SensorReading> kafkaTemplate;

    @MockitoSpyBean
    private AlarmService alarmService;

    @Test
    void consumesMeasurementFromKafkaAndEvaluatesIt() {
        SensorReading reading = new SensorReading("WH-1", "t1", SensorType.TEMPERATURE, 40,
            Instant.parse("2026-01-01T00:00:00Z"));

        kafkaTemplate.send("sensor-measurements", reading.warehouseId() + ":" + reading.sensorId(), reading);

        await().atMost(Duration.ofSeconds(10))
            .untilAsserted(() -> verify(alarmService).evaluate(reading));
    }
}
