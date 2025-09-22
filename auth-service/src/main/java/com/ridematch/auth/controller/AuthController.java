package com.ridematch.auth.controller;

import com.ridematch.auth.dto.LoginRequest;
import com.ridematch.auth.dto.RegisterRequest;
import com.ridematch.auth.dto.TokenResponse;
import com.ridematch.auth.entity.UserEntity;
import com.ridematch.auth.service.JwtService;
import com.ridematch.auth.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest registerRequest) {
        UserEntity userEntity = userService.register(registerRequest);
        return ResponseEntity.ok(userEntity);
    }

    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@RequestBody LoginRequest loginRequest) {
        Authentication auth =
                authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(
                                loginRequest.getUsername(), loginRequest.getPassword()));

        UserEntity entity = userService.getUserEntity(loginRequest.getUsername());
        String token =
                jwtService.generateToken(
                        (User) auth.getPrincipal(), String.valueOf(entity.getId()));
        return ResponseEntity.ok(new TokenResponse(token));
    }
}
