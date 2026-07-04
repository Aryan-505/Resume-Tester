package com.aryan.resumeai.security.jwt;

public class JwtUtil {

    private JwtUtil() {
    }

    public static String extractToken(
            String header
    ) {

        if (header == null) {
            return null;
        }

        if (!header.startsWith(
                JwtConstants.TOKEN_PREFIX)) {

            return null;
        }

        return header.substring(7);
    }
}