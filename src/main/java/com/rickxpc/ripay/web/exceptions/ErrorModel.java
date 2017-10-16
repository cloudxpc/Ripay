package com.rickxpc.ripay.web.exceptions;

public class ErrorModel {
    private final String errorMessage;
    private final String description;

    public ErrorModel(String message) {
        this(message, null);
    }

    public ErrorModel(String message, String description) {
        this.errorMessage = message;
        this.description = description;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public String getDescription() {
        return description;
    }
}
