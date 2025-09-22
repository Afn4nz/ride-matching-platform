package com.ridematch.auth.service;

import com.ridematch.auth.dto.RegisterRequest;
import com.ridematch.auth.entity.UserEntity;
import com.ridematch.auth.mapper.UserMapper;
import com.ridematch.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final PasswordEncoder encoder;
    private final UserMapper userMapper;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserEntity u =
                userRepository
                        .findByUsername(username)
                        .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        return new User(
                u.getUsername(),
                u.getPassword(),
                java.util.List.of(new SimpleGrantedAuthority(u.getRole().name())));
    }

    public UserEntity register(RegisterRequest registerRequest) {
        if (userRepository.existsByUsername(registerRequest.getUsername()))
            throw new RuntimeException("Username taken");
        return userRepository.save(
                userMapper.mapToEntity(
                        registerRequest, encoder.encode(registerRequest.getPassword())));
    }

    public UserEntity getUserEntity(String username) {
        return userRepository
                .findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }
}
