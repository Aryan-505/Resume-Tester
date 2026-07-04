package com.aryan.resumeai.security.filter;

import com.aryan.resumeai.security.jwt.JwtService;
import com.aryan.resumeai.security.jwt.JwtUtil;
import com.aryan.resumeai.security.service.CustomUserDetailsService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final CustomUserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");
        String token = JwtUtil.extractToken(authHeader);

        try {

            if (token != null) {

                String email = jwtService.extractEmail(token);

                if (email != null &&
                        SecurityContextHolder.getContext().getAuthentication() == null) {

                    var userDetails =
                            userDetailsService.loadUserByUsername(email);

                    if (jwtService.isTokenValid(
                            token,
                            userDetails.getUsername())) {

                        UsernamePasswordAuthenticationToken authentication =
                                new UsernamePasswordAuthenticationToken(
                                        userDetails,
                                        null,
                                        userDetails.getAuthorities());

                        authentication.setDetails(
                                new WebAuthenticationDetailsSource()
                                        .buildDetails(request));

                        SecurityContextHolder.getContext()
                                .setAuthentication(authentication);
                    }
                }
            }

        } catch (Exception ex) {

            System.out.println("JWT FILTER ERROR");
            ex.printStackTrace();
        }

        // VERY IMPORTANT
        // Always continue the filter chain
        filterChain.doFilter(request, response);
    }
}