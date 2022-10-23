package com.schoolroyale.aurora.auth;

import com.schoolroyale.aurora.auth.token.TokenService;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
public class AuthTokenManager implements ReactiveAuthenticationManager {

    private final TokenService tokenService;

    public AuthTokenManager(TokenService tokenService) {
        this.tokenService = tokenService;
    }

    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {
        var token = authentication.getCredentials().toString();
        var username = tokenService.usernameFromToken(token);

        return Mono.just(tokenService.validateToken(token))
                .filter(valid -> valid)
                .switchIfEmpty(Mono.empty())
                .map(valid -> {
                    var authorities = rolesFromToken(token).stream()
                            .map(SimpleGrantedAuthority::new)
                            .toList();

                    return new UsernamePasswordAuthenticationToken(
                            username,
                            token,
                            authorities
                    );
                });
    }

    @SuppressWarnings("unchecked")
    private List<String> rolesFromToken(String token) {
        return (List<String>) tokenService.claimsFromToken(token).get("role", List.class);
    }
}
