package com.uzeal.db.connector;

import java.sql.Connection;

public class DBUtil {
	private static DBConnector connector;
	private static String dbName;
	
	public static void setDbName(final String dbName) {
		DBUtil.dbName = dbName;
	}
	
	private static void initDefaultDB() throws Exception {
		if(connector == null) {
			if(dbName == null) {
				throw new Exception("Missing DB name");
			}
			connector = DBConnectorFactory
					.createConnector(DBTypes.SQLLITE)
					.setDbName(DBUtil.dbName)
					.build();
			System.out.println("DBConnector has been created");
			if(!connector.isInitialized()) {
				connector.initDB();
			}
		}
	}
	
	public static Connection getConnection() throws Exception {
		initDefaultDB();
		return connector.getConnection();
	}
	
	public static void close() {
		connector = null;
	}
}
