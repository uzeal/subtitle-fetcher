package com.uzeal.subtitles.db.dto;

import com.uzeal.db.dao.DTO;
import com.uzeal.db.dao.Key;

public class Show implements DTO{
	@Key(order=1)
	private String name;
	private boolean movie;
	
	public Show() { }

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isMovie() {
		return movie;
	}

	public void setMovie(boolean movie) {
		this.movie = movie;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Show [name=").append(name).append(", movie=").append(movie).append("]");
		return builder.toString();
	}
	
}
