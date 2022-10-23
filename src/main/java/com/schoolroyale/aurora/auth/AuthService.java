package com.schoolroyale.aurora.auth;

import com.schoolroyale.aurora.auth.message.AuthRequest;
import com.schoolroyale.aurora.auth.message.AuthResponse;
import com.schoolroyale.aurora.auth.token.TokenService;
import com.schoolroyale.aurora.router.RouterHelper;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class AuthService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final TokenService tokenService;

    public AuthService(PasswordEncoder passwordEncoder, UserRepository userRepository, TokenService tokenService) {
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
        this.tokenService = tokenService;
    }

    public Mono<ResponseEntity<AuthResponse>> validateLoginRequest(AuthRequest request) {
        return userRepository.findByUsername(request.username())
                .filter(user -> passwordEncoder.matches(request.password(), user.password()))
                .map(user -> ResponseEntity.ok(AuthResponse.success(tokenService.createToken(user))))
                .switchIfEmpty(
                        Mono.just(
                                ResponseEntity.status(RouterHelper.UNAUTHORIZED)
                                        .body(AuthResponse.error("Invalid username or password"))
                        )
                );
    }

    public Mono<ResponseEntity<AuthResponse>> validateRegisterRequest(AuthRequest request) {
        return userRepository.findByUsername(request.username())
                .map(user -> ResponseEntity.status(RouterHelper.ALREADY_EXISTS).body(AuthResponse.error("User already exists")))
                .switchIfEmpty(
                        userRepository.save(User.from(request, passwordEncoder))
                                .map(user -> ResponseEntity.ok(AuthResponse.success(tokenService.createToken(user))))
                );
    }



}
