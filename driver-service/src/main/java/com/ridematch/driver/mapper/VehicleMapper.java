package com.ridematch.driver.mapper;

import com.ridematch.driver.dto.VehicleInformationDTO;
import com.ridematch.driver.entity.Vehicle;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface VehicleMapper {

    @Mapping(target = "id", ignore = true)
    Vehicle mapEntityToEntity(VehicleInformationDTO vehicleInformationDTO);
}
