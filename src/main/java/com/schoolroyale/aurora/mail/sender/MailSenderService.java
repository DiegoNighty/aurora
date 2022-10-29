package com.schoolroyale.aurora.mail.sender;


import com.schoolroyale.aurora.mail.template.MailTemplate;
import com.schoolroyale.aurora.mail.template.Placeholder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class MailSenderService {

    private final JavaMailSender mailSender;
    private @Value("${spring.mail.sender}") String sender;

    public void sendMail(String recipient, MailTemplate template, Placeholder... placeholders) {
        try {
            var mimeMessage = mailSender.createMimeMessage();
            var helper = new MimeMessageHelper(mimeMessage, "utf-8");

            helper.setFrom(sender);
            helper.setTo(recipient);
            helper.setText(template.body(placeholders), true);
            helper.setSubject(template.subject());

            mailSender.send(helper.getMimeMessage());
        } catch (Exception e) {
            log.error("Error while sending mail to {}", recipient, e);
        }
    }

}
