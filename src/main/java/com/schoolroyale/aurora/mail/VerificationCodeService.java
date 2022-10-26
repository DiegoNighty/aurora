package com.schoolroyale.aurora.mail;

import com.schoolroyale.aurora.auth.User;
import com.schoolroyale.aurora.cache.Cache;
import com.schoolroyale.aurora.cache.ExpirableCache;
import com.schoolroyale.aurora.mail.message.MailResponse;
import com.schoolroyale.aurora.time.Expirable;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class VerificationCodeService {

    private final static Logger LOGGER = LoggerFactory.getLogger(VerificationCodeService.class);
    private @Value("${aurora.mail.code.expirationMinutes}") long expirationMinutes;

    private final Map<String, String> pendingMails = new HashMap<>();
    private Cache<String, String> codes;

    @PostConstruct
    public void init() {
        codes = ExpirableCache.of(new HashMap<>(), Duration.ofMinutes(expirationMinutes));
    }

    public void requestVerification(String mail, User user) {
        String code = CodeGenerator.generate();

        codes.put(code, mail);
        pendingMails.put(user.username(), mail);
        LOGGER.info("Verification code {} requested for {}", code, mail);
    }

    public MailResponse verifyCode(User user, String code) {
        String pendingMail = pendingMails.get(user.username());

        if (pendingMail == null) {
            return MailResponse.error("No pending verification");
        }

        Optional<Expirable<String>> optionalMail = codes.getExpirable(code);

        if (optionalMail.isEmpty()) {
            return MailResponse.error("Invalid code");
        }

        Expirable<String> expirableMail = optionalMail.get();

        if (expirableMail.isExpired()) {
            clear(user, code);
            return MailResponse.error("Code expired");
        }

        String mail = expirableMail.getValue();

        if (!mail.equals(pendingMail)) {
            return MailResponse.error("Invalid code");
        }

        clear(user, code);

        return MailResponse.success("Code verified", mail);
    }

    private void clear(User user, String code) {
        pendingMails.remove(user.username());
        codes.asMap().remove(code);
    }

}
