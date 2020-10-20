package com.jankovicd.flightadvisor.city.service;

import java.text.MessageFormat;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.springframework.stereotype.Service;

import com.jankovicd.flightadvisor.city.dto.CityDTO;
import com.jankovicd.flightadvisor.city.dto.CityPreviewDTO;
import com.jankovicd.flightadvisor.city.dto.CommentPreviewDTO;
import com.jankovicd.flightadvisor.city.model.City;
import com.jankovicd.flightadvisor.city.model.Comment;
import com.jankovicd.flightadvisor.city.repository.CityRepository;
import com.jankovicd.flightadvisor.exception.FlightAdvisorException;
import com.jankovicd.flightadvisor.util.Bundle;

@Service
public class CityService {

	private final CityRepository cityRepository;

	public CityService(CityRepository cityRepository) {
		this.cityRepository = cityRepository;
	}

	
	public void addCity(@Valid CityDTO cityDTO) {
		City city = new City();
		city.setName(cityDTO.getName());
		city.setCountry(cityDTO.getCountry());
		city.setDescription(cityDTO.getDescription());
		cityRepository.save(city);
	}

	public void addComment(Long cityId, Comment comment) {
		City city = findById(cityId);
		city.addComment(comment);
		cityRepository.save(city);
	}

	public List<CityPreviewDTO> getCities(String name, Integer commentCount) {
		if (name != null) {
			return getAllCitiesWithName(name, commentCount);
		} else {
			return getAllCities(commentCount);
		}
	}

	private List<CityPreviewDTO> getAllCitiesWithName(String name, Integer commentCount) {
		return mapCitiesToDTO(commentCount, cityRepository.findByName(name));
	}

	public List<CityPreviewDTO> getAllCities(Integer commentCount) {
		List<City> cities = findAllCities();
		return mapCitiesToDTO(commentCount, cities);
	}

	private List<City> findAllCities() {
		return cityRepository.findAll();
	}

	private List<CityPreviewDTO> mapCitiesToDTO(Integer commentCount, List<City> cities) {
		List<CityPreviewDTO> citiesDTO = null;
		if (cities != null) {
			citiesDTO = cities.parallelStream().map(city -> convertCityToDTO(city, commentCount))
					.collect(Collectors.toList());
		}
		return citiesDTO;
	}

	private CityPreviewDTO convertCityToDTO(City city, Integer commentCount) {
		CityPreviewDTO cityDTO = new CityPreviewDTO();
		cityDTO.setId(city.getId());
		cityDTO.setName(city.getName());
		cityDTO.setCountry(city.getCountry());
		cityDTO.setDescription(city.getDescription());
		if (city.getComments() != null) {
			int limit = commentCount != null ? commentCount : city.getComments().size();
			List<CommentPreviewDTO> comments = city.getComments().parallelStream()
					.map(comment -> convertCommentToDTO(comment)).limit(limit).collect(Collectors.toList());
			cityDTO.setComments(comments);
		}
		return cityDTO;
	}

	private CommentPreviewDTO convertCommentToDTO(Comment comment) {
		CommentPreviewDTO commentDTO = new CommentPreviewDTO();
		commentDTO.setId(comment.getId());
		commentDTO.setText(comment.getText());
		commentDTO.setCreated(comment.getCreated());
		commentDTO.setModified(comment.getModified());
		return commentDTO;
	}

	public City findById(Long id) {
		Optional<City> city = cityRepository.findById(id);
		if (city.isPresent()) {
			return city.get();
		}
		throw new FlightAdvisorException(MessageFormat.format(Bundle.getBundleMessage("city.id.invalid"), id));
	}

}
