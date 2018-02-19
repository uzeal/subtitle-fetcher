package com.uzeal.db.connector;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public abstract class DBConnector {
	
	protected String dbName;
	private boolean init = false;
	
	protected DBConnector(String dbName) {
		this.dbName = dbName;
	}
	
	boolean isInitialized() {
		return init;
	}
	
	void initDB() throws SQLException  {
		File dbFile = new File(dbName);
		if(dbFile.exists()) {
			System.out.println(dbFile.getAbsolutePath()+" already exists. loading");
		} else {
			System.out.println("Creating: "+dbFile.getAbsolutePath());
		}
		try(Connection conn = DriverManager.getConnection(getURL())) {
			System.out.println("Connection to SQLite has been established.");
		}
		init = true;
	}
	
	protected abstract String getURL();
	
	Connection getConnection() throws SQLException {
		return DriverManager.getConnection(getURL());
	}
}
