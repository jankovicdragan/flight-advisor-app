package com.jankovicd.flightadvisor.flight.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import org.apache.lucene.util.SloppyMath;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.jankovicd.flightadvisor.city.model.City;
import com.jankovicd.flightadvisor.city.service.CityService;
import com.jankovicd.flightadvisor.flight.dto.FlightDTO;
import com.jankovicd.flightadvisor.flight.dto.RouteDTO;
import com.jankovicd.flightadvisor.flight.model.Airport;
import com.jankovicd.flightadvisor.flight.model.Route;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Service
@Scope(value = "prototype")
public class FlightService {

	private final AirportService airportService;
	private final RouteService routeService;
	private final CityService cityService;

	List<Route> allRoutes = new ArrayList<>();
	List<Airport> allAirports = new ArrayList<>();
	Queue<Destination> queue;
	Set<Destination> visited;
	Set<Flight> flights;
	Set<Destination> destinations;
	Double cheapestPrice = null;

	public FlightService(AirportService airportService, RouteService routeService, CityService cityService) {
		this.airportService = airportService;
		this.cityService = cityService;
		this.routeService = routeService;
	}

	public FlightDTO findCheapestRoute(Long sourceCityId, Long destinationCityId) {
		allRoutes = routeService.findAll();
		flights = getAllFlights();
		allAirports = airportService.findAll();
		List<Airport> sourceAirports = getCityAirports(sourceCityId);
		List<Airport> destinationAirports = getCityAirports(destinationCityId);
		List<Destination> cheapestRoute = new ArrayList<>();
		for (Airport source : sourceAirports) {
			for (Airport destination : destinationAirports) {
				List<Destination> candidate = getFlightFromSourceToDestination(source.getId(), destination.getId());
				cheapestRoute = findCheaperRoute(cheapestRoute, candidate);
			}
		}
		return createResponse(cheapestRoute);
	}

	private List<Destination> findCheaperRoute(List<Destination> cheapestRoute, List<Destination> candidate) {
		if (!candidate.isEmpty()) {
			Double candidatePrice = getTotalPrice(candidate);
			if (cheapestPrice == null || candidatePrice.compareTo(cheapestPrice) < 0) {
				cheapestRoute = candidate;
				cheapestPrice = candidatePrice;
			}
		}
		return cheapestRoute;
	}

	private List<Airport> getCityAirports(Long cityId) {
		City city = cityService.findById(cityId);
		return allAirports.stream()
				.filter(a -> a.getCity().equals(city.getName()) && a.getCountry().equals(city.getCountry()))
				.collect(Collectors.toList());
	}

	private FlightDTO createResponse(List<Destination> cheapestRoute) {
		FlightDTO flight = new FlightDTO();
		if (cheapestRoute.size() > 0) {
			List<RouteDTO> routes = cheapestRoute.stream().map(route -> mapRouteToDTO(route))
					.collect(Collectors.toList());
			flight.setRoutes(routes);
			flight.setTotalPrice(getTotalPrice(cheapestRoute));
			flight.setLength(calculateTotalRouteLength(routes));
		}
		return flight;
	}

	private Double getTotalPrice(List<Destination> destinationList) {
		return destinationList.get(destinationList.size() - 1).getTotalPrice();
	}

	private double calculateTotalRouteLength(List<RouteDTO> routes) {
		return routes.stream().mapToDouble(route -> route.getLength()).sum();
	}

	private RouteDTO mapRouteToDTO(Destination destination) {
		Optional<Airport> sourceOptional = findAirportById(destination.getPrevious());
		Optional<Airport> destinationOptional = findAirportById(destination.getId());
		Route route = findRouteBySourceAndDestination(destination.getPrevious(), destination.getId());
		RouteDTO routeDTO = new RouteDTO();
		if (sourceOptional.isPresent() && destinationOptional.isPresent()) {
			Airport sourceAirport = sourceOptional.get();
			Airport destinationAirport = destinationOptional.get();
			routeDTO.setSourceCity(sourceAirport.getCity());
			routeDTO.setDestinationCity(destinationAirport.getCity());
			routeDTO.setPrice(route.getPrice());
			double length = SloppyMath.haversinMeters(sourceAirport.getLatitude(), sourceAirport.getLongitude(),
					destinationAirport.getLatitude(), destinationAirport.getLongitude());
			routeDTO.setLength(length);
		}
		return routeDTO;
	}

	private Route findRouteBySourceAndDestination(Integer previous, Integer id) {
		List<Route> routes = allRoutes.stream()
				.filter(r -> r.getSourceAirportId().equals(previous) && r.getDestinationAirportId().equals(id))
				.collect(Collectors.toList());
		if (routes.size() == 1) {
			return routes.get(0);
		}
		Route cheapestRoute = null;
		for (Route route : routes) {
			if (cheapestRoute == null || route.getPrice().compareTo(cheapestRoute.getPrice()) < 0) {
				cheapestRoute = route;
			}
		}
		return cheapestRoute;
	}

	private Optional<Airport> findAirportById(int id) {
		return allAirports.stream().filter(a -> a.getId() == id).findFirst();
	}

