package com.sensormonitoring.warehouse.udp;

import com.sensormonitoring.common.SensorReading;
import com.sensormonitoring.common.SensorType;
import com.sensormonitoring.warehouse.parser.MeasurementParser;
import com.sensormonitoring.warehouse.publisher.MeasurementPublisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.nio.charset.StandardCharsets;


public class UdpListener implements Runnable {

    private static final Logger log = LoggerFactory.getLogger(UdpListener.class);
    private static final int BUFFER_SIZE = 1024;

    private final int port;
    private final SensorType type;
    private final MeasurementParser parser;
    private final MeasurementPublisher publisher;

    private volatile boolean running = true;
    private DatagramSocket socket;
    private Thread thread;

    public UdpListener(int port, SensorType type,
                       MeasurementParser parser, MeasurementPublisher publisher) {
        this.port = port;
        this.type = type;
        this.parser = parser;
        this.publisher = publisher;
    }

    public void start() {
        try {
            this.socket = new DatagramSocket(port);
        } catch (Exception e) {
            throw new IllegalStateException("Could not bind UDP port " + port, e);
        }
        this.thread = new Thread(this, "udp-listener-" + type.name().toLowerCase());
        this.thread.setDaemon(false);
        this.thread.start();
        log.info("Listening for {} measurements on UDP port {}", type, port);
    }

    @Override
    public void run() {
        byte[] buffer = new byte[BUFFER_SIZE];
        while (running) {
            try {
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                socket.receive(packet);
                String raw = new String(packet.getData(), 0, packet.getLength(), StandardCharsets.UTF_8).trim();
                handle(raw);
            } catch (Exception e) {
                if (running) {
                    log.warn("Error receiving UDP packet on port {}: {}", port, e.getMessage());
                }
            }
        }
    }

    private void handle(String raw) {
        try {
            SensorReading reading = parser.parse(raw, type);
            publisher.publish(reading);
            log.info("Received and published: {}", reading);
        } catch (IllegalArgumentException e) {
            log.warn("Ignoring malformed measurement '{}': {}", raw, e.getMessage());
        }
    }

    public void stop() {
        running = false;
        if (socket != null) {
            socket.close();
        }
    }
}
