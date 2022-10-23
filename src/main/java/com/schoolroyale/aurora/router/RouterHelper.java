package com.schoolroyale.aurora.router;

import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class RouterHelper {

    public static <E> Mono<ResponseEntity<E>> okOrNotFound(Mono<E> mono) {
        return mono
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    public static <E> Flux<ResponseEntity<E>> okOrNotFound(Flux<E> flux) {
        return flux
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    public static <E> Mono<ResponseEntity<E>> okOrBadRequest(Mono<E> mono) {
        return mono
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.badRequest().build());
    }

}
