package com.ridematch.driver.controller;

import com.ridematch.driver.Specification.DriverFilter;
import com.ridematch.driver.dto.ApiResponse;
import com.ridematch.driver.dto.RegisterRequest;
import com.ridematch.driver.dto.StatusUpdateRequest;
import com.ridematch.driver.service.DriverService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import org.springframework.data.domain.Pageable;

import java.util.List;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/drivers")
public class DriverController {
    private final DriverService driverService;

    @PostMapping
    public ResponseEntity<?> register(
            @RequestPart(value = "licenseDocument") MultipartFile licenseDocument,
            @RequestPart(value = "registrationDocument") MultipartFile registrationDocument,
            @RequestPart(value = "images") List<MultipartFile> images,
            @RequestPart(value = "driverInformation") @Valid RegisterRequest registerRequest) {
        driverService.register(licenseDocument, registrationDocument, images, registerRequest);
        return ApiResponse.getSuccessResponse(
                "You registration request submit successfully! we will verify your documents");
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> viewDriverDetails(@PathVariable("id") Long id) {
        return ApiResponse.getSuccessResponse(driverService.viewDriverDetails(id));
    }

    @GetMapping
    public ResponseEntity<?> viewAllDrivers(
            @PageableDefault Pageable pageable, DriverFilter driverFilter) {
        return ApiResponse.getSuccessResponse(driverService.viewAllDrivers(driverFilter, pageable));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<?> updateDriverStatus(
            @PathVariable("id") Long id, @RequestBody StatusUpdateRequest statusUpdateRequest) {
        driverService.updateDriverStatus(id, statusUpdateRequest);
        return ApiResponse.getSuccessResponse("Updated Successfully");
    }

    @PostMapping("/{id}/update-documents")
    public ResponseEntity<?> updateDocuments(
            @PathVariable("id") Long id,
            @RequestPart(value = "licenseDocument", required = false) MultipartFile licenseDocument,
            @RequestPart(value = "registrationDocument", required = false)
                    MultipartFile registrationDocument,
            @RequestPart(value = "images", required = false) List<MultipartFile> images) {
        driverService.updateDocuments(id, licenseDocument, registrationDocument, images);
        return ApiResponse.getSuccessResponse("Updated Successfully");
    }
}
