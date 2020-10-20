
package com.jankovicd.flightadvisor.flight.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class Route {

	@Id
	@GeneratedValue
	private int id;
	private String airline;
	private Integer airlineId;
	private String sourceAirport;
	private Integer sourceAirportId;
	private String destinationAirport;
	private Integer destinationAirportId;
	private String codeshare;
	private Integer stops;
	private String equipment;
	private Double price;

}
