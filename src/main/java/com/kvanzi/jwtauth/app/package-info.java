@ApplicationModule(
        displayName = "Application module",
        allowedDependencies = { "user::exception", "auth::exception", "auth::security", "shared" }
)

package com.kvanzi.jwtauth.app;

import org.springframework.modulith.ApplicationModule;