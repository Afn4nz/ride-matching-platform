package com.ridematch.ride.producer;

import com.ridematch.ride.configuration.RabbitConfigProperties;
import com.ridematch.ride.entity.Ride;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@RequiredArgsConstructor
public class RideRequestPublisher {
    private final RabbitConfigProperties rabbitConfigProperties;
    private final RabbitTemplate rabbitTemplate;

    public void publish(Ride ride) {

        log.info("(RideRequestPublisher) | Ride Received with Details {}", ride);
        CorrelationData crd = new CorrelationData(UUID.randomUUID().toString());
        try {
            rabbitTemplate.convertAndSend(
                    rabbitConfigProperties.getRideExchangeName(),
                    rabbitConfigProperties.getRideRoutingKey(),
                    ride,
                    crd);

            if (crd.getFuture().get(10, TimeUnit.SECONDS).isAck() && crd.getReturned() == null) {
                log.info("(RideRequestPublisher) | Ride {} Submitted Successfully", ride.getId());
            } else {
                log.info("(RideRequestPublisher) | Ride {} Did not submitted ", ride.getId());
            }
        } catch (Exception exception) {
            log.error(
                    "(RideRequestPublisher) | Ride {} Failed with Exception {}",
                    ride.getId(),
                    exception);
        }
    }
}
