package com.jankovicd.flightadvisor.flight.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jankovicd.flightadvisor.flight.model.Route;

public interface RouteRepository extends JpaRepository<Route, Integer> {

}
