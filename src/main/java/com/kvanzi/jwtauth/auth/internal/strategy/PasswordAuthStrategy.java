package com.kvanzi.jwtauth.auth.internal.strategy;

import com.kvanzi.jwtauth.auth.internal.dto.CreateTokensRequest;
import com.kvanzi.jwtauth.auth.internal.dto.CreateTokensResult;
import com.kvanzi.jwtauth.auth.internal.dto.GrantType;
import com.kvanzi.jwtauth.auth.api.exception.InvalidCredentialsException;
import com.kvanzi.jwtauth.auth.api.exception.MissingCredentialsException;
import com.kvanzi.jwtauth.shared.security.IdentifiableUserDetails;
import com.kvanzi.jwtauth.auth.internal.service.JwtService;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class PasswordAuthStrategy implements AuthStrategy {

    private final AuthenticationManager authManager;
    private final JwtService jwtService;

    @Override
    @Transactional
    public @NonNull CreateTokensResult execute(@NonNull CreateTokensRequest request, @Nullable String refreshToken) {
        if (request.getUsername() == null || request.getPassword() == null) {
            throw new MissingCredentialsException("Username and password are required for password grant");
        }

        try {
            Authentication auth = authManager.authenticate(new UsernamePasswordAuthenticationToken(
                    request.getUsername(),
                    request.getPassword()
            ));

            IdentifiableUserDetails userDetails = (IdentifiableUserDetails) auth.getPrincipal();
            if (userDetails == null) {
                throw new IllegalStateException("Authentication returned null principal");
            }

            return new CreateTokensResult(
                    jwtService.generateAccessToken(userDetails.getId()),
                    jwtService.generateRefreshToken(userDetails.getId())
            );
        } catch (BadCredentialsException e) {
            throw new InvalidCredentialsException("Invalid login data");
        }
    }

    @Override
    public @NonNull GrantType getSupportedGrantType() {
        return GrantType.PASSWORD;
    }
}
