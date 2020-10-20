package com.jankovicd.flightadvisor.city.dto;

import javax.validation.constraints.NotBlank;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CommentUpdateDTO {

	@NotBlank(message = "{comment.required.text}")
	private String text;

}
