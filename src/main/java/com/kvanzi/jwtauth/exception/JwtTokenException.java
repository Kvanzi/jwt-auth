package com.kvanzi.jwtauth.exception;

import org.springframework.security.core.AuthenticationException;

public class JwtTokenException extends AuthenticationException {
    public JwtTokenException(String message) {
        super(message);
    }
}
