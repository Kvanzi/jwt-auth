package com.kvanzi.jwtauth.user.api.security;

import com.kvanzi.jwtauth.shared.security.SecurityUser;
import com.kvanzi.jwtauth.user.internal.service.UserService;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserDetailsService implements org.springframework.security.core.userdetails.UserDetailsService {

    private final UserService userService;

    @Override
    public @NonNull SecurityUser loadUserByUsername(@NonNull String username) throws UsernameNotFoundException {
        return userService.findByUsernameIgnoreCase(username)
                .map(userService::mapToSecurityUser)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    public @NonNull SecurityUser loadUserById(@NonNull UUID id) throws UsernameNotFoundException {
        return userService.findById(id)
                .map(userService::mapToSecurityUser)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }
}
