package com.ridematch.ride.configuration;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class RabbitConfig {
    private final RabbitConfigProperties rabbitConfigProperties;

    @Bean
    TopicExchange rideEventsExchange() {
        return new TopicExchange(rabbitConfigProperties.getExchangeRideEvents(), true, false);
    }

    @Bean
    Queue assignmentQueue() {
        return QueueBuilder.durable(rabbitConfigProperties.getQueueAssignment()).build();
    }

    @Bean
    public MessageConverter converter() {
        return new Jackson2JsonMessageConverter();
    }
}
