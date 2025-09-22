package com.ridematch.auth.dto;

import com.ridematch.auth.enums.Role;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterRequest extends LoginRequest {
    private Role role;
}
