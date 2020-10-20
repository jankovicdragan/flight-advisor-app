package com.jankovicd.flightadvisor.flight.importing;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.MessageFormat;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.jankovicd.flightadvisor.exception.FlightAdvisorException;
import com.jankovicd.flightadvisor.flight.model.Airport;
import com.jankovicd.flightadvisor.flight.model.Route;
import com.jankovicd.flightadvisor.flight.repository.AirportRepository;
import com.jankovicd.flightadvisor.flight.repository.RouteRepository;
import com.jankovicd.flightadvisor.flight.util.TypeConverter;
import com.jankovicd.flightadvisor.util.Bundle;

@Component
public class RouteFileImporter implements FileImporter {
	
	private final AirportRepository airportRepository;
	private final RouteRepository routeRepository;
	private final TypeConverter converter;
	
	@Value("${import.default.route}")
	private String defaultFilePath;

	public RouteFileImporter(AirportRepository airportRepository, RouteRepository routeRepository,
			TypeConverter converter) {
		this.airportRepository = airportRepository;
		this.routeRepository = routeRepository;
		this.converter = converter;
	}

	public void importDefaultFile() {
		try {
			Reader reader = Files.newBufferedReader(Paths.get(defaultFilePath));
			importFromReader(reader);
		} catch (IOException e) {
			throw new FlightAdvisorException(
					MessageFormat.format(Bundle.getBundleMessage("file.parsing.failed"), defaultFilePath));
		}
	}

	public void importFile(MultipartFile file) {
		try {
			Reader reader = new BufferedReader(new InputStreamReader(file.getInputStream()));
			importFromReader(reader);
		} catch (IOException e) {
			throw new FlightAdvisorException(
					MessageFormat.format(Bundle.getBundleMessage("file.parsing.failed"), file.getName()));
		}
	}

	private void importFromReader(Reader reader) throws IOException {
		CSVParser parser = CSVParser.parse(reader, CSVFormat.INFORMIX_UNLOAD.withNullString(NULL_CHARACTER));
		List<Airport> airports = airportRepository.findAll();
		if (airports != null && !airports.isEmpty()) {
			parseFileAndSaveRoutes(parser, airports);
		}
	}

	private void parseFileAndSaveRoutes(CSVParser parser, List<Airport> airports) {
		Set<Route> routes = new HashSet<>();
		for (CSVRecord csvRecord : parser) {
			String[] record = csvRecord.get(0).split(",");
			Integer sourceAirportId = converter.convertToInteger(record[3]);
			Integer destinationAirportId = converter.convertToInteger(record[5]);
			if (sourceAirportId != null && destinationAirportId != null
					&& doAirportsExist(airports, sourceAirportId, destinationAirportId)) {
				routes.add(mapToRoute(record, sourceAirportId, destinationAirportId));
			}
		}
		routeRepository.saveAll(routes);
	}

	private boolean doAirportsExist(List<Airport> airports, Integer sourceAirportId, Integer destinationAirportId) {
		return airportWithIdExists(airports, sourceAirportId) && airportWithIdExists(airports, destinationAirportId);
	}

	private boolean airportWithIdExists(List<Airport> airports, Integer airportId) {
		return airports.parallelStream().anyMatch(a -> a.getId() == airportId);
	}

	private Route mapToRoute(String[] record, Integer sourceAirportId, Integer destinationAirportId) {
		Route route = new Route();
		route.setAirline(record[0]);
		route.setAirlineId(converter.convertToInteger(record[1]));
		route.setSourceAirport(record[2]);
		route.setSourceAirportId(sourceAirportId);
		route.setDestinationAirport(record[4]);
		route.setDestinationAirportId(destinationAirportId);
		route.setCodeshare(record[6]);
		route.setStops(converter.convertToInteger(record[7]));
		route.setEquipment(record[8]);
		route.setPrice(converter.convertToDouble(record[9]));
		return route;
	}
}
