package com.schoolroyale.aurora.auth;

import com.schoolroyale.aurora.auth.message.AuthRequest;
import com.schoolroyale.aurora.auth.message.AuthResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/auth")
public class AuthRouter {

    private final AuthService authService;

    public AuthRouter(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public Mono<ResponseEntity<AuthResponse>> login(@RequestBody AuthRequest request) {
        return authService.validateLoginRequest(request);
    }

    @PostMapping("/register")
    public Mono<ResponseEntity<AuthResponse>> register(@RequestBody AuthRequest request) {
        return authService.validateRegisterRequest(request);
    }

}
