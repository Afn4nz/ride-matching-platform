package com.ridematch.driver.configuration;

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
    Queue matchingQueue() {
        return QueueBuilder.durable(rabbitConfigProperties.getQueueMatching()).build();
    }

    @Bean
    Queue availabilityQueue() {
        return QueueBuilder.durable(rabbitConfigProperties.getQueueAvailability()).build();
    }

    @Bean
    Binding bindRideRequested() {
        return BindingBuilder.bind(matchingQueue())
                .to(rideEventsExchange())
                .with(rabbitConfigProperties.getRoutingRideRequested());
    }

    @Bean
    Binding bindRideCanceled() {
        return BindingBuilder.bind(availabilityQueue())
                .to(rideEventsExchange())
                .with(rabbitConfigProperties.getRoutingRideCanceled());
    }

    @Bean
    Binding bindRideCompleted() {
        return BindingBuilder.bind(availabilityQueue())
                .to(rideEventsExchange())
                .with(rabbitConfigProperties.getRoutingRideCompleted());
    }

    @Bean
    Binding bindRideCanceledToAvailability() {
        return BindingBuilder.bind(availabilityQueue())
                .to(rideEventsExchange())
                .with(rabbitConfigProperties.getRoutingRideCanceled());
    }

    @Bean
    Binding bindRideCompletedToAvailability() {
        return BindingBuilder.bind(availabilityQueue())
                .to(rideEventsExchange())
                .with(rabbitConfigProperties.getRoutingRideCompleted());
    }

    @Bean
    public MessageConverter converter() {
        return new Jackson2JsonMessageConverter();
    }
}
