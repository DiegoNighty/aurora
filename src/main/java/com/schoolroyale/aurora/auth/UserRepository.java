package com.schoolroyale.aurora.auth;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Mono;

@Cacheable("api-users")
public interface UserRepository extends ReactiveMongoRepository<User, String> {

    Mono<User> findByUsername(String username);

}
