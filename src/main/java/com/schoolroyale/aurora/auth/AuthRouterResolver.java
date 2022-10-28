package com.schoolroyale.aurora.auth;

import com.schoolroyale.aurora.account.AccountRepository;
import com.schoolroyale.aurora.auth.message.AuthRequest;
import com.schoolroyale.aurora.auth.message.AuthResponse;
import com.schoolroyale.aurora.auth.message.TokenRequest;
import com.schoolroyale.aurora.auth.message.TokenResponse;
import com.schoolroyale.aurora.auth.role.RoleRepository;
import com.schoolroyale.aurora.auth.role.Roles;
import com.schoolroyale.aurora.auth.token.TokenService;
import com.schoolroyale.aurora.auth.user.ApiUser;
import com.schoolroyale.aurora.auth.user.ApiUserRepository;
import com.schoolroyale.aurora.router.RouterHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UserDetailsRepositoryReactiveAuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.server.resource.authentication.BearerTokenAuthenticationToken;
import org.springframework.security.oauth2.server.resource.authentication.JwtReactiveAuthenticationManager;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;


@Component
@RequiredArgsConstructor
public class AuthRouterResolver {

    private final PasswordEncoder passwordEncoder;
    private final ApiUserRepository userRepository;
    private final RoleRepository roleRepository;
    private final TokenService tokenService;
    private final UserDetailsRepositoryReactiveAuthenticationManager authService;
    private final AccountRepository accountRepository;

    private final @Qualifier("refreshManager") JwtReactiveAuthenticationManager refreshManager;

    public Mono<ResponseEntity<TokenResponse>> token(TokenRequest request) {
        return refreshManager.authenticate(new BearerTokenAuthenticationToken(request.refreshToken()))
                .map(tokenService::createToken)
                .map(ResponseEntity::ok);
    }

    public Mono<ResponseEntity<AuthResponse>> register(AuthRequest request) {
        return Mono.zip(
                userRepository.existsByMinecraftAccountIdOrUsername(request.minecraftAccountId(), request.username()),
                accountRepository.existsById(request.minecraftAccountId())
        ).flatMap(exists -> handleRegistration0(request, exists));
    }

    public Mono<ResponseEntity<AuthResponse>> login(AuthRequest request) {
        return authService.authenticate(request.unauthenticated())
                .map(tokenService::createToken)
                .map(tokenResponse -> ResponseEntity.ok(AuthResponse.from(tokenResponse)))
                .switchIfEmpty(Mono.just(
                        ResponseEntity.status(RouterHelper.UNAUTHORIZED)
                                .body(AuthResponse.from("Invalid username or password"))
                ));
    }

    private Mono<ResponseEntity<AuthResponse>> handleRegistration0(AuthRequest request, Tuple2<Boolean, Boolean> exists) {
        if (exists.getT1()) {
            return Mono.just(ResponseEntity.status(RouterHelper.ALREADY_EXISTS)
                    .body(AuthResponse.from("Username or Minecraft Account already registered")));
        }

        if (!exists.getT2()) {
            return Mono.just(ResponseEntity.status(RouterHelper.BAD_REQUEST)
                    .body(AuthResponse.from("Minecraft Account not found")));
        }

        return roleRepository.findByName(Roles.DEFAULT_NAME)
                .map(role -> ApiUser.from(request, passwordEncoder, role))
                .flatMap(userRepository::save)
                .map(ApiUser::authentication)
                .map(auth -> ResponseEntity.ok(
                        AuthResponse.from("User registered", tokenService.createToken(auth)))
                );
    }
}
