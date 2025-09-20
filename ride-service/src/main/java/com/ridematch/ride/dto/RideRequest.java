package com.ridematch.ride.dto;

import com.ridematch.ride.entity.Location;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RideRequest {
    private Location pickUp;
    private Location dropOff;
}
