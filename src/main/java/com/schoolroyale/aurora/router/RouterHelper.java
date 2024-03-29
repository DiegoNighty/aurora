package com.schoolroyale.aurora.router;

import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Mono;

public class RouterHelper {

    public static <E> Mono<ResponseEntity<E>> okOrNotFound(Mono<E> mono) {
        return mono
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    public static <E> Mono<ResponseEntity<E>> okOrBadRequest(Mono<E> mono) {
        return mono
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.badRequest().build());
    }

    public static final HttpStatusCode UNAUTHORIZED = HttpStatusCode.valueOf(401);
    public static final HttpStatusCode ALREADY_EXISTS = HttpStatusCode.valueOf(409);
    public static final HttpStatusCode RATE_LIMIT = HttpStatusCode.valueOf(429);
    public static final HttpStatusCode BAD_REQUEST = HttpStatusCode.valueOf(400);

}
