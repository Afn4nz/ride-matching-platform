package com.ridematch.ride.service;

import com.ridematch.ride.dto.DriverAssignmentDTO;
import com.ridematch.ride.dto.RideRequest;
import com.ridematch.ride.entity.Ride;
import com.ridematch.ride.enums.RideStatus;
import com.ridematch.ride.event.RideCanceledEvent;
import com.ridematch.ride.mapper.RideMapper;
import com.ridematch.ride.repository.RideRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RideService {

    private final RideMapper rideMapper;
    private final RideRepository rideRepository;
    private final ApplicationEventPublisher events;

    @Transactional
    public void requestRide(RideRequest request) {
        Ride ride = rideMapper.mapToEntity(request);
        rideRepository.save(ride);
        events.publishEvent(rideMapper.mapToRideRequestedEvent(ride));
    }

    // TODO: Validate that the ride belongs to the requested user
    public void cancelRide(Long id) {
        Ride ride = rideRepository.findById(id).get();
        if (!ride.getStatus().equals(RideStatus.REQUESTED)) {
            throw new IllegalStateException("Ride cannot be canceled");
        }

        ride.setStatus(RideStatus.CANCELED);
        rideRepository.save(ride);
        events.publishEvent(new RideCanceledEvent(ride.getId()));
    }

    @Transactional
    public void confirmAssigned(DriverAssignmentDTO driverAssignmentDTO) {
        Ride ride = rideRepository.findById(driverAssignmentDTO.getRideId()).get();
        if (ride.getStatus() == RideStatus.CANCELED) {
            events.publishEvent(new RideCanceledEvent(driverAssignmentDTO.getRideId()));
        }

        if (ride.getStatus() == RideStatus.COMPLETED) {
            events.publishEvent(rideMapper.mapToRideCompletedEvent(ride));
        }

        if (ride.getStatus() == RideStatus.REQUESTED) {
            ride.setStatus(RideStatus.ASSIGNED);
            ride.setDriverId(driverAssignmentDTO.getDriverId());
            rideRepository.save(ride);
        }
    }

    @Transactional
    public void endRide(Long rideId, Long driverId) {
        Ride ride = rideRepository.findById(rideId).get();
        if (ride.getDriverId() == null || !ride.getDriverId().equals(driverId)) {
            throw new IllegalStateException("Driver mismatch");
        }

        if (ride.getStatus() == RideStatus.CANCELED) {
            throw new IllegalStateException("Ride already canceled");
        }

        if (ride.getStatus() != RideStatus.COMPLETED) {
            ride.setStatus(RideStatus.COMPLETED);
            events.publishEvent(rideMapper.mapToRideCompletedEvent(ride));
        }
    }
}
