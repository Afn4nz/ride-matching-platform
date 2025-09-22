package com.ridematch.ride.event;

import com.ridematch.ride.entity.Location;
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
