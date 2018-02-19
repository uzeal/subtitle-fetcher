package com.uzeal.subtitles.util;

import java.io.File;
import java.sql.Connection;

import com.uzeal.db.connector.DBUtil;
import com.uzeal.db.dao.DAO;
import com.uzeal.db.dao.DAOFactory;
import com.uzeal.subtitles.db.dto.Show;
import com.uzeal.subtitles.db.dto.Video;

public class Initializer {
	
	public static void initDB() throws Exception {
		DBUtil.setDbName("subtitle-fetcher.db");
		File dbFile = new File("subtitle-fetcher.db"); //TODO path
		if(!dbFile.exists()) {
			DAO<Show> showDao = DAOFactory.getDao(Show.class);
			DAO<Video> videoDao = DAOFactory.getDao(Video.class);
			try(Connection conn = DBUtil.getConnection()) {
				showDao.createTable(conn);
				videoDao.createTable(conn);
			}
		}
	}
}
