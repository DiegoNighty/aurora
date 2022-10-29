package com.schoolroyale.aurora.mail.template.provider;

import com.schoolroyale.aurora.html.HtmlService;
import com.schoolroyale.aurora.mail.template.MailTemplate;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MailProvider {

    private final HtmlService htmlService;

    private @Value("${mails.verification-code.subject}") String verificationCodeSubject;
    private @Value("${mails.verification-code.path}") String verificationCodePath;

    @Bean
    @Qualifier("verification-code")
    public MailTemplate verificationCodeTemplate() {
        return htmlService.createTemplate(verificationCodeSubject, verificationCodePath);
    }

}
