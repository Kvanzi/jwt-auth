package com.kvanzi.jwtauth.mapper;

import com.kvanzi.jwtauth.dto.PrivateUserResponse;
import com.kvanzi.jwtauth.entity.Role;
import com.kvanzi.jwtauth.entity.User;
import com.kvanzi.jwtauth.security.SecurityUser;
import org.jspecify.annotations.NonNull;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface UserMapper {

    @Mapping(target = "password", source = "passwordHash")
    @Mapping(target = "authorities", source = "roles")
    @NonNull SecurityUser mapToSecurityUser(@NonNull User entity);

    @NonNull PrivateUserResponse mapToPrivateUserResponse(@NonNull User entity);

    default @NonNull GrantedAuthority roleToAuthority(@NonNull Role role) {
        return new SimpleGrantedAuthority("ROLE_" + role.name());
    }
}