	private List<Destination> getFlightFromSourceToDestination(int sourceId, int destinationId) {
		initializeCollectionsAndSetStartingPoint(sourceId);
		findCheapestFlight(sourceId, destinationId);
		return getFlighRoutes(sourceId, destinationId);
	}

	private void initializeCollectionsAndSetStartingPoint(int source) {
		destinations = getAllDestinations(allAirports, source);
		visited = new HashSet<>();
		queue = new PriorityQueue<>((a, b) -> a.totalPrice.compareTo(b.totalPrice));
		queue.add(destinations.stream().filter(f -> f.totalPrice != null).findFirst().get());
	}

	private Set<Flight> getAllFlights() {
		return allRoutes.stream()
				.map(r -> new Flight(r.getSourceAirportId(), r.getDestinationAirportId(), r.getPrice()))
				.collect(Collectors.toSet());
	}

	private Set<Destination> getAllDestinations(List<Airport> airports, int source) {
		return airports.stream().map(a -> new Destination(null, a.getId(), a.getId() == source ? 0d : null))
				.collect(Collectors.toSet());
	}

	private void findCheapestFlight(int source, int destination) {
		Double currentCheapest = 0d;
		while (!queue.isEmpty()) {
			Destination currentDestination = pollCheapestFromQueue();
			currentCheapest += currentDestination.totalPrice;
			if (cheapestPrice == null || currentCheapest.compareTo(cheapestPrice) < 0) {
				visited.add(currentDestination);
				if (!currentDestination.id.equals(destination)) {
					removeFromDestinations(currentDestination);
					visitAllDirectDestinations(currentDestination);
				} else {
					queue.clear();
				}
			} else {
				visited.clear();
				queue.clear();
			}
		}
	}

	private Destination pollCheapestFromQueue() {
		Destination cheapestDestination = null;
		for (Destination destination : queue) {
			if (cheapestDestination == null || destination.totalPrice.compareTo(cheapestDestination.totalPrice) < 0) {
				cheapestDestination = destination;
			}
		}
		queue.remove(cheapestDestination);
		return cheapestDestination;
	}

	private void removeFromDestinations(Destination destination) {
		destinations.removeIf(f -> f.id == destination.id);
	}

	private void visitAllDirectDestinations(Destination currentDestination) {
		Set<Flight> neighbors = getAllDirectFlights(currentDestination);
		for (Flight neighbor : neighbors) {
			for (Destination destination : destinations) {
				if (destination.id.equals(neighbor.destination) && !visitedDestination(destination)) {
					updateDestination(currentDestination, neighbor, destination);
					addOrReplaceInQueue(destination);
				}
			}
		}
	}

	private Set<Flight> getAllDirectFlights(Destination destination) {
		Set<Flight> neighbors = new TreeSet<>((a, b) -> a.compareTo(b));
		neighbors.addAll(flights.stream().filter(r -> r.source.equals(destination.id)).collect(Collectors.toSet()));
		return neighbors;
	}

	private boolean visitedDestination(Destination destination) {
		return visited.stream().anyMatch(f -> f.id.equals(destination.id));
	}

	private void updateDestination(Destination currentDestination, Flight neighbor, Destination destination) {
		Double currentPrice = currentDestination.totalPrice;
		Double priceToNeighbor = neighbor.getPrice();
		Double currentPriceToNeighbor = destination.getTotalPrice();
		if (currentPriceToNeighbor == null || (currentPrice + priceToNeighbor) < currentPriceToNeighbor) {
			destination.totalPrice = currentPrice + priceToNeighbor;
			destination.previous = currentDestination.id;
		}
	}

	private void addOrReplaceInQueue(Destination destination) {
		Optional<Destination> currentDestination = queue.stream().filter(f -> f.id == destination.id).findFirst();
		if (currentDestination.isPresent()) {
			Destination destinationFromQueue = currentDestination.get();
			if (destination.totalPrice.compareTo(destinationFromQueue.totalPrice) < 0) {
				queue.remove(destinationFromQueue);
				queue.add(destination);
			}
		} else {
			queue.add(destination);
		}
	}

	private List<Destination> getFlighRoutes(int sourceId, int destinationId) {
		List<Destination> result = new ArrayList<>();
		Optional<Destination> airportWrap = visited.stream().filter(f -> f.id == destinationId).findFirst();
		if (airportWrap.isPresent()) {
			Destination airport = airportWrap.get();
			while (airport.id != sourceId) {
				result.add(airport);
				airport = findNextStop(result, airport);
			}
		}
		Collections.reverse(result);
		return result;
	}

	private Destination findNextStop(List<Destination> result, Destination airport) {
		Integer airportId = airport.previous;
		Optional<Destination> airportWrap = visited.stream().filter(f -> f.id == airportId).findFirst();
		if (airportWrap.isPresent()) {
			airport = airportWrap.get();
		}
		return airport;
	}

}

@Getter
@AllArgsConstructor
class Destination {
	Integer previous;
	Integer id;
	Double totalPrice;

}

@Getter
@AllArgsConstructor
class Flight implements Comparable<Flight> {
	Integer source;
	Integer destination;
	Double price;

	@Override
	public int compareTo(Flight o) {
		int compared = this.price.compareTo(o.price);
		return compared == 0 ? -1 : compared;
	}
}
