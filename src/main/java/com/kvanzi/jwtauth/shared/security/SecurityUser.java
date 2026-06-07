package com.kvanzi.jwtauth.shared.security;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.springframework.security.core.GrantedAuthority;

import java.time.Instant;
import java.util.Collection;
import java.util.Objects;
import java.util.UUID;

@Getter
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class SecurityUser implements IdentifiableUserDetails {

    @EqualsAndHashCode.Include
    private @NonNull UUID id;

    private @NonNull String username;
    private @NonNull String password;
    private @NonNull Collection<? extends GrantedAuthority> authorities;
    private @Nullable Instant lastPasswordChangedAt;
}
