package com.schoolroyale.aurora.mail;

import com.schoolroyale.aurora.auth.user.ApiUser;
import com.schoolroyale.aurora.cache.Cache;
import com.schoolroyale.aurora.cache.ExpirableCache;
import com.schoolroyale.aurora.mail.message.MailResponse;
import com.schoolroyale.aurora.mail.sender.MailSenderService;
import com.schoolroyale.aurora.mail.template.MailTemplate;
import com.schoolroyale.aurora.mail.template.Placeholder;
import com.schoolroyale.aurora.time.Expirable;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class VerificationCodeService {

    private @Value("${aurora.mail-code-expiration}") long expirationMinutes;

    private final Map<String, String> pendingMails = new HashMap<>();
    private Cache<String, String> codes;

    private final MailSenderService mailSenderService;
    private final @Qualifier("verification-code") MailTemplate verificationCodeTemplate;

    @PostConstruct
    public void init() {
        codes = ExpirableCache.of(new HashMap<>(), Duration.ofMinutes(expirationMinutes));
    }

    public Mono<String> requestVerification(String mail, ApiUser apiUser) {
        String code = CodeGenerator.generate();

        codes.put(code, mail);
        pendingMails.put(apiUser.username(), mail);

        return Mono.fromSupplier(() ->
                        mailSenderService.sendMail(
                                mail,
                                verificationCodeTemplate,
                                Placeholder.of("%username%", apiUser.username()),
                                Placeholder.of("%code%", code)
                        )
                );
    }

    public MailResponse verifyCode(ApiUser apiUser, String code) {
        String pendingMail = pendingMails.get(apiUser.username());

        if (pendingMail == null) {
            return MailResponse.error("No pending verification");
        }

        Optional<Expirable<String>> optionalMail = codes.getExpirable(code);

        if (optionalMail.isEmpty()) {
            return MailResponse.error("Invalid code");
        }

        Expirable<String> expirableMail = optionalMail.get();

        if (expirableMail.isExpired()) {
            clear(apiUser, code);
            return MailResponse.error("Code expired");
        }

        String mail = expirableMail.getValue();

        if (!mail.equals(pendingMail)) {
            return MailResponse.error("Invalid code");
        }

        clear(apiUser, code);

        return MailResponse.success("Code verified", mail);
    }

    private void clear(ApiUser apiUser, String code) {
        pendingMails.remove(apiUser.username());
        codes.asMap().remove(code);
    }

}
