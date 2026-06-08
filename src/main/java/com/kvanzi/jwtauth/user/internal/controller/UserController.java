package com.kvanzi.jwtauth.user.internal.controller;

import com.kvanzi.jwtauth.shared.api.ApiResponse;
import com.kvanzi.jwtauth.user.internal.dto.CreateUserRequest;
import com.kvanzi.jwtauth.user.internal.dto.PrivateUserResponse;
import com.kvanzi.jwtauth.user.api.exception.UserNotFoundException;
import com.kvanzi.jwtauth.shared.security.IdentifiableUserDetails;
import com.kvanzi.jwtauth.user.internal.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;

    @PostMapping(version = "1")
    public ResponseEntity<@NonNull ApiResponse<PrivateUserResponse, Void>> createUser(
            @Valid @RequestBody @NonNull CreateUserRequest requestBody
    ) {
        PrivateUserResponse user = userService.createUser(requestBody);
        return ApiResponse.success(user);
    }

    @GetMapping(path = "/me", version = "1")
    public ResponseEntity<@NonNull ApiResponse<PrivateUserResponse, Void>> getMe(
            @AuthenticationPrincipal @NonNull IdentifiableUserDetails userDetails
    ) {
        PrivateUserResponse user = userService.findById(userDetails.getId())
                .map(userService::mapToPrivateUserResponse)
                .orElseThrow(UserNotFoundException::new);
        return ApiResponse.success(user);
    }
}
