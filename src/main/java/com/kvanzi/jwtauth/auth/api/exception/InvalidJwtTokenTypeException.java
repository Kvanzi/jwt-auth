package com.kvanzi.jwtauth.auth.api.exception;

public class InvalidJwtTokenTypeException extends JwtTokenException {
    public InvalidJwtTokenTypeException(String message) {
        super(message);
    }
}
