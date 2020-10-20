package com.jankovicd.flightadvisor.flight.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jankovicd.flightadvisor.flight.model.Airport;

public interface AirportRepository extends JpaRepository<Airport, Integer> {

}
