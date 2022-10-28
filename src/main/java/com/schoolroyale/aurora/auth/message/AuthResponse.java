package com.schoolroyale.aurora.auth.message;

public record AuthResponse(String message, TokenResponse token) {

      public static AuthResponse from(String message) {
          return new AuthResponse(message, null);
      }

      public static AuthResponse from(String message, TokenResponse token) {
          return new AuthResponse(message, token);
      }

      public static AuthResponse from(TokenResponse token) {
          return new AuthResponse("Successfully logged in", token);
      }

}
