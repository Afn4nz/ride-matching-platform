package com.ridematch.auth.configuration;

import com.ridematch.auth.service.JwtService;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7);
            try {
                String username = jwtService.extractUsername(token);
                if (username != null
                        && SecurityContextHolder.getContext().getAuthentication() == null) {
                    UserDetails user = userDetailsService.loadUserByUsername(username);
                    if (jwtService.isTokenValid(token, user)) {
                        var auth =
                                new UsernamePasswordAuthenticationToken(
                                        user, null, user.getAuthorities());
                        SecurityContextHolder.getContext().setAuthentication(auth);
                    }
                }
            } catch (Exception exception) {
                throw new BadRequestException("UnAuthorized");
            }
        }
        chain.doFilter(request, response);
    }
}
