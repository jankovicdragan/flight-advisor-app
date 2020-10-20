package com.jankovicd.flightadvisor.flight.dto;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FlightDTO {

	private List<RouteDTO> routes;
	private double length;
	private double totalPrice;

}
