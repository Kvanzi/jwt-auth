package com.kvanzi.jwtauth.controller;

import com.kvanzi.jwtauth.common.api.ApiResponse;
import com.kvanzi.jwtauth.dto.CreateTokensRequest;
import com.kvanzi.jwtauth.dto.CreateTokensResult;
import com.kvanzi.jwtauth.entity.JwtTokenType;
import com.kvanzi.jwtauth.properties.JwtProperties;
import com.kvanzi.jwtauth.service.AuthService;
import com.kvanzi.jwtauth.service.JwtService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final JwtProperties jwtProperties;
    private final JwtService jwtService;

    @PostMapping(path = "/tokens", version = "1")
    public ResponseEntity<@NonNull ApiResponse<Void, Void>> createTokensWithCookies(
            @Valid @RequestBody @NonNull CreateTokensRequest requestBody,
            @CookieValue(value = "refresh", required = false) @Nullable String refreshToken
    ) {
        CreateTokensResult tokens = authService.processAuthRequest(requestBody, refreshToken);

        ResponseCookie refreshCookie = buildRefreshResponseCookie(tokens.getRefreshToken());
        ResponseCookie accessCookie = buildAccessResponseCookie(tokens.getAccessToken());

        ApiResponse<Void, Void> response = new ApiResponse<>(HttpStatus.OK, "Success", null, null);
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
                .header(HttpHeaders.SET_COOKIE, accessCookie.toString())
                .body(response);
    }

    @PostMapping(path = "/tokens", version = "2")
    public ResponseEntity<@NonNull ApiResponse<@NonNull CreateTokensResult, Void>> createTokensInBody(
            @Valid @RequestBody CreateTokensRequest requestBody,
            @RequestHeader(value = "refresh", required = false) String refreshToken
    ) {
        CreateTokensResult tokens = authService.processAuthRequest(requestBody, refreshToken);
        return ApiResponse.success(tokens);
    }

    @DeleteMapping(path = "/tokens/current", version = "1")
    public ResponseEntity<@NonNull ApiResponse<Void, Void>> deleteTokens(
            @CookieValue(value = "refresh", required = false) @Nullable String refreshToken
    ) {
        if (refreshToken != null) {
            jwtService.revokeToken(refreshToken);
        }

        ResponseCookie refreshCookie = ResponseCookie.from("refresh", "")
                .path("/")
                .maxAge(0)
                .build();
        ResponseCookie accessCookie = ResponseCookie.from("access", "")
                .path("/")
                .maxAge(0)
                .build();

        ApiResponse<Void, Void> response = new ApiResponse<>(HttpStatus.OK, "Success", null, null);
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
                .header(HttpHeaders.SET_COOKIE, accessCookie.toString())
                .body(response);
    }

    private ResponseCookie buildAccessResponseCookie(String token) {
        Duration maxAge = Duration.of(jwtProperties.getAccess().getDuration(), jwtProperties.getAccess().getDurationUnit());

        return buildTokenResponseCookie(JwtTokenType.ACCESS, token, maxAge);
    }

    private ResponseCookie buildRefreshResponseCookie(String token) {
        Duration maxAge = Duration.of(jwtProperties.getRefresh().getDuration(), jwtProperties.getRefresh().getDurationUnit());

        return buildTokenResponseCookie(JwtTokenType.REFRESH, token, maxAge);
    }

    private ResponseCookie buildTokenResponseCookie(JwtTokenType tokenType, String token, Duration maxAge) {
        return ResponseCookie.from(tokenType == JwtTokenType.ACCESS ? "access" : "refresh", token)
                .maxAge(maxAge)
                .httpOnly(jwtProperties.getCookie().isHttpOnly())
                .sameSite(jwtProperties.getCookie().getSameSite().attributeValue())
                .domain(jwtProperties.getCookie().getDomain())
                .path("/")
                .build();
    }
}
