package com.example.event.manager.exceptions;

public class LoginAlreadyExistsException extends RuntimeException {
    public LoginAlreadyExistsException(String login) {
        super("User with login '" + login + "' already exists");
    }
}

