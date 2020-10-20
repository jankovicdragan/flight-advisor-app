package com.jankovicd.flightadvisor.flight.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.jankovicd.flightadvisor.flight.importing.FileImporter;
import com.jankovicd.flightadvisor.flight.importing.RouteFileImporter;
import com.jankovicd.flightadvisor.flight.model.Route;
import com.jankovicd.flightadvisor.flight.repository.RouteRepository;

@Service
public class RouteService {

	private final RouteRepository routeRepository;
	private final FileImporter fileImporter;

	public RouteService(RouteRepository routeRepository, RouteFileImporter routeFileImporter) {
		this.routeRepository = routeRepository;
		this.fileImporter = routeFileImporter;
	}

	public List<Route> findAll() {
		return routeRepository.findAll();
	}

	public void importFromFile() {
		routeRepository.deleteAll();
		fileImporter.importDefaultFile();
	}

	public void importFromFile(MultipartFile file) {
		routeRepository.deleteAll();
		fileImporter.importFile(file);
	}

}
