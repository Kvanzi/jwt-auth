package com.kvanzi.jwtauth.user.internal.dto;

import com.kvanzi.jwtauth.shared.validation.ValidPassword;
import com.kvanzi.jwtauth.shared.validation.ValidUsername;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.jspecify.annotations.NonNull;

@Getter
@AllArgsConstructor
public class CreateUserRequest {
    @NotNull(message = "Username field cannot be null")
    @ValidUsername
    private @NonNull String username;

    @NotNull(message = "Password field cannot be null")
    @ValidPassword
    private @NonNull String password;
}
