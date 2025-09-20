package com.ridematch.ride.configuration;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class RabbitConfig {
    private final RabbitConfigProperties rabbitConfigProperties;

    @Bean
    public ConnectionFactory connectionFactory() {
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory();
        connectionFactory.setHost(rabbitConfigProperties.getHost());
        connectionFactory.setPort(rabbitConfigProperties.getPort());
        connectionFactory.setUsername(rabbitConfigProperties.getUser());
        connectionFactory.setPassword(rabbitConfigProperties.getPass());
        connectionFactory.setPublisherConfirmType(CachingConnectionFactory.ConfirmType.CORRELATED);
        connectionFactory.setPublisherReturns(true);
        return connectionFactory;
    }

    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean(name = "rideRabbitTemplate")
    public AmqpTemplate rabbitTemplate(ConnectionFactory cf, MessageConverter mc) {
        RabbitTemplate rt = new RabbitTemplate(cf);
        rt.setMessageConverter(mc);
        rt.setMandatory(true);
        return rt;
    }

    @Bean
    Queue rideQueue() {
        return QueueBuilder.durable(rabbitConfigProperties.getRideQueueName()).build();
    }

    @Bean
    DirectExchange rideDirectExchange() {
        return new DirectExchange(rabbitConfigProperties.getRideExchangeName());
    }

    @Bean
    Binding rideBinding() {
        return BindingBuilder.bind((rideQueue()))
                .to(rideDirectExchange())
                .with(rabbitConfigProperties.getRideRoutingKey());
    }
}
