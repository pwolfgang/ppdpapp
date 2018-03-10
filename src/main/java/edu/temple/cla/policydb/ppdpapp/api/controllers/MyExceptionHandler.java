package edu.temple.cla.policydb.ppdpapp.api.controllers;

import javax.servlet.http.HttpServletRequest;
import org.apache.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 *
 * @author Paul
 */
@ControllerAdvice
public class MyExceptionHandler {

    private static final Logger LOGGER = Logger.getLogger(MyExceptionHandler.class);
    
    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> exceptionCaught(HttpServletRequest request, Exception ex) {
        String message = "Error Processing " + request.getRequestURL().toString();
        LOGGER.error(message, ex);
        return new ResponseEntity<>(message + " See log for details", HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
