package com.jankovicd.flightadvisor.city.controller;

import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jankovicd.flightadvisor.city.dto.CommentCreateDTO;
import com.jankovicd.flightadvisor.city.dto.CommentUpdateDTO;
import com.jankovicd.flightadvisor.city.service.CommentService;
import com.jankovicd.flightadvisor.util.FlightAdvisorController;

@RestController
@RequestMapping("/city/comment")
public class CommentController extends FlightAdvisorController {

	private final CommentService commentService;

	public CommentController(CommentService commentService) {
		super();
		this.commentService = commentService;
	}

	@PreAuthorize("hasAuthority('REGULAR')")
	@PostMapping
	public ResponseEntity<Void> add(@RequestBody @Valid CommentCreateDTO commentDTO, Errors errors) {
		checkForErrors(errors);
		commentService.addComment(commentDTO);
		return new ResponseEntity<>(HttpStatus.CREATED);
	}

	@PreAuthorize("hasAuthority('REGULAR')")
	@PutMapping("{id}")
	public ResponseEntity<Void> update(@PathVariable("id") Long id, @RequestBody @Valid CommentUpdateDTO commentDTO,
			Errors errors) {
		checkForErrors(errors);
		commentService.updateComment(id, commentDTO);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}

	@PreAuthorize("hasAuthority('REGULAR')")
	@DeleteMapping("{id}")
	public ResponseEntity<Void> delete(@PathVariable("id") Long id) {
		commentService.deleteComment(id);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
}
