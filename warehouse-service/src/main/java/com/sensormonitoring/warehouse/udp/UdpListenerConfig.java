package com.sensormonitoring.warehouse.udp;

import com.sensormonitoring.common.SensorType;
import com.sensormonitoring.warehouse.parser.MeasurementParser;
import com.sensormonitoring.warehouse.publisher.MeasurementPublisher;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.util.List;


@Configuration
public class UdpListenerConfig {

    private final MeasurementParser parser;
    private final MeasurementPublisher publisher;
    private final int temperaturePort;
    private final int humidityPort;

    private List<UdpListener> listeners = List.of();

    public UdpListenerConfig(MeasurementParser parser,
                             MeasurementPublisher publisher,
                             @Value("${warehouse.udp.temperature-port}") int temperaturePort,
                             @Value("${warehouse.udp.humidity-port}") int humidityPort) {
        this.parser = parser;
        this.publisher = publisher;
        this.temperaturePort = temperaturePort;
        this.humidityPort = humidityPort;
    }

    @PostConstruct
    public void startListeners() {
        listeners = List.of(
            new UdpListener(temperaturePort, SensorType.TEMPERATURE, parser, publisher),
            new UdpListener(humidityPort, SensorType.HUMIDITY, parser, publisher)
        );
        listeners.forEach(UdpListener::start);
    }

    @PreDestroy
    public void stopListeners() {
        listeners.forEach(UdpListener::stop);
    }
}
