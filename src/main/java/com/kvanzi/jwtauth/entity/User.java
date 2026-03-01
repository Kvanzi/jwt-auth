package com.kvanzi.jwtauth.entity;

import com.kvanzi.jwtauth.common.persistence.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.jspecify.annotations.NonNull;

import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(
        name = "users"
)
public class User extends BaseEntity {

    @Column(name = "username", length = 16)
    private @NonNull String username;

    @Column(name = "password_hash", length = 60, nullable = false)
    private @NonNull String passwordHash;

    @Setter(AccessLevel.NONE)
    @Builder.Default
    @CollectionTable(
            name = "user_roles"
    )
    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private @NonNull Set<Role> roles = new HashSet<>(Set.of(Role.USER));

    public @NonNull Set<Role> getRoles() {
        return Collections.unmodifiableSet(this.roles);
    }

    public @NonNull User addRole(@NonNull Role role) {
        Objects.requireNonNull(role, "Role cannot be null");
        this.roles.add(role);
        return this;
    }

    public @NonNull User removeRole(@NonNull Role role) {
        Objects.requireNonNull(role, "Role cannot be null");
        this.roles.remove(role);
        return this;
    }

    public boolean hasRole(@NonNull Role role) {
        Objects.requireNonNull(role, "Role cannot be null");
        return this.roles.contains(role);
    }
}
