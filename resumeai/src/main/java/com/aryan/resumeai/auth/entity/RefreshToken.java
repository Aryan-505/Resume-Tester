package com.aryan.resumeai.auth.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "refresh_tokens")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(
        nullable = false,
        unique = true,
        length = 1000
)
private String token;

    private LocalDateTime expiryDate;

    @OneToOne
    @JoinColumn(
            name = "user_id",
            referencedColumnName = "id"
    )
    private User user;
}