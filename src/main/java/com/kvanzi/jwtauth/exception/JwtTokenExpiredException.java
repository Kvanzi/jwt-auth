package com.kvanzi.jwtauth.exception;

public class JwtTokenExpiredException extends JwtTokenException {
    public JwtTokenExpiredException(String message) {
        super(message);
    }
}
