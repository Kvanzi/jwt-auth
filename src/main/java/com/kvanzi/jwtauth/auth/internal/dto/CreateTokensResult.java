package com.kvanzi.jwtauth.auth.internal.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.jspecify.annotations.NonNull;

@Getter
@AllArgsConstructor
public class CreateTokensResult {

    private @NonNull String accessToken;
    private @NonNull String refreshToken;
}
