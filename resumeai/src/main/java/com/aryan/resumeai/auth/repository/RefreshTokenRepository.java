package com.aryan.resumeai.auth.repository;

import com.aryan.resumeai.auth.entity.RefreshToken;
import com.aryan.resumeai.auth.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository
        extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByToken(
            String token
    );

    Optional<RefreshToken> findByUser(
            User user
    );

    void deleteByUser(
            User user
    );
        void deleteByToken(
            String token
    );
}