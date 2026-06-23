package com.sensormonitoring.warehouse.publisher;

import com.sensormonitoring.common.SensorReading;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class MeasurementPublisher {

    private final KafkaTemplate<String, SensorReading> kafkaTemplate;
    private final String topic;

    public MeasurementPublisher(KafkaTemplate<String, SensorReading> kafkaTemplate,
                                @Value("${warehouse.kafka.topic}") String topic) {
        this.kafkaTemplate = kafkaTemplate;
        this.topic = topic;
    }

    public void publish(SensorReading reading) {
        kafkaTemplate.send(topic, key(reading), reading);
    }

    /** Partition key: groups by physical sensor (warehouse + sensor). */
    public static String key(SensorReading reading) {
        return reading.warehouseId() + ":" + reading.sensorId();
    }
}
