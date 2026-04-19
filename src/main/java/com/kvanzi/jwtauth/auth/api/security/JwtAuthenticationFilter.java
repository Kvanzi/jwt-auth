package com.kvanzi.jwtauth.auth.api.security;

import com.kvanzi.jwtauth.auth.api.exception.JwtTokenException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final AuthenticationManager authManager;
    private final AuthenticationEntryPoint authEntryPoint;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain) throws ServletException, IOException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.isAuthenticated()) {
            filterChain.doFilter(request, response);
            return;
        }

        if (request.getCookies() == null) {
            filterChain.doFilter(request, response);
            return;
        }

        String accessToken = extractAccessTokenFromCookies(request);

        if (accessToken == null) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            JwtAuthenticationToken authenticationToken = new JwtAuthenticationToken(accessToken);
            authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            Authentication authenticatedToken = authManager.authenticate(authenticationToken);
            SecurityContextHolder.getContext().setAuthentication(authenticatedToken);
        } catch (JwtTokenException e) {
            SecurityContextHolder.clearContext();
            handleFailure(request, response, e);
            return;
        } catch (Exception e) {
            log.error("", e);
            SecurityContextHolder.clearContext();
            handleFailure(request, response, new JwtTokenException("Unexpected authentication error occurred"));
            return;
        }

        filterChain.doFilter(request, response);
    }

    private void handleFailure(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, JwtTokenException e) throws ServletException, IOException {
        authEntryPoint.commence(request, response, e);
    }

    private @Nullable String extractAccessTokenFromCookies(@NonNull HttpServletRequest request) {
        if (request.getCookies() == null) return null;
        return Arrays.stream(request.getCookies())
                .filter(cookie -> "access".equals(cookie.getName()))
                .map(Cookie::getValue)
                .findFirst()
                .orElse(null);
    }
}
