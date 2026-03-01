package com.kvanzi.jwtauth.security.strategy;

import com.kvanzi.jwtauth.dto.CreateTokensRequest;
import com.kvanzi.jwtauth.dto.CreateTokensResult;
import com.kvanzi.jwtauth.dto.GrantType;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

public interface AuthStrategy {
    @NonNull CreateTokensResult execute(@NonNull CreateTokensRequest request, @Nullable String refreshToken);

    @NonNull GrantType getSupportedGrantType();
}
