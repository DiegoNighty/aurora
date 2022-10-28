package com.schoolroyale.aurora.auth.message;

public record AuthRequest(String username, String password, String minecraftAccountId) { }
