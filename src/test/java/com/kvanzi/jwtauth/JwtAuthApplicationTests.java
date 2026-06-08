package com.kvanzi.jwtauth;

import org.junit.jupiter.api.Test;
import org.springframework.modulith.core.ApplicationModules;

class JwtAuthApplicationTests {
    private static final ApplicationModules modules = ApplicationModules.of(JwtAuthApplication.class);

    @Test
    void applicationModulesAreCompliant() {
        modules.verify();
    }
}
