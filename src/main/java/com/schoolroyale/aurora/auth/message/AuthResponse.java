package com.schoolroyale.aurora.auth.message;

public record AuthResponse(String token, String message) {

    public static final String SUCCESS_MESSAGE = "Login successful";

    public static AuthResponse success(String token) {
        return new AuthResponse(token, SUCCESS_MESSAGE);
    }

    public static AuthResponse error(String message) {
        return new AuthResponse(null, message);
    }

}
