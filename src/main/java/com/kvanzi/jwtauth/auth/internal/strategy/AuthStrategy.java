package com.kvanzi.jwtauth.auth.internal.strategy;

import com.kvanzi.jwtauth.auth.internal.dto.CreateTokensRequest;
import com.kvanzi.jwtauth.auth.internal.dto.CreateTokensResult;
import com.kvanzi.jwtauth.auth.internal.dto.GrantType;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

public interface AuthStrategy {
    @NonNull CreateTokensResult execute(@NonNull CreateTokensRequest request, @Nullable String refreshToken);

    @NonNull GrantType getSupportedGrantType();
}
