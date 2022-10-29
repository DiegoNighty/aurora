package com.schoolroyale.aurora.html;

import com.schoolroyale.aurora.mail.template.MailTemplate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class HtmlService {

    private final Map<String, String> cachedHtml = new HashMap<>();

    public MailTemplate createTemplate(String subject, String fileName) {
        return new MailTemplate(subject, getHtml(fileName));
    }

    private String getHtml(String name) {
        return cachedHtml.computeIfAbsent(name, this::createHtml);
    }

    private String createHtml(String fileName) {
        if (!fileName.endsWith(".html")) {
            fileName += ".html";
        }

        URL url = getClass().getClassLoader().getResource(fileName);

        if (url == null) {
            log.info("Could not find file {}", fileName);
            return null;
        }

        try {
            return new String(Files.readAllBytes(Path.of(url.toURI())));
        } catch (Exception e) {
            log.info("Could not read file {}, {}", fileName, e.getMessage());
        }

        return null;
    }

}
