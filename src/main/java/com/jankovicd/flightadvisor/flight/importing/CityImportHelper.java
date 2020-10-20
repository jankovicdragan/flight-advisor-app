package com.jankovicd.flightadvisor.flight.importing;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.jankovicd.flightadvisor.city.model.City;
import com.jankovicd.flightadvisor.city.repository.CityRepository;
import com.jankovicd.flightadvisor.exception.FlightAdvisorException;
import com.jankovicd.flightadvisor.flight.util.TypeConverter;
import com.jankovicd.flightadvisor.util.Bundle;

@Component
public class CityImportHelper {

	private final CityRepository cityRepository;
	private final TypeConverter converter;
	
	@Value("${import.default.airport}")
	private String defaultFilePath;

	public CityImportHelper(CityRepository cityRepository, TypeConverter converter) {
		this.cityRepository = cityRepository;
		this.converter = converter;
	}

	public void importCities() {
		try {
			Reader reader = Files.newBufferedReader(Paths.get(defaultFilePath));
			CSVParser parser = CSVParser.parse(reader, CSVFormat.INFORMIX_UNLOAD.withNullString("\\N"));
			parseFileAndSaveAirports(parser);
		} catch (IOException e) {
			throw new FlightAdvisorException(
					MessageFormat.format(Bundle.getBundleMessage("file.parsing.failed"), defaultFilePath));
		}
	}

	private void parseFileAndSaveAirports(CSVParser parser) {
		List<City> cities = new ArrayList<>();
		for (CSVRecord csvRecord : parser) {
			String[] record = csvRecord.get(0).split(",");
			String city = converter.convertToString(record[2]);
			String country = converter.convertToString(record[3]);
			if (city != null && country != null && !doesCityExist(cities, city, country)) {
				cities.add(mapToCity(city, country));
			}
		}
		cityRepository.saveAll(cities);
	}

	private City mapToCity(String name, String country) {
		City city = new City();
		city.setName(name);
		city.setCountry(country);
		city.setDescription("City description for: " + converter.convertToString(name));
		return city;
	}

	private boolean doesCityExist(List<City> cities, String city, String country) {
		return cities.parallelStream().anyMatch(c -> c.getName().equals(city) && c.getCountry().equals(country));
	}

}
