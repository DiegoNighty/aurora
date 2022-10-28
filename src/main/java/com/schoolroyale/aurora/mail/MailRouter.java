package com.schoolroyale.aurora.mail;

import com.schoolroyale.aurora.account.AccountRepository;
import com.schoolroyale.aurora.auth.role.Roles;
import com.schoolroyale.aurora.auth.user.ApiUser;
import com.schoolroyale.aurora.mail.message.MailResponse;
import com.schoolroyale.aurora.mail.message.RequestVerificationResponse;
import com.schoolroyale.aurora.router.RouterHelper;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/mail")
@Roles.IsUser
public class MailRouter {

    private final AccountRepository repository;
    private final VerificationCodeService service;

    public MailRouter(AccountRepository repository, VerificationCodeService service) {
        this.repository = repository;
        this.service = service;
    }

    @GetMapping("/add")
    public Mono<ResponseEntity<RequestVerificationResponse>> requestVerification(
            @RequestParam("mail") String mail,
            @AuthenticationPrincipal ApiUser apiUser
    ) {
        return repository.findAccountByCredentialMailsAddress(mail)
                .map(alreadyMail ->
                        ResponseEntity.status(RouterHelper.ALREADY_EXISTS)
                                .body(RequestVerificationResponse.from("Email already linked with another account"))
                )
                .switchIfEmpty(
                        Mono.fromRunnable(() -> service.requestVerification(mail, apiUser))
                                .thenReturn(ResponseEntity.ok(RequestVerificationResponse.from("Verification code sent")))
                );
    }

    @GetMapping("/verify")
    public Mono<ResponseEntity<MailResponse>> verify(
            @RequestParam("code") String code,
            @AuthenticationPrincipal ApiUser apiUser
    ) {
        MailResponse response = service.verifyCode(apiUser, code);

        if (!response.success()) {
            return Mono.just(from(response));
        }

        return repository.findAccountByCredentialUsername(apiUser.username())
                .flatMap(account -> {
                    account.credential()
                            .addMail(response.mail());

                    return repository.save(account);
                })
                .map(__ -> from(response));
    }

    private ResponseEntity<MailResponse> from(MailResponse response) {
        return response.success() ? ResponseEntity.ok(response) : ResponseEntity.status(RouterHelper.BAD_REQUEST).body(response);
    }

}
