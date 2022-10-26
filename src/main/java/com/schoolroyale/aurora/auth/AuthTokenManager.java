package com.schoolroyale.aurora.auth;

import com.schoolroyale.aurora.auth.token.TokenService;
import io.jsonwebtoken.ExpiredJwtException;
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
    private final UserRepository userRepository;

    public AuthTokenManager(TokenService tokenService, UserRepository userRepository) {
        this.tokenService = tokenService;
        this.userRepository = userRepository;
    }

    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {
        String token = authentication.getCredentials().toString();

        return Mono.just(token)
                .map(tokenService::validateToken)
                .onErrorReturn(ExpiredJwtException.class, false)
                .filter(valid -> valid)
                .switchIfEmpty(Mono.empty())
                .map(__ -> tokenService.usernameFromToken(token))
                .flatMap(userRepository::findByUsername)
                .map(user -> {
                    var authorities = rolesFromToken(token).stream()
                            .map(SimpleGrantedAuthority::new)
                            .toList();

                    return new UsernamePasswordAuthenticationToken(
                            user,
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
