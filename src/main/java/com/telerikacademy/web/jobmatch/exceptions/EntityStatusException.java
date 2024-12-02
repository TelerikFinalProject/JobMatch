package com.telerikacademy.web.jobmatch.exceptions;

public class EntityStatusException extends RuntimeException {
    public EntityStatusException(String type, String status) {
        super(String.format("%s with status %s is not valid for the desired action!", type, status));
    }
}
