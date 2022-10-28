package com.schoolroyale.aurora.auth.message;

public record TokenRequest(String username, String accessToken, String refreshToken) {}
