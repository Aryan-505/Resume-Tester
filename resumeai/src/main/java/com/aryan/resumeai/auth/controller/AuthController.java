package com.aryan.resumeai.auth.controller;

import com.aryan.resumeai.auth.dto.*;
import com.aryan.resumeai.auth.service.AuthService;
import com.aryan.resumeai.common.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<Void>> register(
            @Valid @RequestBody RegisterRequest request
    ) {

        authService.register(request);

        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .success(true)
                        .message("User registered successfully")
                        .build()
        );
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(
            @Valid @RequestBody LoginRequest request
    ) {

        AuthResponse response =
                authService.login(request);

        return ResponseEntity.ok(
                ApiResponse.<AuthResponse>builder()
                        .success(true)
                        .message("Login successful")
                        .data(response)
                        .build()
        );
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<ApiResponse<JwtRefreshResponse>>
    refreshToken(
            @Valid
            @RequestBody
            RefreshTokenRequest request
    ) {

        JwtRefreshResponse response =
                authService.refreshToken(request);

        return ResponseEntity.ok(
                ApiResponse
                        .<JwtRefreshResponse>builder()
                        .success(true)
                        .message(
                                "Token refreshed successfully"
                        )
                        .data(response)
                        .build()
        );
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>>
    logout(
            Authentication authentication
    ) {

        authService.logout(
                authentication.getName()
        );

        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .success(true)
                        .message(
                                "Logout successful"
                        )
                        .build()
        );
    }

    @PostMapping("/google")
public ResponseEntity<ApiResponse<AuthResponse>>
googleLogin(
        @Valid
        @RequestBody
        GoogleLoginRequest request
) {

    AuthResponse response =
            authService.googleLogin(
                    request
            );

    return ResponseEntity.ok(
            ApiResponse
                    .<AuthResponse>builder()
                    .success(true)
                    .message(
                            "Google login successful"
                    )
                    .data(response)
                    .build()
    );
}
}