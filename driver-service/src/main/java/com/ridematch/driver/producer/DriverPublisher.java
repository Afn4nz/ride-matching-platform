package com.ridematch.driver.producer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ridematch.driver.configuration.RabbitConfigProperties;
import com.ridematch.driver.dto.DriverAssignmentDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class DriverPublisher {
    private final RabbitTemplate rabbitTemplate;
    private final RabbitConfigProperties rabbitConfigProperties;

    public void publishDriverAssigned(DriverAssignmentDTO driverAssignmentDTO) {
        try {
            log.info(
                    "DriverPublisher | publishDriverAssigned ride id: {}, driver id: {}",
                    driverAssignmentDTO.getRideId(),
                    driverAssignmentDTO.getDriverId());
            rabbitTemplate.convertAndSend(
                    "", rabbitConfigProperties.getQueueAssignment(), driverAssignmentDTO);
        } catch (Exception exception) {
            log.info(
                    "Filed to publish ride id: {}, driver id: {}", driverAssignmentDTO.getRideId());
        }
    }
}
