package com.ridematch.driver.configuration;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "rabbitmq")
public class RabbitConfigProperties {
    private String host;
    private int port;
    private String username;
    private String password;

    private String exchangeRideEvents;
    private String queueMatching;
    private String queueAssignment;
    private String queueAvailability;

    private String routingRideRequested;
    private String routingRideCanceled;
    private String routingRideCompleted;
}
