package com.ridematch.driver.consumer;

import com.ridematch.driver.event.RideCompletedEvent;
import com.ridematch.driver.event.RideRequestedEvent;
import com.ridematch.driver.service.DriverService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class RideListener {
    private final DriverService driverService;

    @RabbitListener(queues = "${rabbitmq.queue-matching}")
    public void onRequested(RideRequestedEvent rideRequestedEvent) {
        try {
            log.info("RideListener | onRequested with id: {}", rideRequestedEvent.getRideId());
            driverService.assignDriverForRide(
                    rideRequestedEvent.getRideId(),
                    rideRequestedEvent.getPickUp().getLatitude().doubleValue(),
                    rideRequestedEvent.getPickUp().getLongitude().doubleValue());
        } catch (Exception exception) {
            log.info("RideListener |  onRequested | Field to consume RideRequestedEvent ");
        }
    }

    @RabbitListener(queues = "${rabbitmq.queue-availability}")
    public void onCanceledORCompleted(RideCompletedEvent rideCompletedEvent) {
        try {
            log.info(
                    "RideListener | onCanceledORCompleted with id: {}",
                    rideCompletedEvent.getRideId());
            driverService.releaseDriverForRide(rideCompletedEvent.getRideId());
        } catch (Exception exception) {
            log.info(
                    "RideListener |  onCanceledORCompleted | Field to consume RideRequestedEvent ");
        }
    }
}
