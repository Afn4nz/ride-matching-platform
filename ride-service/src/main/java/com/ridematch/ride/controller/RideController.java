package com.ridematch.ride.controller;

import com.ridematch.ride.dto.ApiResponse;
import com.ridematch.ride.dto.RideRequest;
import com.ridematch.ride.entity.Ride;
import com.ridematch.ride.service.RideService;
import com.ridematch.ride.validation.ValidId;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@RestController
@RequestMapping("/rides")
@RequiredArgsConstructor
public class RideController {
    private final RideService rideService;

    @PreAuthorize("hasRole('RIDER')")
    @PostMapping
    public ResponseEntity<?> requestRide(@RequestBody RideRequest request) {
        rideService.requestRide(request);
        return ApiResponse.getSuccessResponse("Ride Requested Successfully! Please wait till we find you a driver");
    }

    @PreAuthorize("hasRole('RIDER')")
    @PostMapping("/{id}/cancel")
    public  ResponseEntity<?> cancelRide(@PathVariable("id") @ValidId(entity = Ride.class) Long id) {
        rideService.cancelRide(id);
        return ApiResponse.getSuccessResponse("Ride Cancelled Successfully!");

    }

    @PreAuthorize("hasRole('DRIVER')")
    @PostMapping("/{id}/end")
    public void endRide(@PathVariable @ValidId(entity = Ride.class) Long id, @RequestParam Long driverId) {
        rideService.endRide(id, driverId);
    }
}
