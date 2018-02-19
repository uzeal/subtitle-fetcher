package com.uzeal.db.connector;

public class SQLLightConnector extends DBConnector {
	private static final String urlBase = "jdbc:sqlite:";
	
	SQLLightConnector(String dbName) {
		super(dbName);
	}

	@Override
	protected String getURL() {
		return urlBase+dbName;
	}
}
