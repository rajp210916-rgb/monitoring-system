package com.sensormonitoring.central.consumer;

import com.sensormonitoring.central.alarm.AlarmService;
import com.sensormonitoring.common.SensorReading;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;


@Component
public class MeasurementConsumer {

    private final AlarmService alarmService;

    public MeasurementConsumer(AlarmService alarmService) {
        this.alarmService = alarmService;
    }

    @KafkaListener(
        topics = "${monitoring.kafka.topic}",
        groupId = "${spring.kafka.consumer.group-id}")
    public void onMeasurement(SensorReading reading) {
        alarmService.evaluate(reading);
    }
}
