package com.aryan.resumeai.auth.entity;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import com.aryan.resumeai.common.entity.BaseEntity;
import java.util.Collection;
import java.util.List;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "users",
        indexes = {
                @Index(
                        name = "idx_email",
                        columnList = "email"
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User extends BaseEntity implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(
            unique = true,
            nullable = false
    )
    private String email;

    private String password;

    @Enumerated(EnumType.STRING)
    private Role role;

    @Enumerated(EnumType.STRING)
    private AuthProvider provider;

    @Builder.Default
private Boolean enabled = true;

@Builder.Default
private Boolean emailVerified = false;

    @Override
public Collection<? extends GrantedAuthority> getAuthorities() {
    return List.of(
            new SimpleGrantedAuthority(role.name())
    );
}

@Override
public String getUsername() {
    return email;
}

@Override
public boolean isAccountNonExpired() {
    return true;
}

@Override
public boolean isAccountNonLocked() {
    return true;
}

@Override
public boolean isCredentialsNonExpired() {
    return true;
}

@Override
public boolean isEnabled() {
    return enabled;
}

}