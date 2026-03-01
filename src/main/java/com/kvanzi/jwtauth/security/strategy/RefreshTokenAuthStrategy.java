package com.kvanzi.jwtauth.security.strategy;

import com.kvanzi.jwtauth.dto.CreateTokensRequest;
import com.kvanzi.jwtauth.dto.CreateTokensResult;
import com.kvanzi.jwtauth.dto.GrantType;
import com.kvanzi.jwtauth.entity.JwtToken;
import com.kvanzi.jwtauth.exception.InvalidJwtTokenException;
import com.kvanzi.jwtauth.security.SecurityUser;
import com.kvanzi.jwtauth.service.JwtService;
import com.kvanzi.jwtauth.service.UserDetailsService;
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
