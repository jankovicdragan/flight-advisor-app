package com.jankovicd.flightadvisor.flight.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.jankovicd.flightadvisor.city.model.City;
import com.jankovicd.flightadvisor.city.service.CityService;
import com.jankovicd.flightadvisor.flight.dto.FlightDTO;
import com.jankovicd.flightadvisor.flight.model.Airport;
import com.jankovicd.flightadvisor.flight.model.Route;

@SpringBootTest(classes = FlightService.class)
@ExtendWith(SpringExtension.class)
public class FlightServiceTest {

	@Autowired
	FlightService flightService;

	@MockBean
	AirportService airportService;
	@MockBean
	RouteService routeService;
	@MockBean
	CityService cityService;

	List<City> cities = new ArrayList<>();
	List<Airport> airports = new ArrayList<>();
	List<Route> routes = new ArrayList<>();

	private void mockEverything() {
		when(routeService.findAll()).thenReturn(routes);
		Mockito.when(airportService.findAll()).thenReturn(airports);
		Mockito.when(cityService.findById(Mockito.anyLong())).thenAnswer(new Answer<City>() {
			public City answer(InvocationOnMock invocation) {
				Object[] args = invocation.getArguments();
				return cities.stream().filter(c -> ((Long) args[0]).equals(c.getId())).findFirst().get();
			}
		});
	}

	@Test
	public void findCheapestRoute_oneAirportPerCity_from1to6_foundCheapestRoute() {
		prepareWithOnlyOneAirportPerCity();
		prepareRoutes();
		flightService.findCheapestRoute(1l, 6l);
	}

	@Test
	public void findCheapestRoute_cheapestRouteWith1Stop_routeFoundWithPrice2And1Stops() {
		prepareWithOnlyOneAirportPerCity();
		prepareRoutes();
		mockEverything();
		FlightDTO flight = flightService.findCheapestRoute(1l, 6l);
		assertThat(flight).isNotNull();
		assertThat(flight.getRoutes()).isNotEmpty().hasSize(2);
		assertThat(flight.getRoutes().get(0).getDestinationCity()).isEqualTo("Madrid");
		assertThat(flight.getTotalPrice()).isNotNull().isEqualTo(2d);
	}

	@Test
	public void findCheapestRoute_cheapestRouteWith4Stops_routeFoundWithPrice5And4Stops() {
		prepareWithOnlyOneAirportPerCity();
		prepareRoutes();
		Route madridToLondon = routes.stream().filter(a -> a.getSourceAirportId().equals(7)).findFirst().get();
		routes.remove(madridToLondon);
		mockEverything();
		FlightDTO flight = flightService.findCheapestRoute(1l, 6l);
		assertThat(flight).isNotNull();
		assertThat(flight.getRoutes()).isNotEmpty().hasSize(5);
		assertThat(flight.getTotalPrice()).isNotNull().isEqualTo(5d);
		assertThat(flight.getRoutes().get(0).getDestinationCity()).isEqualTo("Berlin");
		assertThat(flight.getRoutes().get(1).getDestinationCity()).isEqualTo("Frankfurt");
		assertThat(flight.getRoutes().get(2).getDestinationCity()).isEqualTo("Prague");
		assertThat(flight.getRoutes().get(3).getDestinationCity()).isEqualTo("Amsterdam");
		routes.add(madridToLondon);
	}

	@Test
	public void findCheapestRoute_routeNotExisting_emptyRouteList() {
		prepareWithOnlyOneAirportPerCity();
		prepareRoutes();
		Route parisToMadrid = routes.stream().filter(a -> a.getDestinationAirportId().equals(7)).findFirst().get();
		routes.remove(parisToMadrid);
		mockEverything();
		FlightDTO flight = flightService.findCheapestRoute(1l, 7l);
		assertThat(flight).isNotNull();
		assertThat(flight.getRoutes()).isNullOrEmpty();
	}

