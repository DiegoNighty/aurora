package com.schoolroyale.aurora.account;

import com.schoolroyale.aurora.auth.User;
import com.schoolroyale.aurora.auth.role.Roles;
import com.schoolroyale.aurora.router.RouterHelper;
import com.schoolroyale.aurora.schemas.account.Account;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/account")
@PreAuthorize(Roles.USER)
public class AccountRouter {

    private final AccountRepository repository;

    public AccountRouter(AccountRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/search/name/{name}")
    public Mono<ResponseEntity<Account>> findByName(@PathVariable("name") String name) {
        return RouterHelper.okOrNotFound(repository.findAccountByCredentialUsername(name));
    }

    @GetMapping("/me")
    public Mono<ResponseEntity<Account>> findMe(@AuthenticationPrincipal User user) {
        return RouterHelper.okOrNotFound(repository.findAccountByCredentialUsername(user.username()));
    }

    @GetMapping("/search/mail/{mail}")
    public Mono<ResponseEntity<Account>> findByMail(@PathVariable("mail") String mail) {
        return RouterHelper.okOrNotFound(repository.findAccountByCredentialMailsAddress(mail));
    }

    @GetMapping("/search/address/{address}")
    public Flux<Account> findByAddress(@PathVariable("address") String address) {
        return repository.findAllBySessionAddressesNumber(address);
    }

    @PutMapping("/update")
    @PreAuthorize(Roles.ADMIN)
    public Mono<ResponseEntity<Account>> updateAccount(@RequestBody Account account) {
       return RouterHelper.okOrBadRequest(repository.save(account));
    }

}
