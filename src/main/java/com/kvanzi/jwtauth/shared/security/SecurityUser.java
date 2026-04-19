package com.kvanzi.jwtauth.shared.security;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.springframework.security.core.GrantedAuthority;

import java.time.Instant;
import java.util.Collection;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class SecurityUser implements IdentifiableUserDetails {

    private @NonNull UUID id;
    private @NonNull String username;
    private @NonNull String password;
    private @NonNull Collection<? extends GrantedAuthority> authorities;
    private @Nullable Instant lastPasswordChangedAt;
}
