package com.jankovicd.flightadvisor.city.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class City {

	@Id
	@GeneratedValue
	private long id;

	@Column(nullable = false)
	private String name;

	@Column(nullable = false)
	private String country;

	@Column(nullable = false)
	private String description;

	@OrderBy("COALESCE(modified, created) desc")
	@OneToMany(mappedBy = "city", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Comment> comments;

	public void addComment(Comment comment) {
		if (comments == null) {
			comments = new ArrayList<>();
		}
		comments.add(comment);
		comment.setCity(this);
	}

	public void removeComment(Comment comment) {
		if (comments == null) {
			return;
		}
		comments.remove(comment);
		comment.setCity(null);
	}

}
