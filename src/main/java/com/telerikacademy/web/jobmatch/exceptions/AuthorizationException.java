package com.telerikacademy.web.jobmatch.exceptions;

public class AuthorizationException extends RuntimeException {
    public AuthorizationException(String action, String entity) {
        super(String.format("You do not have permissions to %s this %s!", action, entity));
    }
}
