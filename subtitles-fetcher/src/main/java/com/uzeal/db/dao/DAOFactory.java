package com.uzeal.db.dao;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

public class DAOFactory {
	private static final Pattern upperCaseSplit = Pattern.compile("(?=\\p{Upper})");
	private static final String CREATE = "create table ";
	private static final String INSERT = "insert into ";
	private static final String VALUES = " values ( ";
	private static Map<String,DAO<?>> daos = new HashMap<String,DAO<?>>();
	
	
	@SuppressWarnings("unchecked")
	public static <T extends DTO> DAO<T> getDao(Class<T> dto) throws Exception {
		DAO<T> dao = null;
		String name = dto.getName();
		DAO<?> d = daos.get(name);
		if(d == null) {
			String tableName = getTableName(name);
			FieldStructure<T> fieldStructure = new FieldStructure<T>(dto);
			String createStatement = getCreateStatement(tableName, dto, fieldStructure);
			String selectAllStmt = getSelectAllStatement(tableName, fieldStructure);
			dao = new DAO<T>(fieldStructure,
					tableName,
					createStatement,
					getInsertStatement(tableName, fieldStructure),
					getUpdateStatement(tableName, fieldStructure),
					getDeleteStatement(tableName, fieldStructure),
					getSelectByIdStatement(selectAllStmt,fieldStructure),
					selectAllStmt+";");
			daos.put(name, dao);
			System.out.println("Created dao : "+name+" "+dao.toString());
		} else {
			dao = (DAO<T>) d;
		}
		return dao;
	}
	
	private static <T extends DTO> String getSelectByIdStatement(String selectAllSql, FieldStructure<T> fieldStructure) {
		StringBuilder builder = new StringBuilder(selectAllSql);				
		fieldStructure.buildKeys(builder, " where ", ";", " and ", " = ?");		
		return builder.toString();
	}
	
	private static <T extends DTO> String getSelectAllStatement(String tableName, FieldStructure<T> fieldStructure) {
		StringBuilder builder = new StringBuilder("select ");				
		fieldStructure.buildAllFields(builder,",");
		builder.append(" from ").append(tableName);		
		return builder.toString();
	}
	
	private static <T extends DTO> String getDeleteStatement(String tableName, FieldStructure<T> fieldStructure) {
		StringBuilder builder = new StringBuilder("delete from ");
		builder.append(tableName);
		fieldStructure.buildKeys(builder, " where ", ";", " and ", " = ?");			
		return builder.toString();
	}
	
	private static <T extends DTO> String getUpdateStatement(String tableName, FieldStructure<T> fieldStructure) {
		StringBuilder builder = new StringBuilder("update ");
		builder.append(tableName);
		fieldStructure.buildNonKeyFields(builder, " set ", " = ?", ",");
		fieldStructure.buildKeys(builder, " where ", ";", " and ", " = ?");	
		return builder.toString();
	}
	
	private static <T extends DTO> String getInsertStatement(String tableName, FieldStructure<T> fieldStructure) {
		StringBuilder builder = new StringBuilder(INSERT);
		builder.append(tableName).append(VALUES);
		fieldStructure.buildParams(builder);
		builder.append(" );");
		
		return builder.toString();
	}
	
	private static <T extends DTO> String getCreateStatement(String tableName, Class<T> dtoClass, FieldStructure<T> fieldStructure) throws Exception {
		StringBuilder builder = new StringBuilder(CREATE);
		builder.append(tableName).append(" (");
		Field[] fields = dtoClass.getDeclaredFields();
		for(int i=0;i<fields.length;i++) {
			if(!Modifier.isStatic(fields[i].getModifiers()) && !Modifier.isFinal(fields[i].getModifiers())) {
				appendFieldDefinition(builder, fields[i], fieldStructure);
				if(fields.length > 1 && i < fields.length -1) {
					builder.append(",");
				}
			}			
		}
		fieldStructure.buildKeys(builder, ",primary key(", "));", ",", null);
		
		return builder.toString();
	}
	
	private static <T extends DTO> void appendFieldDefinition(StringBuilder builder, Field field, FieldStructure<T> fieldStructure) throws Exception {
		Key key = field.getAnnotation(Key.class);
		DBField dbField = null;
		if(key != null) {
			dbField = fieldStructure.addPrimaryKeyField(key.order(), field);
		} else {
			dbField = fieldStructure.addNonKeyField(field);
		}
		builder.append(dbField.getDbName()).append(" ");
		Class<?> type = field.getType();
		if(type.equals(String.class)) {
			builder.append("varchar ");
		} else if(type.equals(int.class) || type.equals(Integer.class)) {
			builder.append("int ");
		} else if(type.equals(long.class) || type.equals(Long.class)) {
			builder.append("bigint ");
		} else if(type.equals(boolean.class) || type.equals(Boolean.class)) {
			builder.append("bool ");
		}
		
		
	}
	
	private static String getTableName(String className) {
		int dot = StringUtils.lastIndexOf(className,".");
		String name = StringUtils.substring(className, dot+1, className.length());
		return getDBName(name);
	}
	
	static String getDBName(String str) {
		String[] parts = upperCaseSplit.split(str);
		StringBuilder builder = new StringBuilder();
		for(int i = 0;i<parts.length;i++) {
			builder.append(parts[i].toLowerCase());
			if(parts.length > 1 && i == parts.length -2) {
				builder.append("_");
			}
		}
		return builder.toString();
	}
}
