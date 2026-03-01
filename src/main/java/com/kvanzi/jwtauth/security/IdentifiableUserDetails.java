package com.kvanzi.jwtauth.security;

import org.jspecify.annotations.NonNull;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.UUID;

public interface IdentifiableUserDetails extends UserDetails {

    @NonNull UUID getId();
}
