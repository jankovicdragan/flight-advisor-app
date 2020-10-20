package com.jankovicd.flightadvisor.flight.controller;

import org.springframework.beans.factory.annotation.Lookup;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.jankovicd.flightadvisor.flight.dto.FlightDTO;
import com.jankovicd.flightadvisor.flight.importing.CityImportHelper;
import com.jankovicd.flightadvisor.flight.service.AirportService;
import com.jankovicd.flightadvisor.flight.service.FlightService;
import com.jankovicd.flightadvisor.flight.service.RouteService;

@RestController
@RequestMapping("flight")
public class FlightController {

	private final RouteService routeService;
	private final AirportService airportService;
	private final CityImportHelper cityImport;

	public FlightController(RouteService routeService, AirportService airportService, CityImportHelper cityImport) {
		this.routeService = routeService;
		this.airportService = airportService;
		this.cityImport = cityImport;
	}

	@Lookup
	public FlightService getFlightService() {
		return null;
	}

	@PreAuthorize("hasAuthority('REGULAR')")
	@GetMapping
	public ResponseEntity<FlightDTO> findCheapestFlight(@RequestParam("sourceCityId") Long sourceCityId,
			@RequestParam("destinationCityId") Long destinationCityId) {
		return ResponseEntity.ok(getFlightService().findCheapestRoute(sourceCityId, destinationCityId));
	}

	@PreAuthorize("hasAuthority('ADMIN')")
	@PostMapping("/import/airports")
	@Transactional
	public ResponseEntity<Void> importAirportFile(@RequestParam("file") MultipartFile file) {
		airportService.importFromFile(file);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}

	@PreAuthorize("hasAuthority('ADMIN')")
	@PostMapping("/import/routes")
	@Transactional
	public ResponseEntity<Void> importRouteFile(@RequestParam("file") MultipartFile file) {
		routeService.importFromFile(file);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}

	@PreAuthorize("hasAuthority('ADMIN')")
	@GetMapping("/import/default/airports")
	@Transactional
	public ResponseEntity<Void> importAirportsData() {
		airportService.importFromFile();
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}

	@PreAuthorize("hasAuthority('ADMIN')")
	@GetMapping("/import/default/routes")
	@Transactional
	public ResponseEntity<Void> importRoutesData() {
		routeService.importFromFile();
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}

	@PreAuthorize("hasAuthority('ADMIN')")
	@GetMapping("/import/default/cities")
	@Transactional
	public ResponseEntity<Void> importCitiesData() {
		cityImport.importCities();
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}

}
