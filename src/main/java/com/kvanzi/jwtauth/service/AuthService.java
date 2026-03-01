package com.kvanzi.jwtauth.service;

import com.kvanzi.jwtauth.dto.CreateTokensRequest;
import com.kvanzi.jwtauth.dto.CreateTokensResult;
import com.kvanzi.jwtauth.dto.GrantType;
import com.kvanzi.jwtauth.security.strategy.AuthStrategy;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class AuthService {

    private final Map<GrantType, AuthStrategy> authStrategies;

    public AuthService(List<AuthStrategy> authStrategies) {
        this.authStrategies = authStrategies.stream()
                .collect(Collectors.toMap(
                        AuthStrategy::getSupportedGrantType,
                        Function.identity()
                ));
    }

    public @NonNull CreateTokensResult processAuthRequest(@NonNull CreateTokensRequest request, @Nullable String refreshToken) {
        GrantType grantType = request.getGrantType();
        AuthStrategy strategy = authStrategies.get(grantType);

        if (strategy == null) {
            throw new IllegalStateException("Strategy '%s' not found".formatted(grantType));
        }

        return strategy.execute(request, refreshToken);
    }
}
