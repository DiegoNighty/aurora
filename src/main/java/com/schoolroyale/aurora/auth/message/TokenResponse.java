package com.schoolroyale.aurora.auth.message;

public record TokenResponse(String minecraftAccountId, String accessToken, String refreshToken) {

    public static TokenResponse from(String minecraftAccountId, String accessToken, String refreshToken) {
        return new TokenResponse(minecraftAccountId, accessToken, refreshToken);
    }

}
