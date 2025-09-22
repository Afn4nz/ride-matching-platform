package com.ridematch.ride.producer;

import com.ridematch.ride.configuration.RabbitConfigProperties;
import com.ridematch.ride.event.RideCanceledEvent;
import com.ridematch.ride.event.RideCompletedEvent;
import com.ridematch.ride.event.RideRequestedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;
import org.springframework.transaction.event.TransactionPhase;

@Slf4j
@Component
@RequiredArgsConstructor
public class RideEventPublisher {
    private final RabbitTemplate rabbit;
    private final RabbitConfigProperties rabbitConfigProperties;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void publishRideRequested(RideRequestedEvent rideRequestedEvent) {
        log.info("RideEventPublisher | publishRideRequested");

        rabbit.convertAndSend(
                rabbitConfigProperties.getExchangeRideEvents(),
                "ride.requested",
                rideRequestedEvent);
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void publishRideCanceled(RideCanceledEvent rideCanceledEvent) {
        log.info("RideEventPublisher | publishRideCanceled");

        rabbit.convertAndSend(
                rabbitConfigProperties.getExchangeRideEvents(), "ride.canceled", rideCanceledEvent);
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void publishRideCompleted(RideCompletedEvent rideCompletedEvent) {
        log.info("RideEventPublisher | publishRideCompleted");

        rabbit.convertAndSend(
                rabbitConfigProperties.getExchangeRideEvents(),
                "ride.completed",
                rideCompletedEvent);
    }
}
