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

import com.jankovicd.flightadvisor.city.model.City;
import com.jankovicd.flightadvisor.city.repository.CityRepository;
import com.jankovicd.flightadvisor.exception.FlightAdvisorException;
import com.jankovicd.flightadvisor.flight.model.Airport;
import com.jankovicd.flightadvisor.flight.repository.AirportRepository;
import com.jankovicd.flightadvisor.flight.util.TypeConverter;
import com.jankovicd.flightadvisor.util.Bundle;

@Component
public class AirportFileImporter implements FileImporter {
	
	private final CityRepository cityRepository;
	private final AirportRepository airportRepository;
	private final TypeConverter converter;
	
	@Value("${import.default.airport}")
	private String defaultFilePath;

	public AirportFileImporter(CityRepository cityRepository, AirportRepository airportRepository,
			TypeConverter converter) {
		this.cityRepository = cityRepository;
		this.airportRepository = airportRepository;
		this.converter = converter;
	}

	public void importDefaultFile() {
		try {
			importFromReader(Files.newBufferedReader(Paths.get(defaultFilePath)));
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
		List<City> cities = cityRepository.findAll();
		if (cities != null && !cities.isEmpty()) {
			parseFileAndSaveAirports(parser, cities);
		}
	}

	private void parseFileAndSaveAirports(CSVParser parser, List<City> cities) {
		Set<Airport> airports = new HashSet<>();
		for (CSVRecord csvRecord : parser) {
			String[] record = csvRecord.get(0).split(",");
			String city = converter.convertToString(record[2]);
			String country = converter.convertToString(record[3]);
			if (city != null && country != null && doesCityExist(cities, city, country)) {
				airports.add(mapToAirport(record, city, country));
			}
		}
		airportRepository.saveAll(airports);
	}

	private boolean doesCityExist(List<City> cities, String city, String country) {
		return cities.parallelStream().anyMatch(c -> c.getName().equals(city) && c.getCountry().equals(country));
	}

	private Airport mapToAirport(String[] record, String city, String country) {
		Airport airport = new Airport();
		airport.setId(converter.convertToInteger(record[0]));
		airport.setName(converter.convertToString(record[1]));
		airport.setCity(city);
		airport.setCountry(country);
		airport.setIata(converter.convertToString(record[4]));
		airport.setIcao(converter.convertToString(record[5]));
		airport.setLatitude(converter.convertToDouble(record[6]));
		airport.setLongitude(converter.convertToDouble(record[7]));
		airport.setAltitude(converter.convertToDouble(record[8]));
		airport.setTimeZone(converter.convertToFloat(record[9]));
		airport.setDst(converter.convertToString(record[10]));
		airport.setTzDatabaseTimeZone(converter.convertToString(record[11]));
		airport.setType(converter.convertToString(record[12]));
		airport.setSource(converter.convertToString(record[13]));
		return airport;
	}
}
