package com.ridematch.ride.service;

import com.ridematch.ride.dto.RideRequest;
import com.ridematch.ride.entity.Ride;
import com.ridematch.ride.enums.RideStatus;
import com.ridematch.ride.mapper.RideMapper;
import com.ridematch.ride.producer.RideRequestPublisher;
import com.ridematch.ride.repository.RideRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RideService {

    private final RideMapper rideMapper;
    private final RideRepository rideRepository;
    private final RideRequestPublisher rideRequestPublisher;

    @Transactional
    public void requestRide(RideRequest request) {
        Ride ride = rideMapper.mapToEntity(request);
        rideRepository.save(ride);
        rideRequestPublisher.publish(ride);
    }

    // TODO: Validate that ride belong to the user
    public void cancelRide(Long id) {
        Ride ride = rideRepository.findById(id).get();
        if (!ride.getStatus().equals(RideStatus.REQUESTED)) {
            throw new RuntimeException("Bad request"); // TODO: throw custom exception
        }

        ride.setStatus(RideStatus.CANCELED);
    }
}
