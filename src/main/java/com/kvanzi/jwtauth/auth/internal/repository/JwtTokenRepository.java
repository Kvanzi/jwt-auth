package com.kvanzi.jwtauth.auth.internal.repository;

import com.kvanzi.jwtauth.auth.internal.entity.JwtToken;
import jakarta.transaction.Transactional;
import org.jspecify.annotations.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;

@Repository
public interface JwtTokenRepository extends JpaRepository<@NonNull JwtToken, @NonNull UUID> {

    @Modifying
    @Transactional
    @Query("UPDATE JwtToken t SET t.revoked = true WHERE t.id = :id")
    int revokeToken(@Param("id") UUID id);

    @Modifying
    @Transactional
    @Query("UPDATE JwtToken t SET t.revoked = true WHERE t.id IN :ids")
    int revokeTokens(@Param("ids") Set<UUID> ids);

    @Modifying
    @Transactional
    @Query("DELETE FROM JwtToken t WHERE t.expiresAt < :now")
    int deleteAllExpired(Instant now);
}
