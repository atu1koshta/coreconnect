package com.unemployed.coreconnect.exception;

import java.io.IOException;

import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import ch.qos.logback.classic.Logger;

@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {
	private static final Logger log = (Logger) LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(NotSameNetworkException.class)
    public ResponseEntity<String> handleNotSameNetworkException(NotSameNetworkException ex) {
//        log.error("NotSameNetworkException: ", ex);
        return new ResponseEntity<>("Device not found in network!", HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MacAddressNotFoundException.class)
    public ResponseEntity<String> handleMacAddressNotFoundException(MacAddressNotFoundException ex) {
//        log.error("MacAddressNotFoundException: ", ex);
        return new ResponseEntity<>("MAC address couldn't be fetched!", HttpStatus.NOT_FOUND);
    }
    
    @ExceptionHandler(IOException.class)
	public ResponseEntity<String> handleIOException(IOException ex) {
//		log.error("IOException: ", ex);
		return new ResponseEntity<>("An error occurred while processing the request.",
				HttpStatus.INTERNAL_SERVER_ERROR);
	}

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGeneralException(Exception ex) {
        log.error("Exception: ", ex);
        return new ResponseEntity<>("An error occurred while processing the request.", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgumentException(IllegalArgumentException ex, WebRequest request) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }
}
