package com.schoolroyale.aurora.auth.token;

import com.schoolroyale.aurora.auth.message.TokenResponse;
import com.schoolroyale.aurora.auth.user.ApiUser;
import com.schoolroyale.aurora.time.Time;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.time.Duration;

@Service
@RequiredArgsConstructor
public class TokenService {

    private @Value("${aurora.jwt.issuer}") String issuer;

    private final JwtEncoder accessTokenEncoder;
    private final @Qualifier("refreshTokenEncoder") JwtEncoder refreshTokenEncoder;

    private @Value("${aurora.jwt.access-token-expiration}") long accessTokenExpiration;
    private @Value("${aurora.jwt.refresh-token-expiration}") long refreshTokenExpiration;
    private @Value("${aurora.jwt.refresh-when-expiration-left}") long refreshWhenExpirationLeft;

    public TokenResponse createToken(Authentication auth) {
        if (!(auth.getPrincipal() instanceof ApiUser user)) {
            throw new BadCredentialsException(
                    MessageFormat.format("principal {0} is not of ApiUser type", auth.getPrincipal().getClass())
            );
        }

        if (!(auth.getCredentials() instanceof Jwt jwt)) {
            return TokenResponse.from(
                    user.minecraftAccountId(),
                    createAccessToken(auth),
                    createRefreshToken(auth)
            );
        }

        var daysUntilExpiration = Time.between(jwt.getExpiresAt()).toDays();

        if (daysUntilExpiration < refreshWhenExpirationLeft) {
            return TokenResponse.from(
                    user.minecraftAccountId(),
                    createAccessToken(auth),
                    createRefreshToken(auth)
            );
        }

        return TokenResponse.from(
                user.minecraftAccountId(),
                createAccessToken(auth),
                jwt.getTokenValue()
        );
    }

    private String createRefreshToken(Authentication authentication) {
        return refreshTokenEncoder.encode(
                JwtEncoderParameters.from(createClaims(authentication, refreshTokenExpiration))
        ).getTokenValue();
    }

    private String createAccessToken(Authentication auth) {
        return accessTokenEncoder.encode(
                JwtEncoderParameters.from(createClaims(auth, accessTokenExpiration))
        ).getTokenValue();
    }

    private JwtClaimsSet createClaims(Authentication authentication, long expiration) {
        var user = (ApiUser) authentication.getPrincipal();

        return JwtClaimsSet.builder()
                .claim("role", user.getRole())
                .claim("minecraftAccountId", user.getMinecraftAccountId())
                .claim("password", user.getPassword())
                .issuer(issuer)
                .issuedAt(Time.now())
                .expiresAt(Time.when(Duration.ofSeconds(expiration)))
                .subject(user.username())
                .build();
    }

}
