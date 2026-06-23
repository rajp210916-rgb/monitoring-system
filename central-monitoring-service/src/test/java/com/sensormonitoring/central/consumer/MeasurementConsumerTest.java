package com.sensormonitoring.central.consumer;

import com.sensormonitoring.central.alarm.AlarmService;
import com.sensormonitoring.common.SensorReading;
import com.sensormonitoring.common.SensorType;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class MeasurementConsumerTest {

    @Test
    void forwardsReadingToAlarmService() {
        AlarmService alarmService = mock(AlarmService.class);
        MeasurementConsumer consumer = new MeasurementConsumer(alarmService);

        SensorReading reading = new SensorReading("WH-1", "t1", SensorType.TEMPERATURE, 36,
            Instant.parse("2026-01-01T00:00:00Z"));
        consumer.onMeasurement(reading);

        verify(alarmService).evaluate(reading);
    }
}
