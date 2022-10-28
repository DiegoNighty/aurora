package com.schoolroyale.aurora.auth.security;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.schoolroyale.aurora.auth.user.ApiUserManager;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.UserDetailsRepositoryReactiveAuthenticationManager;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusReactiveJwtDecoder;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtReactiveAuthenticationManager;
import org.springframework.security.oauth2.server.resource.web.access.server.BearerTokenServerAccessDeniedHandler;
import org.springframework.security.oauth2.server.resource.web.server.BearerTokenServerAuthenticationEntryPoint;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@EnableReactiveMethodSecurity
public class WebSecurity {

    private final static String[] AUTH_ENDPOINTS = {
            "/api/auth/login", "/api/auth/register", "/api/auth/token"
    };

    private final JwtToUserConverter reactiveConverter;
    private final KeyUtils keyUtils;
    private final ApiUserManager apiUserManager;

    public WebSecurity(JwtToUserConverter jwtToUserConverter, KeyUtils keyUtils, ApiUserManager apiUserManager) {
        this.reactiveConverter = jwtToUserConverter;
        this.keyUtils = keyUtils;
        this.apiUserManager = apiUserManager;
    }

    @Bean
    public SecurityWebFilterChain provideSecurityFilter(ServerHttpSecurity http) {
        return http
                .oauth2ResourceServer(oauth2 ->
                        oauth2.jwt(jwtSpec ->
                                jwtSpec.jwtAuthenticationConverter(reactiveConverter)
                                        .jwtDecoder(accessTokenDecoder())
                        )
                )
                .exceptionHandling(
                        exceptions -> exceptions
                                .authenticationEntryPoint(new BearerTokenServerAuthenticationEntryPoint())
                                .accessDeniedHandler(new BearerTokenServerAccessDeniedHandler())
                )
                .csrf().disable()
                .httpBasic().disable()
                .authorizeExchange()
                .pathMatchers(HttpMethod.OPTIONS).permitAll()
                .pathMatchers(AUTH_ENDPOINTS).permitAll()
                .anyExchange()
                .authenticated()
                .and()
                .build();
    }

    @Bean
    @Primary
    public ReactiveJwtDecoder accessTokenDecoder() {
        return NimbusReactiveJwtDecoder.withPublicKey(keyUtils.getAccessTokenPublicKey()).build();
    }

    @Bean
    @Primary
    public JwtEncoder jwtAccessTokenEncoder() {
        var jwk = new RSAKey
                .Builder(keyUtils.getAccessTokenPublicKey())
                .privateKey(keyUtils.getAccessTokenPrivateKey())
                .build();
        var jwks = new ImmutableJWKSet<>(new JWKSet(jwk));

        return new NimbusJwtEncoder(jwks);
    }

    @Bean
    @Qualifier("refreshTokenDecoder")
    public ReactiveJwtDecoder jwtRefreshTokenDecoder() {
        return NimbusReactiveJwtDecoder.withPublicKey(keyUtils.getRefreshTokenPublicKey()).build();
    }

    @Bean
    @Qualifier("refreshTokenEncoder")
    public JwtEncoder jwtRefreshTokenEncoder() {
        var jwk = new RSAKey
                .Builder(keyUtils.getRefreshTokenPublicKey())
                .privateKey(keyUtils.getRefreshTokenPrivateKey())
                .build();

        var jwks = new ImmutableJWKSet<>(new JWKSet(jwk));
        return new NimbusJwtEncoder(jwks);
    }

    @Bean
    @Qualifier("refreshManager")
    public JwtReactiveAuthenticationManager jwtRefreshTokenAuthProvider() {
        var manager = new JwtReactiveAuthenticationManager(jwtRefreshTokenDecoder());
        manager.setJwtAuthenticationConverter(reactiveConverter);

        return manager;
    }

    @Bean
    @Primary
    public UserDetailsRepositoryReactiveAuthenticationManager daoAuthenticationManager(PasswordEncoder passwordEncoder) {
        var manager = new UserDetailsRepositoryReactiveAuthenticationManager(apiUserManager);
        manager.setPasswordEncoder(passwordEncoder);
        manager.setUserDetailsPasswordService(apiUserManager);

        return manager;
    }

}
