package com.jankovicd.flightadvisor.flight.model;

import javax.persistence.Entity;
import javax.persistence.Id;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class Airport {

	@Id
	private int id;
	private String name;
	private String city;
	private String country;
	private String iata;
	private String icao;
	private Double latitude;
	private Double longitude;
	private Double altitude;
	private Float timeZone;
	private String dst;
	private String tzDatabaseTimeZone;
	private String type;
	private String source;

}
