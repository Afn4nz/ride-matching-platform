package com.ridematch.ride.scheduler;

import com.ridematch.ride.entity.Ride;
import com.ridematch.ride.enums.RideStatus;
import com.ridematch.ride.repository.RideRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class RideScheduler {
    private final RideRepository rideRepository;

    @Value("${ride.expiry.minutes}")
    private int rideExpiryMinutes;

    @Transactional
    @Scheduled(cron = "0 */1 * * * *")
    public void cancelRide() {
        LocalDateTime threshold = LocalDateTime.now().minusMinutes(rideExpiryMinutes);
        rideRepository.cancelExpiredRides(threshold);
    }
}
