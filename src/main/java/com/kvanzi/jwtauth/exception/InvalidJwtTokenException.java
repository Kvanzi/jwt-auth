package com.kvanzi.jwtauth.exception;

public class InvalidJwtTokenException extends JwtTokenException {
    public InvalidJwtTokenException(String message) {
        super(message);
    }
}
