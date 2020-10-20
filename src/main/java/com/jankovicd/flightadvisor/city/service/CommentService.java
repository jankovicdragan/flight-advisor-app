package com.jankovicd.flightadvisor.city.service;

import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.util.Optional;

import javax.validation.Valid;

import org.springframework.stereotype.Service;

import com.jankovicd.flightadvisor.city.dto.CommentCreateDTO;
import com.jankovicd.flightadvisor.city.dto.CommentUpdateDTO;
import com.jankovicd.flightadvisor.city.model.Comment;
import com.jankovicd.flightadvisor.city.repository.CommentRepository;
import com.jankovicd.flightadvisor.exception.FlightAdvisorException;
import com.jankovicd.flightadvisor.util.Bundle;

@Service
public class CommentService {

	private final CommentRepository commentRepository;
	private final CityService cityService;

	public CommentService(CommentRepository commentRepository, CityService cityService) {
		this.commentRepository = commentRepository;
		this.cityService = cityService;
	}

	public void addComment(@Valid CommentCreateDTO commentDTO) {
		Comment comment = new Comment();
		comment.setText(commentDTO.getText());
		comment.setCreated(LocalDateTime.now());
		cityService.addComment(commentDTO.getCityId(), comment);
	}

	public void updateComment(Long id, CommentUpdateDTO commentDTO) {
		Optional<Comment> commentObj = commentRepository.findById(id);
		if (!commentObj.isPresent()) {
			throw new FlightAdvisorException(MessageFormat.format(Bundle.getBundleMessage("comment.id.invalid"), id));
		}
		Comment comment = commentObj.get();
		comment.setText(commentDTO.getText());
		comment.setModified(LocalDateTime.now());
		commentRepository.save(comment);
	}

	public void deleteComment(Long id) {
		commentRepository.deleteById(id);
	}

}
