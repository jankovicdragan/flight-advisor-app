package com.jankovicd.flightadvisor.city.controller;

import java.util.List;

import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.jankovicd.flightadvisor.city.dto.CityDTO;
import com.jankovicd.flightadvisor.city.dto.CityPreviewDTO;
import com.jankovicd.flightadvisor.city.service.CityService;
import com.jankovicd.flightadvisor.util.FlightAdvisorController;

@RestController
@RequestMapping("/city")
public class CityController extends FlightAdvisorController {

	private final CityService cityService;

	public CityController(CityService cityService) {
		this.cityService = cityService;
	}

	@PreAuthorize("hasAuthority('REGULAR')")
	@GetMapping
	public ResponseEntity<List<CityPreviewDTO>> get(@RequestParam(name = "name", required = false) String name,
			@RequestParam(name = "commentCount", required = false) Integer commentCount) {
		List<CityPreviewDTO> allCities = cityService.getCities(name, commentCount);
		return ResponseEntity.ok(allCities);
	}
 
	@PreAuthorize("hasAuthority('ADMIN')")
	@PostMapping
	public ResponseEntity<Void> add(@RequestBody @Valid CityDTO cityDTO, Errors errors) {
		checkForErrors(errors);
		cityService.addCity(cityDTO);
		return new ResponseEntity<>(HttpStatus.CREATED);
	}

}
