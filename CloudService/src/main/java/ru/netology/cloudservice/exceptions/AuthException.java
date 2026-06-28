package ru.netology.cloudservice.exceptions;

public class AuthException extends RuntimeException {

    public AuthException(String message) {

        super(message);
    }
}