package com.uzeal.db.dao;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.TreeMap;

public class FieldStructure<T> {
	private Class<T> dto;
	private List<DBField> fields;
	private TreeMap<Integer, DBField> primaryKey;
	private List<DBField> nonKey;
	
	FieldStructure(Class<T> dto) {
		this.dto = dto;
		fields = new ArrayList<DBField>();
		primaryKey = new TreeMap<Integer, DBField>();
		nonKey = new ArrayList<DBField>();
	}
	
	DBField addPrimaryKeyField(int order, Field field) throws Exception {
		DBField dbField = new DBField(field,getGetMethod(field),getSetMethod(field),DAOFactory.getDBName(field.getName()));
		primaryKey.put(order, dbField);
		fields.add(dbField);
		return dbField;
	}
	
	DBField addNonKeyField(Field field) throws Exception {
		DBField dbField = new DBField(field,getGetMethod(field),getSetMethod(field),DAOFactory.getDBName(field.getName()));
		fields.add(dbField);
		nonKey.add(dbField);
		return dbField;
	}
	
	T buildDTO(ResultSet rs) throws Exception {
		T dto = this.dto.newInstance();
		for(int i=0;i<fields.size();i++) {
			DBField field = fields.get(i);
			Object value = rs.getObject(i+1);
			field.getSetMethod().invoke(dto, value);
		}
		return dto;
	}
	
	void setInsertFields(PreparedStatement ps, T dto) throws Exception {
		int index = 1;
		for(DBField dbField : fields) {
			setField(ps, index++, dbField, dto);
		}
	}
	
	void setUpdateFields(PreparedStatement ps, T dto) throws Exception {
		int index = 1;
		for(DBField dbField : nonKey) {
			setField(ps, index++, dbField, dto);
		}
		setIdFields(ps, dto, index);
	}
	
	void setIdFields(PreparedStatement ps, T dto) throws Exception {
		int index = 1;
		setIdFields(ps, dto, index);
	}
	
	private void setIdFields(PreparedStatement ps, T dto, int index) throws Exception {
		for(Entry<Integer,DBField> entry : primaryKey.entrySet()) {
			setField(ps, index++, entry.getValue(), dto);
		}
	}
	
	private void setField(PreparedStatement ps, int index, DBField dbField, T dto) throws Exception {
		int sqlType = getType(dbField.getField().getType());
		Object value = dbField.getGetMethod().invoke(dto);
		if(value == null) {
			ps.setNull(index, sqlType);
		} else {
			if(sqlType == Types.VARCHAR) {
				ps.setString(index, (String)value);
			} else if(sqlType == Types.INTEGER) {
				ps.setInt(index, (Integer)value);
			} else if(sqlType == Types.BIGINT) {
				ps.setLong(index, (Long)value);
			} else if(sqlType == Types.BOOLEAN) {
				ps.setBoolean(index, (Boolean)value);
			} else if(sqlType == Types.DOUBLE) {
				ps.setDouble(index, (Double)value);
			}
		}
	}
	
	private int getType(Class<?> type) {
		int sqlType = 0 ;
		if(type.equals(String.class)) {
			sqlType = Types.VARCHAR;
		} else if(type.equals(int.class) || type.equals(Integer.class)) {
			sqlType = Types.INTEGER;
		} else if(type.equals(long.class) || type.equals(Long.class)) {
			sqlType = Types.BIGINT;
		} else if(type.equals(boolean.class) || type.equals(Boolean.class)) {
			sqlType = Types.BOOLEAN;
		} else if(type.equals(double.class) || type.equals(Double.class)) {
			sqlType = Types.DOUBLE;
		}
		return sqlType;
	}
	
	void buildAllFields(StringBuilder builder, String separator) {
		if(!fields.isEmpty()) {
			for(int i=0;i<fields.size();i++) {
				builder.append(fields.get(i).getDbName());
				if(fields.size() > 1 && i < fields.size() -1) {
					builder.append(separator);
				}
			}
		}
	}
	
	void buildNonKeyFields(StringBuilder builder, String preField, String postField, String separator) {
		for(int i=0;i<nonKey.size();i++) {
			builder.append(preField).append(nonKey.get(i).getDbName()).append(postField);
			if(nonKey.size() > 1 && i < nonKey.size() -1) {
				builder.append(separator);
			}
		}
	}
	
	void buildKeys(StringBuilder builder, String firstAppend, String endAppend, String separator, String withField) {
		if(!primaryKey.isEmpty()) {
			builder.append(firstAppend);
			Integer key = primaryKey.firstKey();
			Integer firstKey = key;
			builder.append(primaryKey.get(key).getDbName());
			if(withField != null) {
				builder.append(withField);
			}
			while(!key.equals(primaryKey.lastKey())) {
				builder.append(separator);
				key = primaryKey.higherKey(key);
				builder.append(primaryKey.get(key).getDbName());
				if(withField != null) {
					builder.append(withField);
				}
			}
			/*if(!key.equals(firstKey)) {
				builder.append(primaryKey.get(key).getDbName());
			}*/
			builder.append(endAppend);
		}
	}
	
	void buildParams(StringBuilder builder) {
		for(int i=0;i<fields.size();i++) {
			builder.append("?");
			if(fields.size() > 1 && i < fields.size() -1) {
				builder.append(", ");
			}
		}
	}
	
	private Method getGetMethod(Field field) throws Exception {
		String fieldName = field.getName();
		String methodName = fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
		if(field.getType().equals(boolean.class)) {
			methodName = "is"+methodName;
		} else {
			methodName = "get"+methodName;
		}
		return dto.getDeclaredMethod(methodName);
	}
	
	private Method getSetMethod(Field field) throws Exception {
		String fieldName = field.getName();
		String methodName = "set"+fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
		return dto.getDeclaredMethod(methodName, field.getType());
	}
}
