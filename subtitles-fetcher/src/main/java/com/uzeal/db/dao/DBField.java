package com.uzeal.db.dao;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class DBField {
	private Field field;
	private Method getMethod;
	private Method setMethod;
	private String dbName;
	
	public DBField() { }

	public DBField(Field field, Method getMethod, Method setMethod, String dbName) {
		super();
		this.field = field;
		this.getMethod = getMethod;
		this.setMethod = setMethod;
		this.dbName = dbName;
	}

	public Field getField() {
		return field;
	}

	public void setField(Field field) {
		this.field = field;
	}

	public String getDbName() {
		return dbName;
	}

	public void setDbName(String dbName) {
		this.dbName = dbName;
	}

	public Method getGetMethod() {
		return getMethod;
	}

	public void setGetMethod(Method getMethod) {
		this.getMethod = getMethod;
	}

	public Method getSetMethod() {
		return setMethod;
	}

	public void setSetMethod(Method setMethod) {
		this.setMethod = setMethod;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("DBField [field=").append(field).append(", dbName=").append(dbName).append("]");
		return builder.toString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((dbName == null) ? 0 : dbName.hashCode());
		result = prime * result + ((field == null) ? 0 : field.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DBField other = (DBField) obj;
		if (dbName == null) {
			if (other.dbName != null)
				return false;
		} else if (!dbName.equals(other.dbName))
			return false;
		if (field == null) {
			if (other.field != null)
				return false;
		} else if (!field.equals(other.field))
			return false;
		return true;
	}
}
