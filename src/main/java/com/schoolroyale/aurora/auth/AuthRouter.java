package com.schoolroyale.aurora.auth;

import com.schoolroyale.aurora.auth.message.AuthRequest;
import com.schoolroyale.aurora.auth.message.AuthResponse;
import com.schoolroyale.aurora.auth.message.TokenRequest;
import com.schoolroyale.aurora.auth.message.TokenResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/auth")
public class AuthRouter {

    private final AuthRouterResolver resolver;

    public AuthRouter(AuthRouterResolver resolver) {
        this.resolver = resolver;
    }

    @PostMapping("/register")
    public Mono<ResponseEntity<AuthResponse>> register(@RequestBody AuthRequest request) {
        return resolver.register(request);
    }

    @PostMapping("/login")
    public Mono<ResponseEntity<AuthResponse>> login(@RequestBody AuthRequest request) {
        return resolver.login(request);
    }

    @PostMapping("/token")
    public Mono<ResponseEntity<TokenResponse>> token(@RequestBody TokenRequest request) {
        return resolver.token(request);
    }

}
