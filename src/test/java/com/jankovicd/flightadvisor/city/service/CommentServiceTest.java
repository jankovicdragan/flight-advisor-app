package com.jankovicd.flightadvisor.city.service;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.jankovicd.flightadvisor.city.dto.CommentCreateDTO;
import com.jankovicd.flightadvisor.city.dto.CommentUpdateDTO;
import com.jankovicd.flightadvisor.city.model.Comment;
import com.jankovicd.flightadvisor.city.repository.CommentRepository;
import com.jankovicd.flightadvisor.exception.FlightAdvisorException;

@SpringBootTest(classes = CommentService.class)
@ExtendWith(SpringExtension.class)
public class CommentServiceTest {

	private static final String COMMENT = "Comment";

	@Autowired
	CommentService commentService;

	@MockBean
	CityService cityService;

	@MockBean
	CommentRepository commentRepository;

	@Test
	public void addComment_cityServiceCalled() {
		CommentCreateDTO commentDTO = new CommentCreateDTO();
		commentDTO.setCityId(1l);
		commentDTO.setText(COMMENT);

		commentService.addComment(commentDTO);
		verify(cityService, times(1)).addComment(Mockito.anyLong(), Mockito.any(Comment.class));
	}

	@Test
	public void updateComment_commentDoesntExist_flightAdvisorException() {
		Long id = 1l;
		when(commentRepository.findById(id)).thenReturn(Optional.ofNullable(null));
		CommentUpdateDTO commentDTO = new CommentUpdateDTO();
		commentDTO.setText(COMMENT);

		assertThrows(FlightAdvisorException.class, () -> commentService.updateComment(id, commentDTO));
	}

	@Test
	public void updateComment_commentExists_repositoryMethodIsCalled() {
		Long id = 1l;
		when(commentRepository.findById(id)).thenReturn(Optional.ofNullable(new Comment()));
		CommentUpdateDTO commentDTO = new CommentUpdateDTO();
		commentDTO.setText(COMMENT);

		commentService.updateComment(id, commentDTO);
		verify(commentRepository, times(1)).save(Mockito.any(Comment.class));
	}

	@Test
	public void deleteComment_repositoryMethodIsCalled() {
		commentService.deleteComment(1l);
		verify(commentRepository, times(1)).deleteById(Mockito.anyLong());
	}
}
