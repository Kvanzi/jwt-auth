package com.kvanzi.jwtauth.dto;

import com.kvanzi.jwtauth.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Set;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class PrivateUserResponse {

    private UUID id;
    private String username;
    private Set<Role> roles;
}
