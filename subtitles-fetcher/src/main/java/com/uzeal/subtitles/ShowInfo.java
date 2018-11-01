package com.uzeal.subtitles;

public class ShowInfo {
	private boolean movie;
	private String showName;
	private Integer season;
	private String episodeName;
	private Integer episode;
	private String year;
	
	public ShowInfo() {
		super();
	}

	public ShowInfo(boolean movie, String showName, Integer season, String episodeName, Integer episode, String year) {
		super();
		this.movie = movie;
		this.showName = showName;
		this.season = season;
		this.episodeName = episodeName;
		this.episode = episode;
		this.year = year;
	}

	public boolean isMovie() {
		return movie;
	}

	public void setMovie(boolean movie) {
		this.movie = movie;
	}

	public String getShowName() {
		return showName;
	}

	public void setShowName(String showName) {
		this.showName = showName;
	}

	public Integer getSeason() {
		return season;
	}

	public void setSeason(Integer season) {
		this.season = season;
	}

	public String getEpisodeName() {
		return episodeName;
	}

	public void setEpisodeName(String episodeName) {
		this.episodeName = episodeName;
	}

	public Integer getEpisode() {
		return episode;
	}

	public void setEpisode(Integer episode) {
		this.episode = episode;
	}

	public String getYear() {
		return year;
	}

	public void setYear(String year) {
		this.year = year;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ShowInfo [movie=").append(movie).append(", showName=").append(showName).append(", season=")
				.append(season).append(", episodeName=").append(episodeName).append(", episode=").append(episode)
				.append(", year=").append(year).append("]");
		return builder.toString();
	}
	
}
