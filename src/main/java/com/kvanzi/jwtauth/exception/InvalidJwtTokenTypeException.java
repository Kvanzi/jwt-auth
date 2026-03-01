package com.kvanzi.jwtauth.exception;

public class InvalidJwtTokenTypeException extends JwtTokenException {
    public InvalidJwtTokenTypeException(String message) {
        super(message);
    }
}
