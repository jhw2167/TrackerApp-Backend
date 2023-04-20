package com.jack.controller;

//Spring imports
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

//Java imports

@ControllerAdvice
public class ExceptionHandlingControllerAdvice {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<String> handleNotFound(ResourceNotFoundException ex) {
        return response("Resource not found", ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    private static String createJson(String message, String reason) {
        return "{\"error\" : \"" + message + "\"," +
                "\"reason\" : \"" + reason  + "\"}";
    }

    private static ResponseEntity<String> response(String message, String reason, HttpStatus httpStatus) {
        String json = createJson(message, reason);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new ResponseEntity<>(json, headers, httpStatus);
    }

}