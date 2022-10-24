package com.schoolroyale.aurora.bandwith;

import com.schoolroyale.aurora.auth.role.Roles;
import com.schoolroyale.aurora.auth.token.TokenService;
import com.schoolroyale.aurora.router.RouterHelper;
import org.slf4j.Logger;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
public class RateLimiterHandler implements WebFilter {

    private final RateLimiterService rateLimiterService;
    private final TokenService tokenService;

    private static final Logger logger = org.slf4j.LoggerFactory.getLogger(RateLimiterHandler.class);

    public RateLimiterHandler(RateLimiterService rateLimiterService, TokenService tokenService) {
        this.rateLimiterService = rateLimiterService;
        this.tokenService = tokenService;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        var header = exchange.getRequest()
                .getHeaders()
                .getFirst(HttpHeaders.AUTHORIZATION);

        if (header == null || !header.startsWith("Bearer ")) {
            return chain.filter(exchange);
        }

        var token = header.substring(7);

        var username = tokenService.usernameFromToken(token);
        var maxRole = Roles.maxRoleFromAuthorities(rolesFromToken(token));

        if (rateLimiterService.consume(username, maxRole)) {
            return chain.filter(exchange);
        }

        exchange.getResponse()
                .setStatusCode(RouterHelper.RATE_LIMIT);

        return exchange.getResponse()
                .setComplete();
    }

    @SuppressWarnings("unchecked")
    private List<String> rolesFromToken(String token) {
        return (List<String>) tokenService.claimsFromToken(token).get("role", List.class);
    }
}
