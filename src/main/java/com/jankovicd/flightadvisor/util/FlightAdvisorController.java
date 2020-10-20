package com.jankovicd.flightadvisor.util;

import org.springframework.validation.Errors;

import com.jankovicd.flightadvisor.exception.FlightAdvisorException;

public abstract class FlightAdvisorController {

	public void checkForErrors(Errors errors) {
		if (errors.hasErrors()) {
			throw new FlightAdvisorException(errors.getAllErrors().get(0).getDefaultMessage());
		}
	}
}
