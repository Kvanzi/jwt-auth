package com.kvanzi.jwtauth.shared.security;

import java.util.UUID;
import org.jspecify.annotations.NonNull;
import org.springframework.security.core.userdetails.UserDetails;

public interface IdentifiableUserDetails extends UserDetails {
    @NonNull UUID getId();
}
