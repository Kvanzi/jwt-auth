package com.kvanzi.jwtauth.auth.internal.security;

import com.kvanzi.jwtauth.auth.api.exception.JwtTokenException;
import com.kvanzi.jwtauth.shared.api.ApiResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import tools.jackson.databind.json.JsonMapper;

@Slf4j
@Component
@RequiredArgsConstructor
public class RestAuthenticationEntryPoint implements AuthenticationEntryPoint {
    private final JsonMapper mapper;

    @Override
    public void commence(@NonNull HttpServletRequest request,
                         @NonNull HttpServletResponse response,
                         @NonNull AuthenticationException authException) throws IOException, ServletException {
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setCharacterEncoding("UTF-8");
        response.setHeader(HttpHeaders.CACHE_CONTROL, "no-store");
        response.setHeader(HttpHeaders.PRAGMA, "no-cache");

        String message = "Authentication failure";
        boolean unexpected = false;

        if (authException instanceof JwtTokenException) {
            message = authException.getMessage();
        } else if (authException instanceof InsufficientAuthenticationException) {
            message = "Full authentication required to access this resource";
        } else {
            unexpected = true;
        }

        UUID errorId = UUID.randomUUID();
        if (unexpected) {
            log.error("[{}] Unexpected error occurred", errorId, authException);
        }

        response.getWriter().write(mapper.writeValueAsString(
            new ApiResponse<Void, UUID>(HttpStatus.UNAUTHORIZED, message, unexpected ? errorId : null, null)
        ));
        response.getWriter().flush();
    }
}
