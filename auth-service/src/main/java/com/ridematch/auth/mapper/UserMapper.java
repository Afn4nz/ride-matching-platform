package com.ridematch.auth.mapper;

import com.ridematch.auth.dto.RegisterRequest;
import com.ridematch.auth.entity.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "password", source = "encodedPassword")
    UserEntity mapToEntity(RegisterRequest registerRequest, String encodedPassword);
}
