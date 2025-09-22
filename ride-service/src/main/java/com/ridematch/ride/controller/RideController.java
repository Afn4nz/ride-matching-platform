package com.ridematch.ride.controller;

import com.ridematch.ride.dto.RideRequest;
import com.ridematch.ride.service.RideService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/rides")
@RequiredArgsConstructor
public class RideController {
    private final RideService rideService;

    @PostMapping
    public void requestRide(@RequestBody RideRequest request) throws Exception {
        rideService.requestRide(request);
    }

    @PatchMapping("/{id}") // TODO: VALIDATE ID
    public void cancelRide(@PathVariable("id") Long id) {
        rideService.cancelRide(id);
    }

    @PostMapping("/{id}/end")
    public void endRide(@PathVariable Long id, @RequestParam Long driverId) {
        rideService.endRide(id, driverId);
    }
}
