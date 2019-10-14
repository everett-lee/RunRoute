package com.lee.runrouter.api.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.Date;

/**
 *  Custom exceptions arsing from the report procedure call.
 */
@ControllerAdvice
@RestController
public class CustomResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(Exception.class)
    public final ResponseEntity<Error> handleAllExceptions(Exception ex, WebRequest request) {

        ExceptionResponse exceptionResponse =
                new ExceptionResponse(new Date(), ex.getMessage(), request.getDescription(false));

        return new ResponseEntity<Error>((MultiValueMap<String, String>) exceptionResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    // where the provided coordinates are invalid
    @ExceptionHandler(InvalidCoordsException.class)
    public final ResponseEntity<Error> handleInvalidCoordsException(Exception ex, WebRequest request) {

        ExceptionResponse exceptionResponse =
                new ExceptionResponse(new Date(), ex.getMessage(), request.getDescription(false));

        return new ResponseEntity<Error>((MultiValueMap<String, String>) exceptionResponse, HttpStatus.BAD_REQUEST);
    }

    // where the provided length is invalid
    @ExceptionHandler(InvalidDistanceException.class)
    public final ResponseEntity<Error> handleInvalidLengthException(Exception ex, WebRequest request) {

        ExceptionResponse exceptionResponse =
                new ExceptionResponse(new Date(), ex.getMessage(), request.getDescription(false));

        return new ResponseEntity<Error>((MultiValueMap<String, String>) exceptionResponse, HttpStatus.BAD_REQUEST);
    }

    // where a valid route is not generated
    @ExceptionHandler(PathGenerationFailureException.class)
    public final ResponseEntity<Error> handlePassGenerationFailureException(Exception ex, WebRequest request) {

        ExceptionResponse exceptionResponse =
                new ExceptionResponse(new Date(), ex.getMessage(), request.getDescription(false));

        return new ResponseEntity<Error>((MultiValueMap<String, String>) exceptionResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
