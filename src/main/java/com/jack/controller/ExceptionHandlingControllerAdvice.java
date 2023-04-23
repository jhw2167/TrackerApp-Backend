package com.jack.controller;

//Spring imports
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.data.rest.webmvc.support.ExcerptProjector;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

//Java imports

@ControllerAdvice
public class ExceptionHandlingControllerAdvice {

    /* UTILITY AND BASE METHODS */
    private static String createJson(String message, String reason) {
        return "{\"error\" : \"" + message + "\"," +
                "\"reason\" : \"" + reason  + "\"}";
    }

    private static ResponseEntity<String> response(String message, String reason, HttpStatus httpStatus) {
        //replace all double quotes with single to avoid JSON issues
        reason = reason.replace('"', (char) 39);
        message = message.replace('"', (char) 39);

        String json = createJson(message, reason);
        //System.out.println(json);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new ResponseEntity<>(json, headers, httpStatus);
    }

    /* EXCEPTION LIST */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<String> handleNotFound(ResourceNotFoundException ex) {
        return response("Resource not found", ex.getMessage(), HttpStatus.NOT_FOUND);
    }


    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleNotFound(Exception ex) {
        return response("General Exception", ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

}