package com.ridematch.security;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.security.Principal;
import java.util.Set;

@Getter
@RequiredArgsConstructor
public class UserPrincipal implements Principal {
    private final String uid;
    private final String username;
    private final Set<String> roles;

    @Override
    public String getName() {
        return username;
    }
}
