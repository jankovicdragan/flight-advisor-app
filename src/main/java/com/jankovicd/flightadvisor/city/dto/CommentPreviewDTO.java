package com.jankovicd.flightadvisor.city.dto;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CommentPreviewDTO {

	private long id;
	private String text;
	private LocalDateTime created;
	private LocalDateTime modified;

}
