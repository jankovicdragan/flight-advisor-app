package com.jankovicd.flightadvisor.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.jankovicd.flightadvisor.util.Bundle;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@ControllerAdvice
public class FlightAdvisorExceptionHandler extends ResponseEntityExceptionHandler {

	@ExceptionHandler(FlightAdvisorException.class)
	protected ResponseEntity<String> handleFlightAdvisorException(FlightAdvisorException exception) {
		String message = exception.getMessage();
		log.error(message);
		return ResponseEntity.badRequest().body(message);
	}

	@ExceptionHandler(Exception.class)
	protected ResponseEntity<String> handleException(Exception exception) {
		String message = exception.getMessage();
		log.error(message);
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
				.body(Bundle.getBundleMessage("error.unexpected"));
	}

}
