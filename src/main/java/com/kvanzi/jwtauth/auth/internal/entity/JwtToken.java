package com.kvanzi.jwtauth.auth.internal.entity;

import com.kvanzi.jwtauth.shared.persistence.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(
        name = "jwt_tokens"
)
public class JwtToken extends BaseEntity {

    @Builder.Default
    @Column(name = "revoked", nullable = false)
    private boolean revoked = false;

    @Enumerated(EnumType.STRING)
    @Column(name = "token_type", nullable = false)
    private JwtTokenType tokenType;

    @Column(name = "expires_at", nullable = false)
    private Instant expiresAt;

    @Column(name = "issued_at", nullable = false)
    private Instant issuedAt;

    @Column(name = "user_id", nullable = false)
    private UUID userId;
}
