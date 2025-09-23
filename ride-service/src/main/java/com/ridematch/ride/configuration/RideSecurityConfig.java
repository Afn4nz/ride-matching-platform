package com.ridematch.ride.configuration;

import com.ridematch.security.BearerTokenFilter;
import com.ridematch.security.JwtValidator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableMethodSecurity
public class RideSecurityConfig {

    @Bean
    JwtValidator jwtValidator(
            @Value("${jwt.secret}") String secret, @Value("${jwt.clockSkewSeconds:60}") long skew) {

        return new JwtValidator(secret, skew);
    }

    @Bean
    BearerTokenFilter bearerTokenFilter(JwtValidator validator) {
        return new BearerTokenFilter(validator);
    }

    @Bean
    SecurityFilterChain rideSecurityFilterChain(HttpSecurity http, BearerTokenFilter filter)
            throws Exception {
        http.csrf(csrf -> csrf.disable())
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(
                        reg ->
                                reg.requestMatchers(
                                                "/actuator/health",
                                                "/v3/api-docs/**",
                                                "/swagger-ui/**")
                                        .permitAll()
                                        .requestMatchers("/rides/**")
                                        .hasAnyRole("ADMIN", "DRIVER", "RIDER")
                                        .anyRequest()
                                        .authenticated())
                .addFilterBefore(filter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
