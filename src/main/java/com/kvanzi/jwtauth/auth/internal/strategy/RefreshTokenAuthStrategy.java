package com.kvanzi.jwtauth.auth.internal.strategy;

import com.kvanzi.jwtauth.auth.internal.dto.CreateTokensRequest;
import com.kvanzi.jwtauth.auth.internal.dto.CreateTokensResult;
import com.kvanzi.jwtauth.auth.internal.dto.GrantType;
import com.kvanzi.jwtauth.auth.internal.entity.JwtToken;
import com.kvanzi.jwtauth.auth.api.exception.InvalidJwtTokenException;
import com.kvanzi.jwtauth.shared.security.SecurityUser;
import com.kvanzi.jwtauth.auth.internal.service.JwtService;
import com.kvanzi.jwtauth.user.api.security.UserDetailsService;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class RefreshTokenAuthStrategy implements AuthStrategy {

    private final UserDetailsService userDetailsService;
    private final JwtService jwtService;

    @Override
    @Transactional
    public @NonNull CreateTokensResult execute(@NonNull CreateTokensRequest request, @Nullable String refreshToken) {
        JwtToken tokenEntity = jwtService.validateRefreshTokenOrThrow(refreshToken);
        SecurityUser securityUser;

        if (tokenEntity.getId() == null) {
            throw new IllegalStateException("Refresh token does not have an id");
        }

        try {
            securityUser = userDetailsService.loadUserById(tokenEntity.getUserId());
        } catch (UsernameNotFoundException e) {
            jwtService.revokeToken(tokenEntity.getId());
            throw new InvalidJwtTokenException("Invalid refresh token. Re login please");
        }

        if (securityUser.getLastPasswordChangedAt() != null
                && securityUser.getLastPasswordChangedAt().isAfter(tokenEntity.getIssuedAt())) {
            throw new InvalidJwtTokenException("Invalid refresh token. Re login please");
        }

        jwtService.revokeToken(tokenEntity.getId());

        return new CreateTokensResult(
                jwtService.generateAccessToken(securityUser.getId()),
                jwtService.generateRefreshToken(securityUser.getId())
        );
    }

    @Override
    public @NonNull GrantType getSupportedGrantType() {
        return GrantType.REFRESH_TOKEN;
    }
}
