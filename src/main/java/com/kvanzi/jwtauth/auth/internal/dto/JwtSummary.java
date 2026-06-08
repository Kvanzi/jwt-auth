package com.kvanzi.jwtauth.auth.internal.dto;

import com.kvanzi.jwtauth.auth.internal.entity.JwtTokenType;
import java.time.Instant;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

@Getter
@Builder
@AllArgsConstructor
public class JwtSummary {
    private @Nullable UUID tokenId;
    private @NonNull UUID userId;
    private @NonNull JwtTokenType tokenType;
    private @NonNull Instant issuedAt;
    private boolean expired;
}
