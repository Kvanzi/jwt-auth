package com.kvanzi.jwtauth.auth.api.exception;

public class InvalidJwtTokenException extends JwtTokenException {
    public InvalidJwtTokenException(String message) {
        super(message);
    }
}
