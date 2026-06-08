package com.kvanzi.jwtauth.auth.internal.security;

import com.kvanzi.jwtauth.auth.api.exception.InvalidJwtTokenException;
import com.kvanzi.jwtauth.auth.api.exception.InvalidJwtTokenTypeException;
import com.kvanzi.jwtauth.auth.api.exception.JwtTokenExpiredException;
import com.kvanzi.jwtauth.auth.api.security.JwtAuthenticationToken;
import com.kvanzi.jwtauth.auth.internal.dto.JwtSummary;
import com.kvanzi.jwtauth.auth.internal.entity.JwtTokenType;
import com.kvanzi.jwtauth.auth.internal.service.JwtService;
import com.kvanzi.jwtauth.shared.security.SecurityUser;
import com.kvanzi.jwtauth.user.api.security.JpaUserDetailsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationProvider implements AuthenticationProvider {
    private final JwtService jwtService;
    private final JpaUserDetailsService userDetailsService;

    @Override
    public @Nullable Authentication authenticate(@NonNull Authentication authentication)
        throws InvalidJwtTokenTypeException, JwtTokenExpiredException, InvalidJwtTokenException {
        JwtAuthenticationToken unauthenticatedToken = (JwtAuthenticationToken) authentication;
        String jwtToken = (String) unauthenticatedToken.getCredentials();

        JwtSummary jwtSummary = jwtService.extractJwtSummary(jwtToken);

        if (jwtSummary.getTokenType() != JwtTokenType.ACCESS) {
            throw new InvalidJwtTokenTypeException("Cannot use this token type for access to this resource");
        }

        if (jwtSummary.isExpired()) {
            throw new JwtTokenExpiredException("Token expired");
        }

        SecurityUser user;
        try {
            user = userDetailsService.loadUserById(jwtSummary.getUserId());
        } catch (UsernameNotFoundException e) {
            throw new InvalidJwtTokenException("Invalid access token. Re login please");
        }

        JwtAuthenticationToken authenticatedToken = new JwtAuthenticationToken(jwtToken, user);

        if (user.getLastPasswordChangedAt() == null) {
            return authenticatedToken;
        }

        if (user.getLastPasswordChangedAt().isAfter(jwtSummary.getIssuedAt())) {
            throw new JwtTokenExpiredException("Token expired");
        }

        return authenticatedToken;
    }

    @Override
    public boolean supports(@NonNull Class<?> authentication) {
        return JwtAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
