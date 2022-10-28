package com.schoolroyale.aurora.auth.message;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

public record AuthRequest(String username, String password, String minecraftAccountId) {

    public Authentication unauthenticated() {
        return UsernamePasswordAuthenticationToken.unauthenticated(username, password);
    }

}
