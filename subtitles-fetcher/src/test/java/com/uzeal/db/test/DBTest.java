package com.uzeal.db.test;

import java.io.File;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.uzeal.db.connector.DBUtil;
import com.uzeal.db.dao.DAO;
import com.uzeal.db.dao.DAOFactory;
import com.uzeal.db.test.dto.Example;

public class DBTest {
	
	@Test
	public void testDefaultDB() throws Exception {
		try(Connection conn = DBUtil.getConnection()) {
			//throws mising db error
		} catch(Exception e) {
			Assert.assertEquals("Missing DB name",e.getMessage());
		}
		
		File file = new File("test.db");
		file.createNewFile();
		
		DBUtil.setDbName("test.db");
		try(Connection conn = DBUtil.getConnection()) {
			DatabaseMetaData meta = conn.getMetaData();
            System.out.println("The driver name is " + meta.getDriverName());
		}
		
		file.delete();
		DBUtil.close();
		
		try(Connection conn = DBUtil.getConnection()) {
			DatabaseMetaData meta = conn.getMetaData();
		}
		
		System.out.println("Deleting test.db");
		file.delete();
	}
	
	@Test
	public void testTableCreate() throws Exception {
		File file = new File("test.db");
		DAOFactory.getDao(Example.class);
		DBUtil.setDbName("test.db");
		try(Connection conn = DBUtil.getConnection()) {
			DAO<Example> example = DAOFactory.getDao(Example.class);
			example.createTable(conn);
			Example ex = new Example();
			ex.setId(1);
			ex.setTest("Test 1");
			example.insert(conn, ex);
			List<Example> examples = example.selectAll(conn);
			Assert.assertEquals(ex, examples.get(0));
			ex.setTest("asdf");
			example.update(conn, ex);
			examples = example.selectById(conn,ex);
			Assert.assertEquals(ex, examples.get(0));
			example.delete(conn, ex);
			examples = example.selectAll(conn);
			Assert.assertEquals(0, examples.size());
		} finally {
			System.out.println("Deleting test.db");
			file.delete();
		}
	}
}
