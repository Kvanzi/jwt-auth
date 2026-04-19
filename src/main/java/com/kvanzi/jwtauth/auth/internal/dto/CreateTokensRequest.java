package com.kvanzi.jwtauth.auth.internal.dto;

import com.kvanzi.jwtauth.shared.validation.ValidPassword;
import com.kvanzi.jwtauth.shared.validation.ValidUsername;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

@Getter
@AllArgsConstructor
public class CreateTokensRequest {

    @NotNull(message = "Grant type field cannot be null")
    private @NonNull GrantType grantType;

    @ValidUsername
    private @Nullable String username;

    @ValidPassword
    private @Nullable String password;
}
