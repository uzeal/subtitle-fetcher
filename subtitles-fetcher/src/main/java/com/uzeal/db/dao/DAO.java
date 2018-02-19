package com.uzeal.db.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DAO<T extends DTO> {
	private FieldStructure<T> fields;
	private String tableName;
	private String createStatement;
	private String insertStatemenet;
	private String updateStatement;
	private String deleteByIdStatement;
	private String selectByIdStatement;
	private String selectAllStatement;	
	
	DAO(FieldStructure<T> fields, String tableName, String createStatement, String insertStatemenet, String updateStatement,
			String deleteByIdStatement, String selectByIdStatement, String selectAllStatement) {
		super();
		this.fields = fields;
		this.tableName = tableName;
		this.createStatement = createStatement;
		this.insertStatemenet = insertStatemenet;
		this.updateStatement = updateStatement;
		this.deleteByIdStatement = deleteByIdStatement;
		this.selectByIdStatement = selectByIdStatement;
		this.selectAllStatement = selectAllStatement;
	}

	public void createTable(Connection connection) throws SQLException {
		try(PreparedStatement ps = connection.prepareStatement(createStatement)) {
			ps.execute();
		}
	}
	
	public void insert(Connection connection, T dto) throws Exception {
		try(PreparedStatement ps = connection.prepareStatement(insertStatemenet)) {
			fields.setInsertFields(ps, dto);
			ps.execute();
		}
	}
	
	public void update(Connection connection, T dto) throws Exception {
		try(PreparedStatement ps = connection.prepareStatement(updateStatement)) {
			fields.setUpdateFields(ps, dto);
			ps.execute();
		}
	}
	
	public void delete(Connection connection, T dto) throws Exception {
		try(PreparedStatement ps = connection.prepareStatement(deleteByIdStatement)) {
			fields.setIdFields(ps, dto);
			ps.execute();
		}
	}
	
	public List<T> selectAll(Connection connection) throws Exception {
		List<T> dto = new ArrayList<T>();
		try(PreparedStatement ps = connection.prepareStatement(selectAllStatement)) {
			try(ResultSet rs = ps.executeQuery()) {
				while(rs.next()) {
					T obj = fields.buildDTO(rs);
					dto.add(obj);
				}
			}
		}
		return dto;
	}
	
	public List<T> selectById(Connection connection, T input) throws Exception {
		List<T> dto = new ArrayList<T>();
		try(PreparedStatement ps = connection.prepareStatement(selectByIdStatement)) {
			fields.setIdFields(ps, input);
			try(ResultSet rs = ps.executeQuery()) {
				while(rs.next()) {
					T obj = fields.buildDTO(rs);
					dto.add(obj);
				}
			}
		}
		return dto;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("DAO [tableName=").append(tableName).append(", createStatement=").append(createStatement)
				.append(", insertStatemenet=").append(insertStatemenet).append(", updateStatement=")
				.append(updateStatement).append(", deleteByIdStatement=").append(deleteByIdStatement)
				.append(", selectByIdStatement=").append(selectByIdStatement).append(", selectAllStatement=")
				.append(selectAllStatement).append("]");
		return builder.toString();
	}
	
}
