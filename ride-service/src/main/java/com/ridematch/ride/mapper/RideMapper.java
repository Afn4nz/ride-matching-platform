package com.ridematch.ride.mapper;

import com.ridematch.ride.dto.RideRequest;
import com.ridematch.ride.entity.Ride;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface RideMapper {

    @Mapping(target = "status", expression = "java(com.ridematch.ride.enums.RideStatus.REQUESTED)")
    Ride mapToEntity(RideRequest rideRequest);
}
