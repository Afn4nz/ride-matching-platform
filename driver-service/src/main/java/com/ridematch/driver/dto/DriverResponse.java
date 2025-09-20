package com.ridematch.driver.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DriverResponse {
    private Long id;
    private String fullName;
    private String phoneNumber;
    private String email;
    private VehicleInformationDTO vehicleInformationDTO;
}
