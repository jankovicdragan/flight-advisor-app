package com.jankovicd.flightadvisor.city.dto;

import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CityPreviewDTO {
	
	private long id;
	private String name;
	private String country;
	private String description;
	private List<CommentPreviewDTO> comments;
	
}
