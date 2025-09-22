package com.ridematch.driver.event;

import com.ridematch.driver.dto.Location;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class RideRequestedEvent {
    private Long rideId;
    private Long riderId;
    private Location pickUp;
    private Location dropOff;
}
