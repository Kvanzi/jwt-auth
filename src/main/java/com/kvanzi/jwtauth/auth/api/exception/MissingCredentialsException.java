package com.kvanzi.jwtauth.auth.api.exception;

public class MissingCredentialsException extends RuntimeException {
    public MissingCredentialsException(String message) {
        super(message);
    }
}
