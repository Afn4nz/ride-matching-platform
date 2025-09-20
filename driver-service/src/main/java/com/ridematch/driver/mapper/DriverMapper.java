package com.ridematch.driver.mapper;

import com.ridematch.driver.dto.DriverResponse;
import com.ridematch.driver.dto.RegisterRequest;
import com.ridematch.driver.entity.Driver;
import com.ridematch.driver.entity.Vehicle;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface DriverMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(
            target = "status",
            expression = "java(com.ridematch.driver.enums.DriverStatus.OFFLINE)")
    @Mapping(target = "vehicle", source = "vehicle")
    Driver toEntity(RegisterRequest registerRequest, Vehicle vehicle);

    DriverResponse toDriverResponse(Driver driver);

    List<DriverResponse> toDriverResponse(List<Driver> driverList);
}
