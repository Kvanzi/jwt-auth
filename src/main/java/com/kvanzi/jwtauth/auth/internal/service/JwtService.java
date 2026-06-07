package com.kvanzi.jwtauth.auth.internal.service;

import com.kvanzi.jwtauth.auth.internal.dto.JwtSummary;
import com.kvanzi.jwtauth.auth.api.exception.*;
import com.kvanzi.jwtauth.auth.internal.entity.JwtToken;
import com.kvanzi.jwtauth.auth.internal.entity.JwtTokenType;
import com.kvanzi.jwtauth.auth.internal.properties.JwtProperties;
import com.kvanzi.jwtauth.auth.internal.repository.JwtTokenRepository;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.SignatureException;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class JwtService {

    private static final String TOKEN_TYPE_KEY = "token_type";

    private final JwtProperties jwtProperties;
    private final JwtTokenRepository tokenRepository;
    private final JwtParser verifiedJwtParser;

    public JwtService(JwtProperties jwtProperties, JwtTokenRepository tokenRepository) {
        this.jwtProperties = jwtProperties;
        this.tokenRepository = tokenRepository;
        this.verifiedJwtParser = Jwts.parser()
                .verifyWith(jwtProperties.getSigningKey())
                .build();
    }

    @Transactional
    @EventListener(ApplicationReadyEvent.class)
    public void runOnStartup() {
        clearExpiredTokens();
    }

    @Transactional
    public void revokeToken(@NonNull UUID tokenId) {
        tokenRepository.revokeToken(tokenId);
    }

    @Transactional
    public void revokeToken(@NonNull String token) {
        UUID tokenId = extractTokenId(extractClaims(token));
        tokenRepository.revokeToken(tokenId);
    }

    @Transactional
    public void revokeTokensByIds(@NonNull Set<UUID> ids) {
        tokenRepository.revokeTokens(ids);
    }

    @Transactional
    public void revokeTokens(@NonNull Set<@NonNull String> tokens) {
        if (tokens.isEmpty()) {
            return;
        }

        Set<UUID> tokenIds = tokens.stream()
                .map(this::extractClaims)
                .map(this::extractTokenId)
                .collect(Collectors.toSet());
        revokeTokensByIds(tokenIds);
    }

    @Transactional
    @Scheduled(cron = "0 0 * * * *")
    public void clearExpiredTokens() {
        tokenRepository.deleteAllExpired(Instant.now());
    }

    public String generateRefreshToken(UUID userId) {
        return generateRefreshToken(userId, new HashMap<>());
    }

    public String generateAccessToken(UUID userId) {
        return generateAccessToken(userId, new HashMap<>());
    }

    public Claims extractClaims(String token) {
        try {
            return this.verifiedJwtParser
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (ExpiredJwtException e) {
            return e.getClaims();
        }
    }

    public UUID extractUserId(Claims claims) {
        String sub = claims.getSubject();
        return UUID.fromString(sub);
    }

    public boolean isClaimsExpired(Claims claims) {
        return extractExpiresAt(claims).isBefore(Instant.now());
    }

    public JwtTokenType extractTokenType(Claims claims) {
        return JwtTokenType.valueOf(claims.get(TOKEN_TYPE_KEY, String.class));
    }

    public Instant extractIssuedAt(Claims claims) {
        return claims.getIssuedAt().toInstant();
    }

    public Instant extractExpiresAt(Claims claims) {
        return claims.getExpiration().toInstant();
    }

    public @Nullable UUID extractTokenId(Claims claims) {
        String id = claims.getId();
        return id != null ? UUID.fromString(id) : null;
    }

    public JwtSummary extractJwtSummary(String token) {
        Claims claims = extractClaims(token);

        return new JwtSummary(
                extractTokenId(claims),
                extractUserId(claims),
                extractTokenType(claims),
                extractIssuedAt(claims),
                isClaimsExpired(claims)
        );
    }

    private String generateRefreshToken(UUID userId, Map<String, Object> extraClaims) {
        Instant issuedAt = Instant.now();
        Instant expiresAt = issuedAt.plus(jwtProperties.getRefresh().getDuration(), jwtProperties.getRefresh().getDurationUnit());

        JwtToken tokenEntity = JwtToken.builder()
                .tokenType(JwtTokenType.REFRESH)
                .expiresAt(expiresAt)
                .issuedAt(issuedAt)
                .userId(userId)
                .build();
        tokenEntity = tokenRepository.save(tokenEntity);
        extraClaims.put(TOKEN_TYPE_KEY, JwtTokenType.REFRESH);

        return generateToken(
                issuedAt,
                expiresAt,
                userId,
                extraClaims,
                tokenEntity.getId()
        );
    }

    private String generateAccessToken(UUID userId, Map<String, Object> extraClaims) {
        Instant issuedAt = Instant.now();
        Instant expiresAt = issuedAt.plus(jwtProperties.getAccess().getDuration(), jwtProperties.getAccess().getDurationUnit());

        extraClaims.put(TOKEN_TYPE_KEY, JwtTokenType.ACCESS);

        return generateToken(
                issuedAt,
                expiresAt,
                userId,
                extraClaims,
                null
        );
    }

    @Transactional
    public @NonNull JwtToken validateRefreshTokenOrThrow(@Nullable String refreshToken)
            throws MissingRefreshTokenException, InvalidJwtTokenException, InvalidJwtTokenTypeException, JwtTokenExpiredException {
        if (refreshToken == null || refreshToken.isBlank()) {
            throw new MissingRefreshTokenException("Refresh token cannot be null or blank");
        }

        try {
            JwtSummary jwtSummary = extractJwtSummary(refreshToken);

            if (jwtSummary.getTokenType() != JwtTokenType.REFRESH) {
                throw new InvalidJwtTokenTypeException("You cannot use this type of token to refresh an access token");
            }

            if (jwtSummary.getTokenId() == null) {
                throw new IllegalStateException("Refresh token does not have an id");
            }

            if (jwtSummary.isExpired()) {
                findById(jwtSummary.getTokenId()).ifPresent(this::deleteToken);
                throw new JwtTokenExpiredException("Refresh token expired. Re login please");
            }

            JwtToken refreshTokenEntity = findById(jwtSummary.getTokenId())
                    .orElseThrow(() -> new InvalidJwtTokenException("Invalid refresh token. Re login please"));

            if (refreshTokenEntity.isRevoked()) {
                throw new InvalidJwtTokenException("Invalid refresh token. Re login please");
            }

            return refreshTokenEntity;
        } catch (SignatureException | UnsupportedJwtException | MalformedJwtException e) {
            throw new InvalidJwtTokenException("Invalid refresh token. Re login please");
        } catch (MissingRefreshTokenException | JwtTokenException e) {
            throw e;
        } catch (RuntimeException e) {
            log.error("", e);
            throw new IllegalStateException("Unexpected error occurred. Re login please");
        }
    }

    @Transactional
    protected @NonNull Optional<JwtToken> findById(@NonNull UUID id) {
        return tokenRepository.findById(id);
    }

    @Transactional
    protected void deleteToken(@NonNull JwtToken token) {
        tokenRepository.delete(token);
    }

    private String generateToken(@Nullable Instant issuedAt, @Nullable Instant expiresAt, @NonNull UUID userId, @Nullable Map<String, Object> extraClaims, @Nullable UUID tokenId) {
        JwtBuilder builder = Jwts.builder();

        if (issuedAt != null) {
            builder.issuedAt(Date.from(issuedAt));
        }

        if (expiresAt != null) {
            builder.expiration(Date.from(expiresAt));
        }

        if (extraClaims != null && !extraClaims.isEmpty()) {
            builder.claims(extraClaims);
        }

        if (tokenId != null) {
            builder.id(tokenId.toString());
        }

        return builder
                .subject(userId.toString())
                .signWith(jwtProperties.getSigningKey())
                .compact();
    }
}
