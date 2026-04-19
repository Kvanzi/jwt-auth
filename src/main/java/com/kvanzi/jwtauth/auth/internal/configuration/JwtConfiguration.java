package com.kvanzi.jwtauth.auth.internal.configuration;

import com.kvanzi.jwtauth.auth.internal.properties.JwtProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(JwtProperties.class)
public class JwtConfiguration {
}
