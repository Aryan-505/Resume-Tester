package com.aryan.resumeai.auth.service;

import com.aryan.resumeai.auth.dto.*;

public interface AuthService {

    void register(RegisterRequest request);

    AuthResponse login(LoginRequest request);
    AuthResponse googleLogin(
        GoogleLoginRequest request
);
    JwtRefreshResponse refreshToken(
            RefreshTokenRequest request
    );

    void logout(String email);
}