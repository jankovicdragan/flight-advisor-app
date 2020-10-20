package com.jankovicd.flightadvisor.flight.importing;

import org.springframework.web.multipart.MultipartFile;

public interface FileImporter {

	public static final String NULL_CHARACTER = "\\N";

	public void importDefaultFile();

	public void importFile(MultipartFile file);

}
