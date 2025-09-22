package com.ridematch.driver.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Future;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import jakarta.validation.constraints.Pattern;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest {
    private String fullName;

    @Pattern(regexp = "^0\\d{9}$", message = "INVALID_PHONE_FORMAT") // TODO: handle duplication
    private String phoneNumber;

    @Email private String email; // TODO: handle duplication

    @Future private LocalDate licenseExpiryDate;
    private VehicleInformationDTO vehicleInformationDTO;

    private Location location;
}
