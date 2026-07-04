package com.aryan.resumeai.auth.service;

import com.aryan.resumeai.auth.dto.*;
import com.aryan.resumeai.auth.entity.*;
import com.aryan.resumeai.auth.google.GoogleTokenVerifierService;
import com.aryan.resumeai.auth.repository.*;
import com.aryan.resumeai.common.exception.*;
import com.aryan.resumeai.security.jwt.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final GoogleTokenVerifierService googleTokenVerifierService;
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final RefreshTokenService refreshTokenService;

    @Override
    public void register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

User user = User.builder()
        .name(request.getName())
        .email(request.getEmail())
        .password(passwordEncoder.encode(request.getPassword()))
        .role(Role.ROLE_USER)
        .provider(AuthProvider.LOCAL)
        .enabled(true)
        .emailVerified(true)
        .build();

        userRepository.save(user);
    }

   @Override
public AuthResponse login(LoginRequest request) {

    User user = userRepository
            .findByEmail(request.getEmail())
            .orElseThrow(() ->
                    new RuntimeException("User not found"));

    if (user.getProvider() == AuthProvider.GOOGLE) {
        throw new RuntimeException(
                "This account uses Google Login"
        );

    }

    if (!user.getEmailVerified()) {
    throw new BadRequestException(
            "Please verify your email first"
    );
}
    try {

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

    } catch (BadCredentialsException ex) {

        throw new RuntimeException(
                "Invalid email or password"
        );
    }

    return buildAuthResponse(user);
}

    @Override
    public JwtRefreshResponse refreshToken(RefreshTokenRequest request) {
        RefreshToken refreshToken = refreshTokenService.verifyRefreshToken(request.getRefreshToken());
        User user = refreshToken.getUser();

        String newAccessToken = jwtService.generateAccessToken(user);
        String newRefreshToken = jwtService.generateRefreshToken(user);

        refreshTokenRepository.delete(refreshToken);
        refreshTokenService.createRefreshToken(user, newRefreshToken);

        return JwtRefreshResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .build();
    }

    @Override
    public void logout(String email) {
        User user = userRepository.findByEmail(email).orElseThrow();
        refreshTokenRepository.deleteByUser(user);
    }

   @Override
public AuthResponse googleLogin(
        GoogleLoginRequest request
) {

    GoogleIdToken.Payload payload =
            googleTokenVerifierService.verifyToken(
                    request.getIdToken()
            );

    String email = payload.getEmail();
    String name = (String) payload.get("name");

    User user = userRepository
            .findByEmail(email)
            .orElse(null);

    if (user != null) {

        if (user.getProvider() == AuthProvider.LOCAL) {

            throw new RuntimeException(
                    "This email is already registered using email/password login"
            );
        }

        return buildAuthResponse(user);
    }

    User newUser = User.builder()
            .name(name)
            .email(email)
            .provider(AuthProvider.GOOGLE)
            .role(Role.ROLE_USER)
            .enabled(true)
            .emailVerified(true)
            .build();

    user = userRepository.save(newUser);

    return buildAuthResponse(user);
}
    private AuthResponse buildAuthResponse(User user) {

    String accessToken =
            jwtService.generateAccessToken(user);

    String refreshToken =
            jwtService.generateRefreshToken(user);

    refreshTokenService.createRefreshToken(
            user,
            refreshToken
    );

    return AuthResponse.builder()
            .accessToken(accessToken)
            .refreshToken(refreshToken)
            .user(
                    UserResponse.builder()
                            .id(user.getId())
                            .name(user.getName())
                            .email(user.getEmail())
                            .role(user.getRole())
                            .provider(user.getProvider())
                            .build()
            )
            .build();
}
}