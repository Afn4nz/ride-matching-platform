package com.ridematch.ride.consumer;

import com.ridematch.ride.dto.DriverAssignmentDTO;
import com.ridematch.ride.service.RideService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class DriverAssignedListener {
    private final RideService rideService;

    @RabbitListener(queues = "${rabbitmq.queue-assignment}")
    public void onDriverAssigned(DriverAssignmentDTO driverAssignmentDTO) {
        log.info(
                "DriverAssignedListener |  onDriverAssigned | ride: {}, driver: {}",
                driverAssignmentDTO.getRideId(),
                driverAssignmentDTO.getDriverId());
        rideService.confirmAssigned(driverAssignmentDTO);
    }
}
