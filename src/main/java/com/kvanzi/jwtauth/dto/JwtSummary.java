package com.kvanzi.jwtauth.dto;

import com.kvanzi.jwtauth.entity.JwtTokenType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class JwtSummary {

    private @Nullable UUID tokenId;
    private @NonNull UUID userId;
    private @NonNull JwtTokenType tokenType;
    private @NonNull Instant issuedAt;
    private boolean tokenExpired;
}
