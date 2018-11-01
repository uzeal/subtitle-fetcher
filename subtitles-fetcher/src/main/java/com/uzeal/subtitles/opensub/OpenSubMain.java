package com.uzeal.subtitles.opensub;

import java.io.File;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;

import com.uzeal.subtitles.ShowInfo;

public class OpenSubMain {
	public static void main(String[] args) {
		OpenSubtitleAPI openSubtitle=new OpenSubtitleAPI();
	    try {
			//openSubtitle.login(args[0],args[1]);
			//System.out.println(openSubtitle.getTvSeriesSubs("True Detective", "2", "1", "100", "eng"));
			File video = new File(args[2]);
			System.out.println(getShowInfo(video));
			//openSubtitle.downloadSub(video);		
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			//openSubtitle.logOut();
		}
	}
	
	private static ShowInfo getShowInfo(File video) {
		ShowInfo show = new ShowInfo();
		File parent = video.getParentFile();
		if(parent.getName().startsWith("Season ")) {
			show.setMovie(false);
			show.setSeason(Integer.valueOf(parent.getName().substring(7)));
			show.setShowName(sanitizeName(parent.getParentFile().getName()));
			getEpisode(video, show);
		} else {
			getMovieInfo(video, show);
		}
		return show;
	}
	
	private static void getMovieInfo(File video, ShowInfo show) {
		show.setMovie(true);
		String movieName = video.getParentFile().getName();
		int index = movieName.indexOf('(');
		if(index > 0) {
			show.setYear(movieName.substring(index+1, movieName.indexOf(')')));
			show.setShowName(sanitizeName(movieName.substring(0,index)));
		} else {
			show.setShowName(sanitizeName(movieName));
		}
	}
	
	private static void getEpisode(File video, ShowInfo show) {	
		int index = video.getName().indexOf(buildSXXE(show.getSeason()));
		if(index > 0) {
			String ep = video.getName().substring(index+4, index+6);
			show.setEpisode(Integer.valueOf(ep));
			show.setEpisodeName(sanitizeName(FilenameUtils.getBaseName(video.getName().substring(index+6))));
		}

	}
	
	private static String buildSXXE(Integer season) {
		StringBuilder builder = new StringBuilder("S");
		if(season.intValue() < 10) {
			builder.append("0");
		}
		builder.append(season);
		builder.append("E");
		return builder.toString();
	}
	
	private static String sanitizeName(String name) {
		return StringUtils.replace(name,"."," ").trim();
	}
}
