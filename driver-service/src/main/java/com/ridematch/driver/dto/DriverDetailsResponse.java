package com.ridematch.driver.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class DriverDetailsResponse extends DriverResponse {
    private FileDTO license;
    private FileDTO vehicleRegistration;
    private List<FileDTO> images;
}
