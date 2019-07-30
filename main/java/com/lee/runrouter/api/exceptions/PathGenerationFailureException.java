package com.lee.runrouter.api.exceptions;

public class PathGenerationFailureException extends RuntimeException {
    private String message;

    public PathGenerationFailureException(String message) {
        this.message = message;
    }
    @Override
    public String getMessage() {
        return String.format("Unable to generate a path. Try adjusting your parameters", this.message);
    }
}
