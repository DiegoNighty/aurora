package com.schoolroyale.aurora.mail.message;

public record RequestVerificationResponse(String message) {

    public static RequestVerificationResponse from(String message) {
        return new RequestVerificationResponse(message);
    }

}
