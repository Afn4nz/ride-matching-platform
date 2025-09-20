package com.ridematch.ride.entity;

import com.ridematch.ride.enums.RideStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table
public class Ride extends AuditEntity {
    @Embedded
    @AttributeOverrides({
        @AttributeOverride(
                name = "latitude",
                column = @Column(name = "pickup_lat", precision = 9, scale = 6, nullable = false)),
        @AttributeOverride(
                name = "longitude",
                column = @Column(name = "pickup_lng", precision = 9, scale = 6, nullable = false))
    })
    private Location pickUp;

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(
                name = "latitude",
                column = @Column(name = "dropoff_lat", precision = 9, scale = 6, nullable = false)),
        @AttributeOverride(
                name = "longitude",
                column = @Column(name = "dropoff_lng", precision = 9, scale = 6, nullable = false))
    })
    private Location dropOff;

    private Long driverId;
    private Long riderId;

    @Enumerated(EnumType.STRING)
    private RideStatus status;
}
