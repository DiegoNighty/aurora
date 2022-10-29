package com.schoolroyale.aurora.mail.sender;


import com.schoolroyale.aurora.mail.template.MailTemplate;
import com.schoolroyale.aurora.mail.template.Placeholder;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MailSenderService {

    private final JavaMailSender mailSender;
    private @Value("${spring.mail.sender}") String sender;

    public String sendMail(String recipient, MailTemplate template, Placeholder... placeholders) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");

            helper.setFrom(sender);
            helper.setTo(recipient);
            helper.setText(template.body(placeholders), true);
            helper.setSubject(template.subject());

            // Sending the mail
            mailSender.send(helper.getMimeMessage());
            return "Mail Sent Successfully...";
        }

        catch (Exception e) {
            return "Error while Sending Mail";
        }
    }

}
