package com.jankovicd.flightadvisor.auth.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jankovicd.flightadvisor.auth.dto.UserDTO;
import com.jankovicd.flightadvisor.auth.service.UserService;
import com.jankovicd.flightadvisor.util.FlightAdvisorController;

@RestController
@RequestMapping("/auth")
public class UserController extends FlightAdvisorController {

	private final UserService userService;

	public UserController(UserService userService) {
		this.userService = userService;
	}

	@PostMapping("/register")
	public ResponseEntity<Void> register(@RequestBody UserDTO userDTO, Errors errors) {
		checkForErrors(errors);
		userService.register(userDTO);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}

}
