package com.ridematch.driver.dto;

import com.ridematch.driver.enums.DriverStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StatusUpdateRequest {
    private DriverStatus driverStatus;
}
