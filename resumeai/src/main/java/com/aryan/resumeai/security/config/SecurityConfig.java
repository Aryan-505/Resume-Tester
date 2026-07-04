package com.aryan.resumeai.security.config;

import com.aryan.resumeai.security.filter.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.*;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import com.aryan.resumeai.security.service.CustomUserDetailsService;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

        private final JwtAuthenticationFilter jwtAuthenticationFilter;
        private final CustomUserDetailsService customUserDetailsService;
        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
                http.authenticationProvider(
        authenticationProvider()
);

                http
    .cors(cors -> {})
    .csrf(csrf -> csrf.disable())

                                .sessionManagement(
                                                session -> session
                                                                .sessionCreationPolicy(
                                                                                SessionCreationPolicy.STATELESS))

                                .authorizeHttpRequests(auth ->

                                auth

                                                .requestMatchers(
                                                                "/api/auth/**")
                                                .permitAll()

                                                .requestMatchers(
                                                                "/api/public/**")
                                                .permitAll()
                                                .requestMatchers("/error").permitAll()

                                                .requestMatchers(
                                                                "/api/admin/**")
                                                .hasRole("ADMIN")

                                                .anyRequest()
                                                .authenticated())

                                .addFilterBefore(
                                                jwtAuthenticationFilter,
                                                UsernamePasswordAuthenticationFilter.class);

                return http.build();
        }

        @Bean
        public PasswordEncoder passwordEncoder() {

                return new BCryptPasswordEncoder();
        }

        @Bean
        public AuthenticationManager authenticationManager(
                        AuthenticationConfiguration config) throws Exception {

                return config.getAuthenticationManager();
        }
        @Bean
public DaoAuthenticationProvider authenticationProvider() {

    DaoAuthenticationProvider authProvider =
            new DaoAuthenticationProvider();

    authProvider.setUserDetailsService(
            customUserDetailsService
    );

    authProvider.setPasswordEncoder(
            passwordEncoder()
    );

    return authProvider;
}
}