package com.schoolroyale.aurora.auth.user;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Mono;

public interface ApiUserRepository extends ReactiveMongoRepository<ApiUser, String> {
    Mono<ApiUser> findByUsername(String username);
    Mono<Boolean> existsByMinecraftAccountIdOrUsername(String minecraftAccountId, String username);
}
