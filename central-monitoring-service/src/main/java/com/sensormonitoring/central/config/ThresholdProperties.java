package com.sensormonitoring.central.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "monitoring.thresholds")
public record ThresholdProperties(double temperature, double humidity) {
}
