package com.aryan.resumeai.auth.service;

import com.aryan.resumeai.auth.entity.RefreshToken;
import com.aryan.resumeai.auth.entity.User;
import com.aryan.resumeai.auth.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;

    public RefreshToken createRefreshToken(
            User user,
            String token
    ) {

        refreshTokenRepository
                .findByUser(user)
                .ifPresent(
                        existing ->
                                refreshTokenRepository
                                        .delete(existing)
                );

        RefreshToken refreshToken =
                RefreshToken.builder()
                        .token(token)
                        .user(user)
                        .expiryDate(
                                LocalDateTime.now()
                                        .plusDays(7)
                        )
                        .build();

        return refreshTokenRepository.save(
                refreshToken
        );
    }

    public RefreshToken verifyRefreshToken(
            String token
    ) {

        RefreshToken refreshToken =
                refreshTokenRepository
                        .findByToken(token)
                        .orElseThrow(
                                () ->
                                        new RuntimeException(
                                                "Refresh token not found"
                                        )
                        );

        if (
                refreshToken
                        .getExpiryDate()
                        .isBefore(
                                LocalDateTime.now()
                        )
        ) {

            refreshTokenRepository.delete(
                    refreshToken
            );

            throw new RuntimeException(
                    "Refresh token expired"
            );
        }

        return refreshToken;
    }
}