package com.schoolroyale.aurora.mail.template;

public record MailTemplate(String subject, String body) {

    public String body(Placeholder... placeholders) {
        var internalBody = body;

        for (Placeholder placeholder : placeholders) {
            internalBody = internalBody.replace(placeholder.getPlaceholder(), placeholder.getValue());
        }

        return internalBody;
    }

}
