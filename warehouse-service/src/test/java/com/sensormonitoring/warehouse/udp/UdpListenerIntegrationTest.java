package com.sensormonitoring.warehouse.udp;

import com.sensormonitoring.common.SensorReading;
import com.sensormonitoring.common.SensorType;
import com.sensormonitoring.warehouse.parser.MeasurementParser;
import com.sensormonitoring.warehouse.publisher.MeasurementPublisher;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;
import java.time.Clock;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;


class UdpListenerIntegrationTest {

    private UdpListener listener;

    @AfterEach
    void tearDown() {
        if (listener != null) {
            listener.stop();
        }
    }

    @Test
    void receivesUdpPacketParsesAndPublishes() throws Exception {
        int port = 13344;
        MeasurementPublisher publisher = mock(MeasurementPublisher.class);
        MeasurementParser parser = new MeasurementParser("WH-1", Clock.systemUTC());
        listener = new UdpListener(port, SensorType.TEMPERATURE, parser, publisher);
        listener.start();

        sendUdp(port, "sensor_id=t1; value=36");

        ArgumentCaptor<SensorReading> captor = ArgumentCaptor.forClass(SensorReading.class);
        verify(publisher, timeout(2000)).publish(captor.capture());

        SensorReading reading = captor.getValue();
        assertThat(reading.warehouseId()).isEqualTo("WH-1");
        assertThat(reading.sensorId()).isEqualTo("t1");
        assertThat(reading.type()).isEqualTo(SensorType.TEMPERATURE);
        assertThat(reading.value()).isEqualTo(36.0);
        assertThat(reading.capturedAt()).isNotNull();
    }

    private void sendUdp(int port, String message) throws Exception {
        try (DatagramSocket socket = new DatagramSocket()) {
            byte[] data = message.getBytes(StandardCharsets.UTF_8);
            DatagramPacket packet = new DatagramPacket(
                data, data.length, InetAddress.getByName("localhost"), port);
            socket.send(packet);
        }
    }
}
