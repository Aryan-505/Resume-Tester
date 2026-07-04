package com.aryan.resumeai.auth.dto;

import com.aryan.resumeai.auth.entity.AuthProvider;
import com.aryan.resumeai.auth.entity.Role;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class UserResponse {

    private Long id;

    private String name;

    private String email;

    private Role role;

    private AuthProvider provider;

    private Boolean emailVerified;
}