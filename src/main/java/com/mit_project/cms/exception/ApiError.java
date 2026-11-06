package com.mit_project.cms.exception;

/**
 * The JSON body we send back when an error happens.
 * A record is a short way to write a class that only holds data.
 * Java creates the constructor and getters for us.
 */


import java.time.LocalDateTime;
import java.util.Map;

public record ApiError(
        LocalDateTime timestamp,
        int status,
        String error,
        String message,
        String path,
        Map<String, String> fieldErrors) {
}