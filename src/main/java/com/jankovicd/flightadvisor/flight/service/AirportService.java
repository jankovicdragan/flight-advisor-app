package com.jankovicd.flightadvisor.flight.service;

import java.text.MessageFormat;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.jankovicd.flightadvisor.exception.FlightAdvisorException;
import com.jankovicd.flightadvisor.flight.importing.AirportFileImporter;
import com.jankovicd.flightadvisor.flight.importing.FileImporter;
import com.jankovicd.flightadvisor.flight.model.Airport;
import com.jankovicd.flightadvisor.flight.repository.AirportRepository;
import com.jankovicd.flightadvisor.util.Bundle;

@Service
public class AirportService {

	private final AirportRepository airportRepository;
	private final FileImporter fileImporter;

	public AirportService(AirportRepository airportRepository, AirportFileImporter airportFileImporter) {
		this.airportRepository = airportRepository;
		this.fileImporter = airportFileImporter;
	}

	public List<Airport> findAll() {
		return airportRepository.findAll();
	}

	public Airport findById(Integer id) {
		Optional<Airport> airport = airportRepository.findById(id);
		if (airport.isPresent()) {
			return airport.get();
		}
		throw new FlightAdvisorException(MessageFormat.format(Bundle.getBundleMessage("airport.id.invalid"), id));
	}

	public void importFromFile() {
		airportRepository.deleteAll();
		fileImporter.importDefaultFile();
	}

	public void importFromFile(MultipartFile file) {
		airportRepository.deleteAll();
		fileImporter.importFile(file);
	}
}
