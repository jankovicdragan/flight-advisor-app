package com.jankovicd.flightadvisor.flight.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RouteDTO {

	private String sourceCity;
	private String destinationCity;
	private double price;
	private double length;

}
