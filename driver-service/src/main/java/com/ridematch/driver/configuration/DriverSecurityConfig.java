package com.ridematch.driver.configuration;

import com.ridematch.security.BearerTokenFilter;
import com.ridematch.security.JwtValidator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableMethodSecurity
public class DriverSecurityConfig {

    @Bean
    JwtValidator jwtValidator(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.clockSkewSeconds:60}") long skewSeconds) {
        return new JwtValidator(secret, skewSeconds);
    }

    @Bean
    BearerTokenFilter bearerTokenFilter(JwtValidator validator) {
        return new BearerTokenFilter(validator);
    }

    @Bean
    SecurityFilterChain driverFilterChain(HttpSecurity http, BearerTokenFilter filter)
            throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(
                        reg ->
                                reg.requestMatchers(
                                                "/actuator/**", "/v3/api-docs/**", "/swagger-ui/**")
                                        .permitAll()
                                        .requestMatchers("/drivers/**")
                                        .hasAnyRole("ADMIN", "DRIVER")
                                        .anyRequest()
                                        .authenticated())
                .addFilterBefore(filter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}
