@ApplicationModule(
        displayName = "Auth module",
        allowedDependencies = {"user::security", "shared" }
)

package com.kvanzi.jwtauth.auth;

import org.springframework.modulith.ApplicationModule;