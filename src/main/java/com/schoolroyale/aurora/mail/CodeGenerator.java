package com.schoolroyale.aurora.mail;

import java.util.Random;

public class CodeGenerator {

    static final Random RANDOM = new Random();
    static final String ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    static final int CODE_LENGTH = 6;

    public static String generate() {
        StringBuilder codeBuilder = new StringBuilder();

        for (int i = 0; i < CODE_LENGTH; i++) {
            codeBuilder.append(ALPHABET.charAt(RANDOM.nextInt(ALPHABET.length())));
        }

        return codeBuilder.toString();
    }

}
