package com.ridematch.auth.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginRequest {
    private String password;
    private String username;
}
