package com.aryan.resumeai.security.service;

import com.aryan.resumeai.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService
        implements UserDetailsService {

    private final UserRepository userRepository;

@Override
public UserDetails loadUserByUsername(
        String email
) {

    return userRepository
            .findByEmail(email)
            .orElseThrow(
                    () -> new UsernameNotFoundException(
                            "User not found: " + email
                    )
            );
}
}