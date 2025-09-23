package com.ridematch.driver.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RideCanceledEvent { //TODO: Move All events to shared lib
    private Long rideId;
}
