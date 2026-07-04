package com.aryan.resumeai.auth.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class JwtRefreshResponse {

    private String accessToken;

    private String refreshToken;
}