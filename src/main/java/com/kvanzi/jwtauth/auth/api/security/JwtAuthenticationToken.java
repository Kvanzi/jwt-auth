package com.kvanzi.jwtauth.auth.api.security;

import com.kvanzi.jwtauth.shared.security.SecurityUser;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;

public class JwtAuthenticationToken extends AbstractAuthenticationToken {

    private final String jwtToken;
    private final SecurityUser securityUser;

    public JwtAuthenticationToken(@NonNull String jwtToken, @NonNull SecurityUser securityUser) {
        super(securityUser.getAuthorities());
        this.jwtToken = jwtToken;
        this.securityUser = securityUser;
        setAuthenticated(true);
    }

    public JwtAuthenticationToken(@NonNull String jwtToken) {
        super(AuthorityUtils.NO_AUTHORITIES);
        this.jwtToken = jwtToken;
        this.securityUser = null;
        setAuthenticated(false);
    }

    @Override
    public @NonNull Object getCredentials() {
        return this.jwtToken;
    }

    @Override
    public @Nullable Object getPrincipal() {
        return securityUser;
    }
}
