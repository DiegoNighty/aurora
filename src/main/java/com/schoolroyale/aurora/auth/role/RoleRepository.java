package com.schoolroyale.aurora.auth.role;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Mono;

public interface RoleRepository extends ReactiveMongoRepository<Role, String> {

    Mono<Role> findByName(String name);

}
