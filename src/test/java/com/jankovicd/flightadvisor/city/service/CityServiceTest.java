package com.jankovicd.flightadvisor.city.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.jankovicd.flightadvisor.city.dto.CityDTO;
import com.jankovicd.flightadvisor.city.dto.CityPreviewDTO;
import com.jankovicd.flightadvisor.city.model.City;
import com.jankovicd.flightadvisor.city.model.Comment;
import com.jankovicd.flightadvisor.city.repository.CityRepository;
import com.jankovicd.flightadvisor.exception.FlightAdvisorException;

@SpringBootTest(classes = CityService.class)
@ExtendWith(SpringExtension.class)
public class CityServiceTest {

	private static final String COMMENT_NO = "Comment No.";
	private static final String COMMENT = "Comment";
	private static final String CITY_1 = "City1";
	private static final String CITY_2 = "City2";

	@Autowired
	CityService cityService;

	@MockBean
	CityRepository cityRepository;

	@Test
	public void addCity_addingNewCity_repositoryMethodIsCalled() {
		CityDTO cityDTO = new CityDTO();
		cityDTO.setCountry("USA");
		cityDTO.setName("California");
		cityDTO.setDescription("Prettiest city in the United States.");

		cityService.addCity(cityDTO);
		verify(cityRepository, times(1)).save(Mockito.any(City.class));
	}

	@Test
	public void addComment_cityDoesNotExist_flightAdvisorException() {
		Long cityId = 5l;
		Comment comment = createComment(COMMENT);
		when(cityRepository.findById(cityId)).thenReturn(Optional.ofNullable(null));

		assertThrows(FlightAdvisorException.class, () -> cityService.addComment(cityId, comment));
	}

	@Test
	public void addComment_cityExists_exceptionNotThrown_saveCalled() {
		Long cityId = 5l;
		Comment comment = createComment(COMMENT);
		when(cityRepository.findById(cityId)).thenReturn(Optional.of(new City()));

		cityService.addComment(cityId, comment);
		verify(cityRepository, times(1)).save(Mockito.any(City.class));
	}

	@Test
	public void getCities_noParamsPassed_citiesDontExist_emptyListReturned() {
		assertThat(cityService.getCities(null, null)).isEmpty();
	}

	@Test
	public void getCities_noParamsPassed_citiesExist_wholeListReturned() {
		List<City> cityList = Arrays.asList(createCityWithComments(null, 10), createCityWithComments(null, 2));
		when(cityRepository.findAll()).thenReturn(cityList);
		when(cityRepository.findByName(null)).thenReturn(Collections.emptyList());

		List<CityPreviewDTO> cities = cityService.getCities(null, null);
		assertThat(cities).isNotEmpty().hasSize(2);
		assertThat(cities.get(0).getComments()).hasSize(10);
		assertThat(cities.get(1).getComments()).hasSize(2);
	}

	@Test
	public void getCities_commentCountPassed_citiesExist_listReturnedWithLimitedNumberOfComments() {
		List<City> cityList = Arrays.asList(createCityWithComments(null, 10), createCityWithComments(null, 2));
		when(cityRepository.findAll()).thenReturn(cityList);
		when(cityRepository.findByName(null)).thenReturn(Collections.emptyList());

		int commentCount = 5;
		List<CityPreviewDTO> cities = cityService.getCities(null, commentCount);
		assertThat(cities).isNotEmpty().hasSize(2);
		assertThat(cities.get(0).getComments()).hasSize(commentCount);
		assertThat(cities.get(1).getComments()).hasSize(2);
	}

	@Test
	public void getCities_cityNamePassed_oneCityExistsWithSameName_oneCityReturned() {
		City city1 = createCityWithComments(CITY_1, 10);
		List<City> cityList = Arrays.asList(city1, createCityWithComments(CITY_2, 2));
		when(cityRepository.findAll()).thenReturn(cityList);
		when(cityRepository.findByName(CITY_1)).thenReturn(Arrays.asList(city1));

		List<CityPreviewDTO> cities = cityService.getCities(CITY_1, null);
		assertThat(cities).isNotEmpty().hasSize(1);
		assertThat(cities.get(0).getName()).isEqualTo(CITY_1);
		assertThat(cities.get(0).getComments()).hasSize(10);
	}

	@Test
	public void getCities_cityNameAndCommentCountPassed_oneCityExistsWithSameName_oneCityReturnedWithLimitedComments() {
		City city1 = createCityWithComments(CITY_1, 10);
		List<City> cityList = Arrays.asList(city1, createCityWithComments(CITY_2, 2));
		when(cityRepository.findAll()).thenReturn(cityList);
		when(cityRepository.findByName(CITY_1)).thenReturn(Arrays.asList(city1));
		int commentCount = 5;

		List<CityPreviewDTO> cities = cityService.getCities(CITY_1, commentCount);
		assertThat(cities).isNotEmpty().hasSize(1);
		assertThat(cities.get(0).getName()).isEqualTo(CITY_1);
		assertThat(cities.get(0).getComments()).hasSize(commentCount);
	}

	private City createCityWithComments(String name, int commentCount) {
		City city = new City();
		city.setName(name);
		for (int i = 0; i < commentCount; i++) {
			city.addComment(createComment(COMMENT_NO + (i + 1)));
		}
		return city;
	}

	private Comment createComment(String text) {
		Comment comment = new Comment();
		comment.setText(text);
		return comment;
	}

}
