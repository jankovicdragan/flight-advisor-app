package com.jankovicd.flightadvisor.city.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CommentCreateDTO {

	@NotNull(message = "{comment.required.city}")
	private Long cityId;
	@NotBlank(message = "{comment.required.text}")
	private String text;

}
