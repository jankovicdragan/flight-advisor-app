package com.jankovicd.flightadvisor.city.model;

import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class Comment {

	@Id
	@GeneratedValue
	private long id;
	
	private String text;
	
	@JsonIgnore
	@ManyToOne
	@JoinColumn(name = "city_id", nullable = false)
	private City city;
	
	private LocalDateTime created;
	
	private LocalDateTime modified;

}
