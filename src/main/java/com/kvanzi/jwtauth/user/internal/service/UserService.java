package com.kvanzi.jwtauth.user.internal.service;

import com.kvanzi.jwtauth.shared.security.SecurityUser;
import com.kvanzi.jwtauth.user.api.exception.UsernameTakenException;
import com.kvanzi.jwtauth.user.internal.dto.CreateUserRequest;
import com.kvanzi.jwtauth.user.internal.dto.PrivateUserResponse;
import com.kvanzi.jwtauth.user.internal.entity.User;
import com.kvanzi.jwtauth.user.internal.mapper.UserMapper;
import com.kvanzi.jwtauth.user.internal.repository.UserRepository;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public @NonNull PrivateUserResponse createUser(@NonNull CreateUserRequest requestBody) {
        if (userRepository.existsByUsernameIgnoreCase(requestBody.getUsername())) {
            throw new UsernameTakenException("This username is taken by another user");
        }

        String encodedPassword = Objects.requireNonNull(
            passwordEncoder.encode(requestBody.getPassword()),
            "Password encoder returned null value"
        );

        User user = User.builder()
            .username(requestBody.getUsername())
            .passwordHash(encodedPassword)
            .build();

        return userMapper.mapToPrivateUserResponse(
            userRepository.save(user)
        );
    }

    public Optional<User> findById(@NonNull UUID id) {
        return userRepository.findById(id);
    }

    public Optional<User> findByUsernameIgnoreCase(@NonNull String username) {
        return userRepository.findByUsernameIgnoreCase(username);
    }

    public @NonNull SecurityUser mapToSecurityUser(@NonNull User entity) {
        return userMapper.mapToSecurityUser(entity);
    }

    public @NonNull PrivateUserResponse mapToPrivateUserResponse(@NonNull User entity) {
        return userMapper.mapToPrivateUserResponse(entity);
    }
}