	@Test
	public void findCheapestRoute_cheapestRouteWith4Stops_doubleRouteSkipped_routeFoundWithPrice5And4Stops() {
		prepareWithOnlyOneAirportPerCity();
		prepareRoutes();
		Route parisToMadrid = routes.stream().filter(a -> a.getDestinationAirportId().equals(7)).findFirst().get();
		routes.remove(parisToMadrid);
		createRoute(55, 2, 3, 1);
		mockEverything();
		FlightDTO flight = flightService.findCheapestRoute(1l, 6l);
		assertThat(flight).isNotNull();
		assertThat(flight.getRoutes()).isNotEmpty().hasSize(5);
		assertThat(flight.getTotalPrice()).isNotNull().isEqualTo(5d);
		assertThat(flight.getRoutes().get(0).getDestinationCity()).isEqualTo("Berlin");
		assertThat(flight.getRoutes().get(1).getDestinationCity()).isEqualTo("Frankfurt");
		assertThat(flight.getRoutes().get(2).getDestinationCity()).isEqualTo("Prague");
		assertThat(flight.getRoutes().get(3).getDestinationCity()).isEqualTo("Amsterdam");
	}

	@Test
	public void findCheapestRoute_cheapestRouteWith4Stops_circularRouteBetween1and2Skipped_routeFoundWithPrice5And4Stops() {
		prepareWithOnlyOneAirportPerCity();
		prepareRoutes();
		createRoute(12, 2, 1, 5);
		Route parisToMadrid = routes.stream().filter(a -> a.getDestinationAirportId().equals(7)).findFirst().get();
		routes.remove(parisToMadrid);
		mockEverything();
		FlightDTO flight = flightService.findCheapestRoute(1l, 6l);
		assertThat(flight).isNotNull();
		assertThat(flight.getRoutes()).isNotEmpty().hasSize(5);
		assertThat(flight.getTotalPrice()).isNotNull().isEqualTo(5d);
		assertThat(flight.getRoutes().get(0).getDestinationCity()).isEqualTo("Berlin");
		assertThat(flight.getRoutes().get(1).getDestinationCity()).isEqualTo("Frankfurt");
		assertThat(flight.getRoutes().get(2).getDestinationCity()).isEqualTo("Prague");
		assertThat(flight.getRoutes().get(3).getDestinationCity()).isEqualTo("Amsterdam");
	}

	private void prepareRoutes() {
		routes.clear();
		createRoute(1, 1, 2, 1d);
		createRoute(2, 1, 5, 31d);
		createRoute(3, 1, 6, 15d);
		createRoute(4, 1, 7, 1d);
		createRoute(5, 2, 3, 1d);
		createRoute(6, 2, 4, 48d);
		createRoute(7, 2, 5, 51d);
		createRoute(8, 3, 4, 1d);
		createRoute(9, 4, 5, 1d);
		createRoute(10, 5, 6, 1d);
		createRoute(11, 7, 6, 1d);
		createRoute(12, 6, 8, 1d);
	}

	private void createRoute(int id, int sourceAirportId, int destinationAirportId, double price) {
		Route route = new Route();
		route.setId(id);
		route.setSourceAirportId(sourceAirportId);
		route.setDestinationAirportId(destinationAirportId);
		route.setPrice(price);
		routes.add(route);
	}

	private void prepareWithOnlyOneAirportPerCity() {
		cities.clear();
		airports.clear();
		City paris = createCity(1, "Paris", "France");
		createAirport(1, 48.85, 2.35, paris);
		City berlin = createCity(2, "Berlin", "Germany");
		createAirport(2, 52.52, 13.41, berlin);
		City frankfurt = createCity(3, "Frankfurt", "Germany");
		createAirport(3, 50.11, 8.68, frankfurt);
		City prague = createCity(4, "Prague", "Checz Republic");
		createAirport(4, 50.07, 14.43, prague);
		City amsterdam = createCity(5, "Amsterdam", "Netherlands");
		createAirport(5, 52.37, 4.89, amsterdam);
		City london = createCity(6, "London", "England");
		createAirport(6, 51.5, -0.12, london);
		City madrid = createCity(7, "Madrid", "Spain");
		createAirport(7, 40.41, -3.7, madrid);
		City banjaLuka = createCity(7, "Banja Luka", "BiH");
		createAirport(8, 44.77, 17.19, banjaLuka);
		cities = Arrays.asList(paris, berlin, frankfurt, prague, amsterdam, london, madrid, banjaLuka);
	}

	private City createCity(long id, String name, String country) {
		City city = new City();
		city.setId(id);
		city.setName(name);
		city.setCountry(country);
		cities.add(city);
		return city;
	}

	private void createAirport(int id, Double latitude, Double longitude, City city) {
		Airport airport = new Airport();
		airport.setCity(city.getName());
		airport.setCountry(city.getCountry());
		airport.setId(id);
		airport.setLatitude(latitude);
		airport.setLongitude(longitude);
		airports.add(airport);
	}

}
