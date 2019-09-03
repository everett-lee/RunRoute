package com.lee.runrouter.api.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * 400 error where length
 * not within the valid range
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidDistanceException extends RuntimeException {
    private String message;
    public InvalidDistanceException(String message) {
        this.message = message;
    }

    @Override
    public String getMessage() {
        return String.format("%s is invalid. It must be between 2,000 and 21,000 metres", this.message);
    }
}
