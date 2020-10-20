package com.jankovicd.flightadvisor.city.dto;

import javax.validation.constraints.NotBlank;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CityDTO {

	@NotBlank(message = "{city.required.name}")
	private String name;
	@NotBlank(message = "{city.required.country}")
	private String country;
	@NotBlank(message = "{city.required.description}")
	private String description;

}
