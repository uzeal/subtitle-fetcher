package com.uzeal.db.connector;

public class DBConnectorFactory {
	
	private DBTypes type;
	private String dbName;
	
	private DBConnectorFactory(DBTypes dbType) {
		this.type = dbType;
	}
	
	DBConnector build() {
		DBConnector connector = null;
		if(type == DBTypes.SQLLITE) {
			connector = new SQLLightConnector(dbName);
		}
		return connector;
	}
	
	static DBConnectorFactory createConnector(DBTypes dbType) {
		return new DBConnectorFactory(dbType);
	}
	
	DBConnectorFactory setDbName(String fullName) {
		this.dbName = fullName;
		return this;
	}
}
