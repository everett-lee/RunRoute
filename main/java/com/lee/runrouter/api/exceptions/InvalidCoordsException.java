package com.lee.runrouter.api.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * 400 error where coordinates
 * not within the valid range
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidCoordsException extends RuntimeException {
    private String message;
    public InvalidCoordsException(String message) {
        this.message = message;
    }

    @Override
    public String getMessage() {
        return String.format("%s are invalid", this.message);
    }
}
