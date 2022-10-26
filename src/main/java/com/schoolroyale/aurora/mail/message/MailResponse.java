package com.schoolroyale.aurora.mail.message;

import com.fasterxml.jackson.annotation.JsonIgnore;

public record MailResponse(String message, String mail, @JsonIgnore boolean success) {

    public static MailResponse success(String message, String mail) {
        return new MailResponse(message, mail, true);
    }

    public static MailResponse error(String message) {
        return new MailResponse(message, null, false);
    }

}
