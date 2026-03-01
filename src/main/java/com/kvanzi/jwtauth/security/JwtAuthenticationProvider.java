package com.kvanzi.jwtauth.security;

import com.kvanzi.jwtauth.dto.JwtSummary;
import com.kvanzi.jwtauth.entity.JwtTokenType;
import com.kvanzi.jwtauth.exception.InvalidJwtTokenException;
import com.kvanzi.jwtauth.exception.InvalidJwtTokenTypeException;
import com.kvanzi.jwtauth.exception.JwtTokenException;
import com.kvanzi.jwtauth.exception.JwtTokenExpiredException;
import com.kvanzi.jwtauth.service.JwtService;
import com.kvanzi.jwtauth.service.UserDetailsService;
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
    private final UserDetailsService userDetailsService;

    @Override
    public @Nullable Authentication authenticate(@NonNull Authentication authentication) throws InvalidJwtTokenException, JwtTokenExpiredException {
        JwtAuthenticationToken unauthenticatedToken = (JwtAuthenticationToken) authentication;
        String jwtToken = (String) unauthenticatedToken.getCredentials();

        try {
            JwtSummary jwtSummary = jwtService.extractJwtSummary(jwtToken);

            if (jwtSummary.getTokenType() != JwtTokenType.ACCESS) {
                throw new InvalidJwtTokenTypeException("Cannot use this token type for access to this resource");
            }

            if (jwtSummary.isTokenExpired()) {
                throw new JwtTokenExpiredException("Token expired");
            }

            SecurityUser user;
            try {
                user = userDetailsService.loadUserById(jwtSummary.getUserId());
            } catch (UsernameNotFoundException e) {
                throw new InvalidJwtTokenException("Invalid access token. Re login please");
            }

            JwtAuthenticationToken authenticatedToken = new JwtAuthenticationToken(
                    jwtToken,
                    user
            );

            if (user.getLastPasswordChangedAt() == null) {
                return authenticatedToken;
            }

            if (user.getLastPasswordChangedAt().isAfter(jwtSummary.getIssuedAt())) {
                throw new JwtTokenExpiredException("Token expired");
            }

            return authenticatedToken;
        } catch (JwtTokenException e) {
            throw e;
        } catch (Exception e) {
            log.error("", e);
            throw new JwtTokenException("Unexpected authorization error occurred");
        }
    }

    @Override
    public boolean supports(@NonNull Class<?> authentication) {
        return JwtAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
