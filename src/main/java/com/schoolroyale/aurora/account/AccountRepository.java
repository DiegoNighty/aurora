package com.schoolroyale.aurora.account;

import com.schoolroyale.aurora.schemas.account.Account;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface AccountRepository extends ReactiveMongoRepository<Account, String> {

    Mono<Account> findAccountByCredentialUsername(String username);

    Mono<Account> findAccountByCredentialMailsAddress(String mail);

    Flux<Account> findAccountsBySessionAddressesNumber(String number);

}
