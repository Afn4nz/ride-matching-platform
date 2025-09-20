package com.ridematch.driver.dto;

import com.ridematch.driver.enums.RegistrationType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VehicleInformationDTO { // TODO: Add validation
    private String plateLetter1;
    private String plateLetter2;
    private String plateLetter3;
    private String plateNumber;
    private RegistrationType registrationType;
    private String maker;
    private String model;
}
