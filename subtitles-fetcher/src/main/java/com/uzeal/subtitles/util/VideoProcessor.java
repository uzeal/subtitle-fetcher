package com.uzeal.subtitles.util;

import java.io.File;
import java.sql.Connection;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.commons.lang3.StringUtils;

import com.uzeal.db.connector.DBUtil;
import com.uzeal.db.dao.DAO;
import com.uzeal.db.dao.DAOFactory;
import com.uzeal.subtitles.db.dto.Show;

public class VideoProcessor {
	private static final String[] unwantedChars = {" ",".","the"};
	private static final String[] replaceChars = {"_","_"};
	
	
	public static void process(File video) {
		AtomicBoolean movie = new AtomicBoolean(true);
		String showName = getShowName(video, movie);
		try {
			Show show = getOrAddShow(showName, movie);
			if(show != null) {
				//get or add video, compute hash, get subtitle etc
			} else {
				System.out.println("Could not process: "+video.getAbsolutePath());
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		
	}
	
	private static Show getOrAddShow(String showName, AtomicBoolean movie) throws Exception {
		Show show = null;
		DAO<Show> showDao = DAOFactory.getDao(Show.class);
		try(Connection connection = DBUtil.getConnection()) {
			show = new Show();
			show.setName(showName);
			show.setMovie(movie.get());
			List<Show> showlist = showDao.selectById(connection, show);
			if(showlist.isEmpty()) {
				showDao.insert(connection, show);
			}
		}
		return show;
	}
	
	
	private static String getShowName(File video, AtomicBoolean movie) {
		File show = video.getParentFile();
		if(show.getName().toUpperCase().startsWith("SEASON")) {
			movie.set(false);
			show = show.getParentFile();
		}
		return StringUtils.replaceEach(show.getName().toLowerCase(), unwantedChars, replaceChars);
	}
 }
