package com.jankovicd.flightadvisor.city.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.jankovicd.flightadvisor.city.model.City;
import com.jankovicd.flightadvisor.city.model.Comment;

public interface CommentRepository extends JpaRepository<Comment, Long> {

	@Query("SELECT c FROM Comment c WHERE c.city = :city ORDER BY COALESCE(c.modified, c.created) DESC")
	List<Comment> findByCity(City city, Pageable pageable);

}
