package com.ridematch.driver.event;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RideCompletedEvent {
    private Long rideId;
    private Long driverId;
}
