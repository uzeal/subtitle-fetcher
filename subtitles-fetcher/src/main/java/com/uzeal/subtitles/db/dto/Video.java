package com.uzeal.subtitles.db.dto;

import com.uzeal.db.dao.DTO;
import com.uzeal.db.dao.Key;

public class Video implements DTO {
	@Key(order=1)
	private String showName;
	@Key(order=2)
	private String name;
	private String path;
	private long hash;
	private boolean subtitles;
	
	public Video() {
		
	}

	public String getshowName() {
		return showName;
	}

	public void setshowName(String showName) {
		this.showName = showName;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public long getHash() {
		return hash;
	}

	public void setHash(long hash) {
		this.hash = hash;
	}

	public boolean isSubtitles() {
		return subtitles;
	}

	public void setSubtitles(boolean subtitles) {
		this.subtitles = subtitles;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Video [showName=").append(showName).append(", name=").append(name).append(", path=").append(path)
				.append(", hash=").append(hash).append(", subtitles=").append(subtitles).append("]");
		return builder.toString();
	}
	
}
