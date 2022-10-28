package com.schoolroyale.aurora.auth.security;

import com.schoolroyale.aurora.auth.user.ApiUser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class JwtToUserConverter implements Converter<Jwt, Mono<UsernamePasswordAuthenticationToken>> {

    @Override
    public Mono<UsernamePasswordAuthenticationToken> convert(Jwt jwt) {
        return Mono.fromSupplier(() -> {
            var user = ApiUser.from(jwt);

            return new UsernamePasswordAuthenticationToken(user, jwt, user.getAuthorities());
        });
    }
}
