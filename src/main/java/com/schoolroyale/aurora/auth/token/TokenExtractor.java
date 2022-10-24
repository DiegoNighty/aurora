package com.schoolroyale.aurora.auth.token;

public class TokenExtractor {

    public static final String TOKEN_PREFIX = "Bearer ";

    public static String extract(String header) {
        return header.substring(TOKEN_PREFIX.length());
    }

    public static boolean isToken(String header) {
        return header.startsWith(TOKEN_PREFIX);
    }

}
